package br.com.fiap.amparo.service;

import br.com.fiap.amparo.dto.CidadaoFiltro;
import br.com.fiap.amparo.dto.CidadaoRequest;
import br.com.fiap.amparo.dto.CidadaoResponse;
import br.com.fiap.amparo.exception.ResourceNotFoundException;
import br.com.fiap.amparo.model.Cidadao;
import br.com.fiap.amparo.repository.CidadaoRepository;
import br.com.fiap.amparo.specification.CidadaoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CidadaoService {

    private final CidadaoRepository repository;

    /** Busca com filtros dinamicos (Specification): so entra no WHERE o que foi informado. */
    @Transactional(readOnly = true)
    public Page<CidadaoResponse> listar(CidadaoFiltro filtro, Pageable pageable) {
        Specification<Cidadao> spec = Specification
                .where(CidadaoSpecification.nomeContem(filtro.nome()))
                .and(CidadaoSpecification.condicaoContem(filtro.condicao()))
                .and(CidadaoSpecification.ativo(filtro.ativo()));
        return repository.findAll(spec, pageable).map(CidadaoResponse::from);
    }

    /** Query method derivado: busca simples por nome. */
    @Transactional(readOnly = true)
    public Page<CidadaoResponse> buscarPorNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable).map(CidadaoResponse::from);
    }

    /** Cache por id: o app abre a tela do cidadao muitas vezes e o cadastro muda pouco. */
    @Cacheable(value = "cidadaos", key = "#id")
    @Transactional(readOnly = true)
    public CidadaoResponse buscar(Long id) {
        return CidadaoResponse.from(obter(id));
    }

    @Transactional
    public CidadaoResponse criar(CidadaoRequest req) {
        Cidadao c = new Cidadao();
        aplicar(c, req);
        return CidadaoResponse.from(repository.save(c));
    }

    /** Escrita invalida o cache daquele id. */
    @CacheEvict(value = "cidadaos", key = "#id")
    @Transactional
    public CidadaoResponse atualizar(Long id, CidadaoRequest req) {
        Cidadao c = obter(id);
        aplicar(c, req);
        return CidadaoResponse.from(repository.save(c));
    }

    /** Excluir remove o cidadao e, em cascata, cuidadores e lembretes: limpa os tres caches. */
    @Caching(evict = {
            @CacheEvict(value = "cidadaos", key = "#id"),
            @CacheEvict(value = "cuidadoresPorCidadao", key = "#id"),
            @CacheEvict(value = "lembretesPorCidadao", key = "#id")
    })
    @Transactional
    public void excluir(Long id) {
        repository.delete(obter(id));
    }

    /** Usado pelos outros services para resolver o relacionamento. */
    @Transactional(readOnly = true)
    public Cidadao obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cidadao", id));
    }

    private void aplicar(Cidadao c, CidadaoRequest req) {
        c.setNome(req.nome());
        c.setDataNascimento(req.dataNascimento());
        c.setTelefone(req.telefone());
        c.setEndereco(req.endereco());
        c.setCondicao(req.condicao());
        c.setAtivo(req.ativo() != null ? req.ativo() : Boolean.TRUE);
    }
}
