package com.github.rafaelfernandes.parquimetro.cliente.controller.response;

import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;

import java.util.List;

public record MessageFormaPagamento(

        FormaPagamento forma_pagamento,
        Integer httpStatusCode,
        List<String> errors
) {
}
