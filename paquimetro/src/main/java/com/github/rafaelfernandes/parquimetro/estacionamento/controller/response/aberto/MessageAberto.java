package com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto;

import java.util.List;

public record MessageAberto(
        List<ParkingOpened> estacionamentos,
        Integer http_status_code,
        List<String> erros
) {
}
