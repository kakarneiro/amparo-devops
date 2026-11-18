package br.com.fiap.amparo.service;

import br.com.fiap.amparo.dto.AtendimentoRequest;
import br.com.fiap.amparo.dto.AtendimentoResponse;
import br.com.fiap.amparo.exception.ResourceNotFoundException;
import br.com.fiap.amparo.model.Atendimento;
import br.com.fiap.amparo.model.ResultadoAtendimento;
import br.com.fiap.amparo.repository.AtendimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AtendimentoService {

    private final AtendimentoRepository repository;
    private final EventoService eventoService;
    private final CuidadorService cuidadorService;

    @Transactional(readOnly = true)
    public Page<AtendimentoResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(AtendimentoResponse::from);
    }

    /** Linha do tempo de acionamentos de um evento. */
    @Transactional(readOnly = true)
    public List<AtendimentoResponse> listarPorEvento(Long eventoId) {
        eventoService.obter(eventoId);
        return repository.findByEventoIdOrderByDataAcionamentoAsc(eventoId)
                .stream().map(AtendimentoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AtendimentoResponse buscar(Long id) {
        return AtendimentoResponse.from(obter(id));
    }

    @Transactional
    public AtendimentoResponse criar(AtendimentoRequest req) {
        Atendimento a = new Atendimento();
        aplicar(a, req);
        return AtendimentoResponse.from(repository.save(a));
    }

    @Transactional
    public AtendimentoResponse atualizar(Long id, AtendimentoRequest req) {
        Atendimento a = obter(id);
        aplicar(a, req);
        return AtendimentoResponse.from(repository.save(a));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(obter(id));
    }

    @Transactional(readOnly = true)
    public Atendimento obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atendimento", id));
    }

    private void aplicar(Atendimento a, AtendimentoRequest req) {
        a.setEvento(eventoService.obter(req.eventoId()));
        a.setCuidador(cuidadorService.obter(req.cuidadorId()));
        a.setObservacao(req.observacao());

        ResultadoAtendimento resultado = req.resultado() != null ? req.resultado() : ResultadoAtendimento.PENDENTE;
        a.setResultado(resultado);
        // Quando o cuidador responde (qualquer resultado diferente de PENDENTE), marca a hora
        if (resultado != ResultadoAtendimento.PENDENTE && a.getDataResposta() == null) {
            a.setDataResposta(LocalDateTime.now());
        }
    }
}
