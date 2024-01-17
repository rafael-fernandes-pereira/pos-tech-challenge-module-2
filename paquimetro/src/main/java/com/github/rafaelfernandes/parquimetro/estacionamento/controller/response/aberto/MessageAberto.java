package com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto;

import java.util.List;

public record MessageAberto(
        List<EstacionamentoAberto> estacionamentos,
        Integer http_status_code,
        List<String> erros
) {
}
