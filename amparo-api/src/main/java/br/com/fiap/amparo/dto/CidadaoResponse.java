package br.com.fiap.amparo.dto;

import br.com.fiap.amparo.model.Cidadao;

import java.time.LocalDate;
import java.time.Period;

public record CidadaoResponse(
        Long id,
        String nome,
        LocalDate dataNascimento,
        Integer idade,
        String telefone,
        String endereco,
        String condicao,
        Boolean ativo
) {
    public static CidadaoResponse from(Cidadao c) {
        return new CidadaoResponse(
                c.getId(), c.getNome(), c.getDataNascimento(),
                Period.between(c.getDataNascimento(), LocalDate.now()).getYears(),
                c.getTelefone(), c.getEndereco(), c.getCondicao(), c.getAtivo());
    }
}
