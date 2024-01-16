package com.github.rafaelfernandes.parquimetro.estacionamento.entity;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Cliente;
import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Contato;
import com.github.rafaelfernandes.parquimetro.cliente.entity.ContatoEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document("estacionamento_aberto")
public record EstacionamentoAbertoEntity(
        UUID id,
        UUID clienteId,
        @Indexed(unique = true)
        String carro,
        String nome,
        ContatoEntity contato,
        FormaPagamento formaPagamento,
        TipoPeriodo tipoPeriodo,
        Integer duracaoFixa,
        LocalDateTime inicio
) {
        public static EstacionamentoAbertoEntity novo (Cliente cliente, String carro, TipoPeriodo tipoPeriodo, Integer duracao){

                return new EstacionamentoAbertoEntity(
                        UUID.randomUUID(),
                        cliente.id(),
                        carro,
                        cliente.nome(),
                        new ContatoEntity(
                                cliente.contato().email(),
                                cliente.contato().celular()
                        ),
                        cliente.forma_pagamento(),
                        tipoPeriodo,
                        tipoPeriodo.equals(TipoPeriodo.FIXO) ? duracao : null,
                        LocalDateTime.now()
                );

        }
}
