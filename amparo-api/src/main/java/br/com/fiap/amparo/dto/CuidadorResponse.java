package br.com.fiap.amparo.dto;

import br.com.fiap.amparo.model.Cuidador;

public record CuidadorResponse(
        Long id,
        String nome,
        String telefone,
        String email,
        String parentesco,
        Integer prioridade,
        Long cidadaoId,
        String cidadaoNome
) {
    public static CuidadorResponse from(Cuidador c) {
        return new CuidadorResponse(c.getId(), c.getNome(), c.getTelefone(), c.getEmail(),
                c.getParentesco(), c.getPrioridade(),
                c.getCidadao().getId(), c.getCidadao().getNome());
    }
}
