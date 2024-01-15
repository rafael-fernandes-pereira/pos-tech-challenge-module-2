package com.github.rafaelfernandes.parquimetro.controller;

import com.github.rafaelfernandes.parquimetro.enums.FormaPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.UUID;

public record Cliente(

        UUID id,
        @NotEmpty(message = "O campo nome deve estar preenchido")
        @Length(min = 10, max = 100, message = "O campo nome deve ter no mínimo de 20 e no máximo de 100 caracteres")
        String nome,
        @NotNull(message = "O campo documento deve estar preenchido")
        Long documento,
        @NotNull(message = "O campo endereco deve estar preenchido")
        Endereco endereco,
        @NotNull(message = "O campo forma_pagamento deve estar preenchido")
        FormaPagamento forma_pagamento,
        @NotNull(message = "O campo contato deve estar preenchido")
        Contato contato
) {
}
