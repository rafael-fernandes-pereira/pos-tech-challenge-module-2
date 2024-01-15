package com.github.rafaelfernandes.parquimetro.controller.response;

import java.util.List;

public record MessageCarros(
        List<String> carros,
        Integer httpStatusCode,
        List<String> errors){
}