package br.com.fiap.amparo.dto;

import br.com.fiap.amparo.model.Lembrete;

import java.time.LocalTime;

public record LembreteResponse(
        Long id,
        String titulo,
        String descricao,
        LocalTime horario,
        Boolean ativo,
        Long cidadaoId
) {
    public static LembreteResponse from(Lembrete l) {
        return new LembreteResponse(l.getId(), l.getTitulo(), l.getDescricao(), l.getHorario(),
                l.getAtivo(), l.getCidadao().getId());
    }
}
