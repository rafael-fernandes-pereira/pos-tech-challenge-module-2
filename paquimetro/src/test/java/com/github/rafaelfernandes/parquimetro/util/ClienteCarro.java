package com.github.rafaelfernandes.parquimetro.util;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Cliente;

import java.util.UUID;

public record ClienteCarro(
        Cliente cliente,
        String carro
) {
}
