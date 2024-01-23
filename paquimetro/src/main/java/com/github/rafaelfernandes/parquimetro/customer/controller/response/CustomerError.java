package com.github.rafaelfernandes.parquimetro.customer.controller.response;

import java.util.List;

public record CustomerError(
        List<String> errors
) {
}
