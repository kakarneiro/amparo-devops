package br.com.fiap.amparo.service;

import br.com.fiap.amparo.dto.LembreteRequest;
import br.com.fiap.amparo.dto.LembreteResponse;
import br.com.fiap.amparo.exception.ResourceNotFoundException;
import br.com.fiap.amparo.model.Lembrete;
import br.com.fiap.amparo.repository.LembreteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LembreteService {

    private final LembreteRepository repository;
    private final CidadaoService cidadaoService;

    @Transactional(readOnly = true)
    public Page<LembreteResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(LembreteResponse::from);
    }

    /** Lembretes ativos do cidadao, na ordem do dia. Cacheado: a home do app le isso sempre. */
    @Cacheable(value = "lembretesPorCidadao", key = "#cidadaoId")
    @Transactional(readOnly = true)
    public List<LembreteResponse> listarAtivosPorCidadao(Long cidadaoId) {
        cidadaoService.obter(cidadaoId);
        return repository.findByCidadaoIdAndAtivoTrueOrderByHorarioAsc(cidadaoId)
                .stream().map(LembreteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public LembreteResponse buscar(Long id) {
        return LembreteResponse.from(obter(id));
    }

    @CacheEvict(value = "lembretesPorCidadao", key = "#req.cidadaoId()")
    @Transactional
    public LembreteResponse criar(LembreteRequest req) {
        Lembrete l = new Lembrete();
        aplicar(l, req);
        return LembreteResponse.from(repository.save(l));
    }

    @CacheEvict(value = "lembretesPorCidadao", allEntries = true)
    @Transactional
    public LembreteResponse atualizar(Long id, LembreteRequest req) {
        Lembrete l = obter(id);
        aplicar(l, req);
        return LembreteResponse.from(repository.save(l));
    }

    @CacheEvict(value = "lembretesPorCidadao", allEntries = true)
    @Transactional
    public void excluir(Long id) {
        repository.delete(obter(id));
    }

    @Transactional(readOnly = true)
    public Lembrete obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lembrete", id));
    }

    private void aplicar(Lembrete l, LembreteRequest req) {
        l.setTitulo(req.titulo());
        l.setDescricao(req.descricao());
        l.setHorario(req.horario());
        l.setAtivo(req.ativo() != null ? req.ativo() : Boolean.TRUE);
        l.setCidadao(cidadaoService.obter(req.cidadaoId()));
    }
}
