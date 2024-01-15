package com.github.rafaelfernandes.parquimetro.controller.response;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.enums.FormaPagamento;

import java.util.List;

public record MessageCliente(
        List<Cliente> clientes,
        Integer httpStatusCode,
        List<String> errors
) {
}