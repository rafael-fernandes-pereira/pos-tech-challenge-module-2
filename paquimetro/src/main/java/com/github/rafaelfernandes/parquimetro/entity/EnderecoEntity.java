package com.github.rafaelfernandes.parquimetro.entity;

public record EnderecoEntity(
        String logradouro,
        Number numero,
        String observacao,
        String bairro,
        String cidade,
        String estado
) {
}

