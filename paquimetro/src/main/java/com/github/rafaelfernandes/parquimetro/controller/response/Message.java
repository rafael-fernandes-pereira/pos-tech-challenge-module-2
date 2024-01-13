package com.github.rafaelfernandes.parquimetro.controller.response;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;

import java.util.List;

public record Message(
        Cliente cliente,
        Boolean isError,
        List<String> errors
) {
}
