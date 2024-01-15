package com.github.rafaelfernandes.parquimetro.service;

import com.github.rafaelfernandes.parquimetro.controller.response.MessageFormaPagamento;
import com.github.rafaelfernandes.parquimetro.dto.MessageDTO;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
public class FormaPagamentoService {

    @Autowired private ClienteRepository repository;

    public MessageFormaPagamento alterar(UUID requestId, String formaPagamentoStr){

        FormaPagamento formaPagamento = FormaPagamento.obterPorNome(formaPagamentoStr);

        if (formaPagamento == null){
            return MessageDTO.formaPagamentoError(HttpStatus.BAD_REQUEST, null);
        }


        Optional<ClienteEntity> cliente = this.repository.findById(requestId);

        if (cliente.isEmpty()) {
            return MessageDTO.formaPagamentoError(HttpStatus.NOT_FOUND, null);
        }

        ClienteEntity clienteEntity = new ClienteEntity(
                requestId,
                cliente.get().nome(),
                cliente.get().documento(),
                cliente.get().endereco(),
                formaPagamento,
                cliente.get().contato(),
                cliente.get().carros()
        );

        this.repository.save(clienteEntity);

        return MessageDTO.formaPagamentoSuccess(HttpStatus.NO_CONTENT, null);

    }


    public MessageFormaPagamento obter(UUID requestId) {
        Optional<ClienteEntity> cliente = this.repository.findById(requestId);

        return cliente
                .map(clienteEntity -> MessageDTO.formaPagamentoSuccess(HttpStatus.OK, clienteEntity.forma_pagamento()))
                .orElseGet(() -> MessageDTO.formaPagamentoError(HttpStatus.NOT_FOUND, null));
    }
}
