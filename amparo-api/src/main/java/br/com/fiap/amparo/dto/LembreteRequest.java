package br.com.fiap.amparo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record LembreteRequest(
        @NotBlank(message = "titulo e obrigatorio") @Size(max = 100)
        String titulo,

        @Size(max = 255)
        String descricao,

        @NotNull(message = "horario e obrigatorio")
        LocalTime horario,

        Boolean ativo,

        @NotNull(message = "cidadaoId e obrigatorio")
        Long cidadaoId
) {}
