package com.github.rafaelfernandes.parquimetro.entity;

import com.github.rafaelfernandes.parquimetro.enums.FormaPagamento;

import java.util.List;
import java.util.UUID;

public record ClienteEntity(

        UUID id,
        String nome,
        Long documento,
        EnderecoEntity endereco,
        FormaPagamento forma_pagamento,
        ContatoEntity contato,
        List<String> carros
) {
}
