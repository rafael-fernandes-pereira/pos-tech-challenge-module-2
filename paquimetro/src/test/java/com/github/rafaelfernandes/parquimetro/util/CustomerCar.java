package com.github.rafaelfernandes.parquimetro.util;

import com.github.rafaelfernandes.parquimetro.customer.controller.request.Customer;

public record CustomerCar(
        Customer customer,
        String carro
) {
}
