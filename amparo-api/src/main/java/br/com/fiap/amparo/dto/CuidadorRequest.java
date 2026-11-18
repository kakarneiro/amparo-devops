package br.com.fiap.amparo.dto;

import jakarta.validation.constraints.*;

public record CuidadorRequest(
        @NotBlank(message = "nome e obrigatorio") @Size(max = 120)
        String nome,

        @NotBlank(message = "telefone e obrigatorio") @Size(max = 20)
        String telefone,

        @Email(message = "email invalido") @Size(max = 120)
        String email,

        @Size(max = 60)
        String parentesco,

        @NotNull(message = "prioridade e obrigatoria")
        @Min(value = 1, message = "prioridade minima e 1")
        Integer prioridade,

        @NotNull(message = "cidadaoId e obrigatorio")
        Long cidadaoId
) {}
