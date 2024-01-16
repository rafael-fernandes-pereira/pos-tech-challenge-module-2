package com.github.rafaelfernandes.parquimetro.cliente.entity;

import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document("cliente")
public record ClienteEntity(

        UUID id,
        String nome,
        @Indexed(unique = true)
        Long documento,
        EnderecoEntity endereco,
        FormaPagamento forma_pagamento,
        ContatoEntity contato,
        List<String> carros
) {
}
