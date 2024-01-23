package com.github.rafaelfernandes.parquimetro.parking.controller.response.encerrado;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Receipt(
        LocalDateTime start,
        LocalDateTime end,
        Integer hours_request,
        BigDecimal value,
        Long more_time,
        BigDecimal penalty,
        BigDecimal final_value
) {
}
