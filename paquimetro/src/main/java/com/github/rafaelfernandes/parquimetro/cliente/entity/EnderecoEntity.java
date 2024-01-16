package com.github.rafaelfernandes.parquimetro.cliente.entity;

import com.github.rafaelfernandes.parquimetro.cliente.enums.Estados;

public record EnderecoEntity(
        String logradouro,
        Integer numero,
        String complemento,
        String bairro,
        String cidade,
        Estados estado
) {
}

