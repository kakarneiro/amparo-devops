package br.com.fiap.amparo.dto;

import br.com.fiap.amparo.model.ResultadoAtendimento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtendimentoRequest(
        @NotNull(message = "eventoId e obrigatorio")
        Long eventoId,

        @NotNull(message = "cuidadorId e obrigatorio")
        Long cuidadorId,

        ResultadoAtendimento resultado,

        @Size(max = 255)
        String observacao
) {}
