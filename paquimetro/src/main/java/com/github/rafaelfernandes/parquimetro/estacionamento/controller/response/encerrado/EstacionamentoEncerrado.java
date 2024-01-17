package com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Contato;
import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoEncerradoEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;

import java.util.UUID;

public record EstacionamentoEncerrado(
        UUID id,
        UUID cliente_id,
        String carro,
        String nome,
        Contato contato,
        FormaPagamento forma_pagamento,
        TipoPeriodo tipo_periodo,
        Integer duracao_fixa,
        Recibo recibo

) {

    public static EstacionamentoEncerrado fromEstacionamentoEncerradoEntity(EstacionamentoEncerradoEntity entity){
        return new EstacionamentoEncerrado(
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
                entity.recibo()
        );
    }

}
