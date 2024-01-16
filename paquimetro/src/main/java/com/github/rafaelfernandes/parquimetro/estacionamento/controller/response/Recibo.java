package com.github.rafaelfernandes.parquimetro.estacionamento.controller.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Recibo(
        LocalDateTime inicio,
        LocalDateTime fim,
        Integer horas_solicitadas,
        BigDecimal valor,
        Long tempo_a_mais,
        BigDecimal multa,
        BigDecimal valor_final
) {
}
