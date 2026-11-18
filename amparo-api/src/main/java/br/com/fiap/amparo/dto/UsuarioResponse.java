package br.com.fiap.amparo.dto;

import br.com.fiap.amparo.model.Papel;
import br.com.fiap.amparo.model.Usuario;

public record UsuarioResponse(Long id, String nome, String email, Papel papel, Boolean ativo) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPapel(), u.getAtivo());
    }
}
