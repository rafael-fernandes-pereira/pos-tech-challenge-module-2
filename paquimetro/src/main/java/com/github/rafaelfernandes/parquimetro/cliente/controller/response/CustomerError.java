package com.github.rafaelfernandes.parquimetro.cliente.controller.response;

import java.util.List;

public record CustomerError(
        List<String> errors
) {
}
