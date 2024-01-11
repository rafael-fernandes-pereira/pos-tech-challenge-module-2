package com.github.rafaelfernandes.parquimetro.entity;

import com.github.rafaelfernandes.parquimetro.enums.FormaPagamento;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document("cliente")
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
