package com.github.rafaelfernandes.parquimetro.controller;

import com.github.rafaelfernandes.parquimetro.enums.Estados;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record Endereco(
        @NotEmpty(message = "O campo endereco.logradouro deve estar preenchido")
        @Length( min = 10, max = 150, message = "O campo endereco.logradouro deve ter no máximo 150 caracteres")
        String logradouro,

        @NotNull(message = "O campo endereco.numero deve estar preenchido")
        @Positive(message = "O campo endereco.numero deve ser maior que zero (0)")
        Integer numero,

        @Length( min = 0, max = 150, message = "O campo endereco.complemento deve ter no máximo 150 caracteres")
        String complemento,
        @NotEmpty(message = "O campo endereco.bairro deve estar preenchido")
        @Length( min = 3, max = 30, message = "O campo endereco.bairro deve ter no máximo 30 caracteres")
        String bairro,

        @NotEmpty(message = "O campo endereco.cidade deve estar preenchido")
        @Length( min = 3, max = 60, message = "O campo endereco.complemento deve ter no máximo 60 caracteres")
        String cidade,

        @NotNull(message = "O campo endereco.cidade deve estar preenchido")
        Estados estado
) {
}

