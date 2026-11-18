package br.com.fiap.amparo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CidadaoRequest(
        @NotBlank(message = "nome e obrigatorio")
        @Size(max = 120)
        String nome,

        @NotNull(message = "dataNascimento e obrigatoria")
        @Past(message = "dataNascimento deve estar no passado")
        LocalDate dataNascimento,

        @Size(max = 20)
        String telefone,

        @Size(max = 200)
        String endereco,

        @Size(max = 120)
        String condicao,

        Boolean ativo
) {}
