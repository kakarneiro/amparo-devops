package br.com.fiap.amparo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DispositivoRequest(
        @NotBlank(message = "codigoSerial e obrigatorio") @Size(max = 60)
        String codigoSerial,

        @Size(max = 60)
        String modelo,

        Boolean ativo,

        @NotNull(message = "cidadaoId e obrigatorio")
        Long cidadaoId
) {}
