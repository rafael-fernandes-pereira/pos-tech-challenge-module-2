package com.github.rafaelfernandes.parquimetro.estacionamento.controller.response;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Contato;
import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoAbertoEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;

import java.time.LocalDateTime;
import java.util.UUID;

public record Estacionamento(
        UUID id,
        UUID cliente_id,
        String carro,
        String nome,
        Contato contato,
        FormaPagamento forma_pagamento,
        TipoPeriodo tipo_periodo,
        Integer duracao_fixa,
        LocalDateTime inicio

) {

    public static Estacionamento fromEstacionamentoAberto(EstacionamentoAbertoEntity entity){
        return new Estacionamento(
                entity.id(),
                entity.clienteId(),
                entity.carro(),
                entity.nome(),
                new Contato(
                        entity.contato().email(),
                        entity.contato().telefone()
                ),
                entity.formaPagamento(),
                entity.tipoPeriodo(),
                entity.duracaoFixa(),
                entity.inicio()
        );
    }

}
