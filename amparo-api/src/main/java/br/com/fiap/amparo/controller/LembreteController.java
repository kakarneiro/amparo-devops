package br.com.fiap.amparo.controller;

import br.com.fiap.amparo.dto.LembreteRequest;
import br.com.fiap.amparo.dto.LembreteResponse;
import br.com.fiap.amparo.service.LembreteService;
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
@RequestMapping("/lembretes")
@RequiredArgsConstructor
public class LembreteController {

    private final LembreteService service;

    @GetMapping
    public Page<LembreteResponse> listar(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.listar(pageable);
    }

    /** Lembretes ativos de um cidadao (cacheado). */
    @GetMapping("/cidadao/{cidadaoId}")
    public List<LembreteResponse> ativosPorCidadao(@PathVariable Long cidadaoId) {
        return service.listarAtivosPorCidadao(cidadaoId);
    }

    @GetMapping("/{id}")
    public LembreteResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<LembreteResponse> criar(@RequestBody @Valid LembreteRequest req) {
        LembreteResponse criado = service.criar(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public LembreteResponse atualizar(@PathVariable Long id, @RequestBody @Valid LembreteRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
