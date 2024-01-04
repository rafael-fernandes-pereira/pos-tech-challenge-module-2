package com.github.rafaelfernandes.parquimetro.controller;

public record Endereco(
        String logradouro,
        Number numero,
        String observacao,
        String bairro,
        String cidade,
        String estado
) {
}

