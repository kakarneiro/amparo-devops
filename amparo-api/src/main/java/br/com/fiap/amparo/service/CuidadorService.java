package br.com.fiap.amparo.service;

import br.com.fiap.amparo.dto.CuidadorRequest;
import br.com.fiap.amparo.dto.CuidadorResponse;
import br.com.fiap.amparo.exception.ResourceNotFoundException;
import br.com.fiap.amparo.model.Cuidador;
import br.com.fiap.amparo.repository.CuidadorRepository;
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
public class CuidadorService {

    private final CuidadorRepository repository;
    private final CidadaoService cidadaoService;

    @Transactional(readOnly = true)
    public Page<CuidadorResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(CuidadorResponse::from);
    }

    /**
     * Cadeia de acionamento do cidadao, em ordem de prioridade.
     * Cacheada: e consultada a cada emergencia e precisa responder rapido.
     */
    @Cacheable(value = "cuidadoresPorCidadao", key = "#cidadaoId")
    @Transactional(readOnly = true)
    public List<CuidadorResponse> listarPorCidadao(Long cidadaoId) {
        cidadaoService.obter(cidadaoId); // garante 404 se o cidadao nao existir
        return repository.findByCidadaoIdOrderByPrioridadeAsc(cidadaoId)
                .stream().map(CuidadorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CuidadorResponse buscar(Long id) {
        return CuidadorResponse.from(obter(id));
    }

    /** Qualquer escrita em cuidador muda a cadeia: invalida o cache do cidadao dono. */
    @CacheEvict(value = "cuidadoresPorCidadao", key = "#req.cidadaoId()")
    @Transactional
    public CuidadorResponse criar(CuidadorRequest req) {
        Cuidador c = new Cuidador();
        aplicar(c, req);
        return CuidadorResponse.from(repository.save(c));
    }

    @CacheEvict(value = "cuidadoresPorCidadao", allEntries = true)
    @Transactional
    public CuidadorResponse atualizar(Long id, CuidadorRequest req) {
        Cuidador c = obter(id);
        aplicar(c, req);
        return CuidadorResponse.from(repository.save(c));
    }

    @CacheEvict(value = "cuidadoresPorCidadao", allEntries = true)
    @Transactional
    public void excluir(Long id) {
        repository.delete(obter(id));
    }

    @Transactional(readOnly = true)
    public Cuidador obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuidador", id));
    }

    private void aplicar(Cuidador c, CuidadorRequest req) {
        c.setNome(req.nome());
        c.setTelefone(req.telefone());
        c.setEmail(req.email());
        c.setParentesco(req.parentesco());
        c.setPrioridade(req.prioridade());
        c.setCidadao(cidadaoService.obter(req.cidadaoId()));
    }
}
