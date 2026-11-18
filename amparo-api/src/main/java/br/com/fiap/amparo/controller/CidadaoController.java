package br.com.fiap.amparo.controller;

import br.com.fiap.amparo.dto.*;
import br.com.fiap.amparo.hateoas.CidadaoModelAssembler;
import br.com.fiap.amparo.hateoas.EventoModelAssembler;
import org.springframework.hateoas.EntityModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.com.fiap.amparo.service.CuidadorService;
import br.com.fiap.amparo.service.EventoService;
import br.com.fiap.amparo.dto.CidadaoRequest;
import br.com.fiap.amparo.dto.CidadaoResponse;
import br.com.fiap.amparo.service.CidadaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cidadaos")
@RequiredArgsConstructor
@Tag(name = "Cidadaos", description = "Pessoas assistidas pela rede de cuidado (idosos e PcD)")
public class CidadaoController {

    private final CidadaoService service;
    private final CuidadorService cuidadorService;
    private final EventoService eventoService;
    private final CidadaoModelAssembler assembler;
    private final EventoModelAssembler eventoAssembler;

    /** GET /cidadaos?nome=&condicao=&ativo=&page=&size=&sort= (filtros via Specification) */
    @Operation(summary = "Lista cidadaos com filtros opcionais (nome, condicao, ativo), paginado")
    @GetMapping
    public Page<EntityModel<CidadaoResponse>> listar(
            CidadaoFiltro filtro,
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.listar(filtro, pageable).map(assembler::toModel);
    }

    /** Cadeia de cuidadores do cidadao, em ordem de prioridade. */
    @GetMapping("/{id}/cuidadores")
    public List<CuidadorResponse> cuidadores(@PathVariable Long id) {
        return cuidadorService.listarPorCidadao(id);
    }

    /** Historico de eventos do cidadao (@Query JPQL). */
    @GetMapping("/{id}/eventos")
    public Page<EntityModel<EventoResponse>> eventos(@PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable) {
        return eventoService.listarPorCidadao(id, pageable).map(eventoAssembler::toModel);
    }

    @Operation(summary = "Busca cidadao por id (cacheado)")
    @GetMapping("/{id}")
    public EntityModel<CidadaoResponse> buscar(@PathVariable Long id) {
        return assembler.toModel(service.buscar(id));
    }

    @Operation(summary = "Cadastra cidadao (ADMIN)")
    @PostMapping
    public ResponseEntity<EntityModel<CidadaoResponse>> criar(@RequestBody @Valid CidadaoRequest req) {
        CidadaoResponse criado = service.criar(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(assembler.toModel(criado));
    }

    @Operation(summary = "Atualiza cidadao (ADMIN)")
    @PutMapping("/{id}")
    public EntityModel<CidadaoResponse> atualizar(@PathVariable Long id, @RequestBody @Valid CidadaoRequest req) {
        return assembler.toModel(service.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
