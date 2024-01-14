package com.github.rafaelfernandes.parquimetro.controller.response;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import org.springframework.http.HttpStatus;

import java.util.List;

public record Message(
        Cliente cliente,
        Boolean isError,

        Integer httpStatusCode,
        List<String> errors
) {
}
