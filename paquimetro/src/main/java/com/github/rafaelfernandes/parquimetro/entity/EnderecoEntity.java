package com.github.rafaelfernandes.parquimetro.entity;

import com.github.rafaelfernandes.parquimetro.enums.Estados;

public record EnderecoEntity(
        String logradouro,
        Integer numero,
        String complemento,
        String bairro,
        String cidade,
        Estados estado
) {
}

