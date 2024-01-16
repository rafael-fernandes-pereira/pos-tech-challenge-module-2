package com.github.rafaelfernandes.parquimetro.estacionamento.controller.response;

import java.util.List;

public record MessageEstacionamento(
        List<Estacionamento> estacionamentos,
        Integer http_status_code,
        List<String> erros
) {
}
