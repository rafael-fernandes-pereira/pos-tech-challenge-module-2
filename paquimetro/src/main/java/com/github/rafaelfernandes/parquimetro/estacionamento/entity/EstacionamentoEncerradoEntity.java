package com.github.rafaelfernandes.parquimetro.estacionamento.entity;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Cliente;
import com.github.rafaelfernandes.parquimetro.cliente.entity.ContatoEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.Estacionamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.Recibo;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document("estacionamento_encerrado")
public record EstacionamentoEncerradoEntity(
        UUID id,
        UUID clienteId,
        String carro,
        String nome,
        ContatoEntity contato,
        FormaPagamento formaPagamento,
        TipoPeriodo tipoPeriodo,
        Integer duracaoFixa,
        Recibo recibo
) {
        public static EstacionamentoEncerradoEntity from (Estacionamento estacionamento, Recibo recibo){
                return new EstacionamentoEncerradoEntity(
                        UUID.randomUUID(),
                        estacionamento.cliente_id(),
                        estacionamento.carro(),
                        estacionamento.nome(),
                        new ContatoEntity(
                                estacionamento.contato().email(),
                                estacionamento.contato().celular()
                        ),
                        estacionamento.forma_pagamento(),
                        estacionamento.tipo_periodo(),
                        estacionamento.duracao_fixa(),
                        recibo
                );
        }
}
