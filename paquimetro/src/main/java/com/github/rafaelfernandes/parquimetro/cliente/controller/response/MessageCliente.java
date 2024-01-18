package com.github.rafaelfernandes.parquimetro.cliente.controller.response;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;

import java.util.List;

public record MessageCliente(
        List<Customer> customers,
        Integer httpStatusCode,
        List<String> errors
) {
}
