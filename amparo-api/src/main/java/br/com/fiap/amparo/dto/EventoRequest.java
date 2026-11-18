package br.com.fiap.amparo.dto;

import br.com.fiap.amparo.model.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload que o ESP32 envia. Usa o serial do dispositivo (nao o id),
 * porque e o que o hardware conhece.
 */
public record EventoRequest(
        @NotBlank(message = "codigoSerial e obrigatorio")
        String codigoSerial,

        @NotNull(message = "tipo e obrigatorio")
        TipoEvento tipo,

        @Size(max = 255)
        String descricao,

        Double latitude,

        Double longitude
) {}
