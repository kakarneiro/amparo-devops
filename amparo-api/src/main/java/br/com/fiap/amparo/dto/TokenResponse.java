package br.com.fiap.amparo.dto;

import br.com.fiap.amparo.model.Papel;

public record TokenResponse(
        String token,
        String tipo,
        long expiraEmMs,
        String nome,
        String email,
        Papel papel
) {}
