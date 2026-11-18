package br.com.fiap.amparo.dto;

import br.com.fiap.amparo.model.Papel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "nome e obrigatorio") @Size(max = 120)
        String nome,

        @NotBlank(message = "email e obrigatorio") @Email(message = "email invalido") @Size(max = 120)
        String email,

        @NotBlank(message = "senha e obrigatoria")
        @Size(min = 6, max = 60, message = "senha deve ter entre 6 e 60 caracteres")
        String senha,

        @NotNull(message = "papel e obrigatorio")
        Papel papel
) {}
