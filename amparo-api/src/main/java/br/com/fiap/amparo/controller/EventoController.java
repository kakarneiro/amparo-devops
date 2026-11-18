package br.com.fiap.amparo.controller;

import br.com.fiap.amparo.dto.AtendimentoResponse;
import br.com.fiap.amparo.hateoas.EventoModelAssembler;
import org.springframework.hateoas.EntityModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.com.fiap.amparo.dto.EventoFiltro;
import br.com.fiap.amparo.dto.EventoRequest;
import br.com.fiap.amparo.dto.EventoResponse;
import br.com.fiap.amparo.dto.EventoStatusRequest;
import br.com.fiap.amparo.service.AtendimentoService;
import br.com.fiap.amparo.service.EventoService;
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
@RequestMapping("/eventos")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "Ocorrencias enviadas pelo dispositivo e seu atendimento")
public class EventoController {

    private final EventoService service;
    private final AtendimentoService atendimentoService;
    private final EventoModelAssembler assembler;

    /** GET /eventos?status=&tipo=&cidadaoId=&inicio=&fim=&page=&size=&sort= */
    @Operation(summary = "Lista eventos com filtros (status, tipo, cidadaoId, inicio, fim), paginado")
    @GetMapping
    public Page<EntityModel<EventoResponse>> listar(
            EventoFiltro filtro,
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(filtro, pageable).map(assembler::toModel);
    }

    /** Linha do tempo de acionamentos do evento. */
    @GetMapping("/{id}/atendimentos")
    public List<AtendimentoResponse> atendimentos(@PathVariable Long id) {
        return atendimentoService.listarPorEvento(id);
    }

    /** Cuidador atual nao respondeu: aciona o proximo da cadeia. */
    @Operation(summary = "Escala o evento para o proximo cuidador da cadeia de prioridade")
    @PostMapping("/{id}/escalar")
    public EntityModel<EventoResponse> escalar(@PathVariable Long id) {
        return assembler.toModel(service.escalar(id));
    }

    @GetMapping("/{id}")
    public EntityModel<EventoResponse> buscar(@PathVariable Long id) {
        return assembler.toModel(service.buscar(id));
    }

    /** Endpoint que o ESP32 chama. */
    @Operation(summary = "Registra evento vindo do dispositivo (ESP32) e aciona o cuidador de prioridade 1",
            description = "Nao usa JWT: exige o header X-Device-Token.",
            security = @SecurityRequirement(name = "deviceToken"))
    @Parameter(name = "X-Device-Token", in = ParameterIn.HEADER, required = true, description = "Token fixo do dispositivo")
    @PostMapping
    public ResponseEntity<EntityModel<EventoResponse>> registrar(@RequestBody @Valid EventoRequest req) {
        EventoResponse criado = service.registrar(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(assembler.toModel(criado));
    }

    @PutMapping("/{id}")
    public EntityModel<EventoResponse> atualizar(@PathVariable Long id, @RequestBody @Valid EventoRequest req) {
        return assembler.toModel(service.atualizar(id, req));
    }

    @Operation(summary = "Altera o status do evento (RESOLVIDO, FALSO_ALARME...)")
    @PatchMapping("/{id}/status")
    public EntityModel<EventoResponse> alterarStatus(@PathVariable Long id, @RequestBody @Valid EventoStatusRequest req) {
        return assembler.toModel(service.alterarStatus(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
