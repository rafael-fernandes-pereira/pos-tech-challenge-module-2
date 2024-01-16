package com.github.rafaelfernandes.parquimetro.estacionamento.controller.response;

import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;

import java.time.LocalDateTime;
import java.util.UUID;

public record Estacionamento(
        UUID cliente_id,
        String carro,
        FormaPagamento forma_pagamento,
        TipoPeriodo tipo_periodo,
        Integer duracao_fixa,
        LocalDateTime inicio

) {
}
