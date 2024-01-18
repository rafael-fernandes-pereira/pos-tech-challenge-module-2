package com.github.rafaelfernandes.parquimetro.cliente.controller.response;

import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;

import java.util.List;

public record MessageFormaPagamento(

        PaymentMethod payment_method,
        Integer httpStatusCode,
        List<String> errors
) {
}
