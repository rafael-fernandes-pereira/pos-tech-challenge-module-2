package com.github.rafaelfernandes.parquimetro.cliente.controller.response;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Cliente;

import java.util.List;

public record MessageCliente(
        List<Cliente> clientes,
        Integer httpStatusCode,
        List<String> errors
) {
}
