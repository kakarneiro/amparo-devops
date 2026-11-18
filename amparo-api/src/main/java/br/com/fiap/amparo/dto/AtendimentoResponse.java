package br.com.fiap.amparo.dto;

import br.com.fiap.amparo.model.Atendimento;
import br.com.fiap.amparo.model.ResultadoAtendimento;

import java.time.LocalDateTime;

public record AtendimentoResponse(
        Long id,
        Long eventoId,
        Long cuidadorId,
        String cuidadorNome,
        LocalDateTime dataAcionamento,
        LocalDateTime dataResposta,
        ResultadoAtendimento resultado,
        String observacao
) {
    public static AtendimentoResponse from(Atendimento a) {
        return new AtendimentoResponse(a.getId(), a.getEvento().getId(),
                a.getCuidador().getId(), a.getCuidador().getNome(),
                a.getDataAcionamento(), a.getDataResposta(), a.getResultado(), a.getObservacao());
    }
}
