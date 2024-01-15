package com.github.rafaelfernandes.parquimetro.controller.response;

import com.github.rafaelfernandes.parquimetro.enums.FormaPagamento;

import java.util.List;

public record MessageFormaPagamento(

        FormaPagamento forma_pagamento,
        Integer httpStatusCode,
        List<String> errors
) {
}
