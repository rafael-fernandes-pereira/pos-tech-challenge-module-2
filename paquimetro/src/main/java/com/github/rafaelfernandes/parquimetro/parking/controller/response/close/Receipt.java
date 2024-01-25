package com.github.rafaelfernandes.parquimetro.parking.controller.response.close;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Receipt(
        LocalDateTime start,
        LocalDateTime end,
        BigDecimal value,
        BigDecimal penalty,
        BigDecimal final_value
) {
}
