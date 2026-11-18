package br.com.fiap.amparo.dto;

import br.com.fiap.amparo.model.Evento;
import br.com.fiap.amparo.model.StatusEvento;
import br.com.fiap.amparo.model.TipoEvento;

import java.time.LocalDateTime;

public record EventoResponse(
        Long id,
        TipoEvento tipo,
        StatusEvento status,
        LocalDateTime dataHora,
        String descricao,
        Double latitude,
        Double longitude,
        Long dispositivoId,
        String codigoSerial,
        Long cidadaoId,
        String cidadaoNome
) {
    public static EventoResponse from(Evento e) {
        return new EventoResponse(e.getId(), e.getTipo(), e.getStatus(), e.getDataHora(),
                e.getDescricao(), e.getLatitude(), e.getLongitude(),
                e.getDispositivo().getId(), e.getDispositivo().getCodigoSerial(),
                e.getDispositivo().getCidadao().getId(), e.getDispositivo().getCidadao().getNome());
    }
}
