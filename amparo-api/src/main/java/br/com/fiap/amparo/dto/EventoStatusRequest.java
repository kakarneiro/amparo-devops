package br.com.fiap.amparo.dto;

import br.com.fiap.amparo.model.StatusEvento;
import jakarta.validation.constraints.NotNull;

public record EventoStatusRequest(
        @NotNull(message = "status e obrigatorio")
        StatusEvento status
) {}
