package com.github.rafaelfernandes.parquimetro.entity;

public record EnderecoEntity(
        String logradouro,
        Number numero,
        String complemento,
        String bairro,
        String cidade,
        String estado
) {
}

