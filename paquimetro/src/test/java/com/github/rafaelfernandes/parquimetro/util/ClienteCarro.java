package com.github.rafaelfernandes.parquimetro.util;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;

public record ClienteCarro(
        Customer customer,
        String carro
) {
}
