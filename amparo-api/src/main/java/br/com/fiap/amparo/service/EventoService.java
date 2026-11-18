package br.com.fiap.amparo.service;

import br.com.fiap.amparo.dto.EventoFiltro;
import br.com.fiap.amparo.dto.EventoRequest;
import br.com.fiap.amparo.dto.EventoResponse;
import br.com.fiap.amparo.dto.EventoStatusRequest;
import br.com.fiap.amparo.exception.BusinessException;
import br.com.fiap.amparo.exception.ResourceNotFoundException;
import br.com.fiap.amparo.model.*;
import br.com.fiap.amparo.repository.AtendimentoRepository;
import br.com.fiap.amparo.repository.CuidadorRepository;
import br.com.fiap.amparo.repository.EventoRepository;
import br.com.fiap.amparo.specification.EventoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository repository;
    private final DispositivoService dispositivoService;
    private final CuidadorRepository cuidadorRepository;
    private final AtendimentoRepository atendimentoRepository;

    /** Filtros dinamicos via Specification. */
    @Transactional(readOnly = true)
    public Page<EventoResponse> listar(EventoFiltro filtro, Pageable pageable) {
        Specification<Evento> spec = Specification
                .where(EventoSpecification.status(filtro.status()))
                .and(EventoSpecification.tipo(filtro.tipo()))
                .and(EventoSpecification.cidadao(filtro.cidadaoId()))
                .and(EventoSpecification.aPartirDe(filtro.inicio()))
                .and(EventoSpecification.ate(filtro.fim()));
        return repository.findAll(spec, pageable).map(EventoResponse::from);
    }

    /** @Query JPQL: historico de um cidadao. */
    @Transactional(readOnly = true)
    public Page<EventoResponse> listarPorCidadao(Long cidadaoId, Pageable pageable) {
        return repository.buscarPorCidadao(cidadaoId, pageable).map(EventoResponse::from);
    }

    @Transactional(readOnly = true)
    public EventoResponse buscar(Long id) {
        return EventoResponse.from(obter(id));
    }

    /**
     * REGRA DE NEGOCIO 1 - acionamento automatico.
     * Chamado pelo ESP32. Ao registrar o evento, a API ja aciona o cuidador
     * de prioridade 1 do cidadao e marca o evento como EM_ATENDIMENTO.
     * Eventos do tipo TESTE e BATERIA_BAIXA nao acionam ninguem.
     */
    @Transactional
    public EventoResponse registrar(EventoRequest req) {
        Dispositivo dispositivo = dispositivoService.obterPorSerial(req.codigoSerial());
        if (!Boolean.TRUE.equals(dispositivo.getAtivo())) {
            throw new BusinessException("Dispositivo " + req.codigoSerial() + " esta inativo");
        }

        Evento evento = Evento.builder()
                .tipo(req.tipo())
                .status(StatusEvento.ABERTO)
                .dataHora(LocalDateTime.now())
                .descricao(req.descricao())
                .latitude(req.latitude())
                .longitude(req.longitude())
                .dispositivo(dispositivo)
                .build();
        evento = repository.save(evento);

        if (req.tipo().isEmergencia()) {
            acionarProximoCuidador(evento);
        }
        return EventoResponse.from(evento);
    }

    /**
     * REGRA DE NEGOCIO 2 - escalonamento.
     * O cuidador atual nao respondeu: marca o atendimento pendente como
     * SEM_RESPOSTA e aciona o proximo da cadeia de prioridade.
     * Se nao houver mais ninguem, o evento fica ABERTO para a central.
     */
    @Transactional
    public EventoResponse escalar(Long id) {
        Evento evento = obter(id);
        if (evento.getStatus() == StatusEvento.RESOLVIDO || evento.getStatus() == StatusEvento.FALSO_ALARME) {
            throw new BusinessException("Evento " + id + " ja esta encerrado (" + evento.getStatus() + ")");
        }

        atendimentoRepository.findFirstByEventoIdAndResultado(id, ResultadoAtendimento.PENDENTE)
                .ifPresent(pendente -> {
                    pendente.setResultado(ResultadoAtendimento.SEM_RESPOSTA);
                    pendente.setDataResposta(LocalDateTime.now());
                    atendimentoRepository.save(pendente);
                });

        acionarProximoCuidador(evento);
        return EventoResponse.from(evento);
    }

    private void acionarProximoCuidador(Evento evento) {
        Long cidadaoId = evento.getDispositivo().getCidadao().getId();
        List<Cuidador> cadeia = cuidadorRepository.findByCidadaoIdOrderByPrioridadeAsc(cidadaoId);
        long jaAcionados = atendimentoRepository.countByEventoId(evento.getId());

        if (jaAcionados >= cadeia.size()) {
            // Cadeia esgotada: ninguem respondeu, evento volta para ABERTO (central/emergencia)
            evento.setStatus(StatusEvento.ABERTO);
            repository.save(evento);
            return;
        }

        Cuidador proximo = cadeia.get((int) jaAcionados);
        Atendimento atendimento = Atendimento.builder()
                .evento(evento)
                .cuidador(proximo)
                .dataAcionamento(LocalDateTime.now())
                .resultado(ResultadoAtendimento.PENDENTE)
                .build();
        atendimentoRepository.save(atendimento);

        evento.setStatus(StatusEvento.EM_ATENDIMENTO);
        repository.save(evento);
    }

    @Transactional
    public EventoResponse atualizar(Long id, EventoRequest req) {
        Evento e = obter(id);
        e.setTipo(req.tipo());
        e.setDescricao(req.descricao());
        e.setLatitude(req.latitude());
        e.setLongitude(req.longitude());
        e.setDispositivo(dispositivoService.obterPorSerial(req.codigoSerial()));
        return EventoResponse.from(repository.save(e));
    }

    @Transactional
    public EventoResponse alterarStatus(Long id, EventoStatusRequest req) {
        Evento e = obter(id);
        e.setStatus(req.status());
        return EventoResponse.from(repository.save(e));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(obter(id));
    }

    @Transactional(readOnly = true)
    public Evento obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
    }
}
