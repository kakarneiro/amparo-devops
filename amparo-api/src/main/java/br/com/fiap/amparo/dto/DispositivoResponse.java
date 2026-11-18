package br.com.fiap.amparo.dto;

import br.com.fiap.amparo.model.Dispositivo;

public record DispositivoResponse(
        Long id,
        String codigoSerial,
        String modelo,
        Boolean ativo,
        Long cidadaoId,
        String cidadaoNome
) {
    public static DispositivoResponse from(Dispositivo d) {
        return new DispositivoResponse(d.getId(), d.getCodigoSerial(), d.getModelo(), d.getAtivo(),
                d.getCidadao().getId(), d.getCidadao().getNome());
    }
}
