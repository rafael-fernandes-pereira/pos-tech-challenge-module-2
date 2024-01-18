package com.github.rafaelfernandes.parquimetro.cliente.entity;

import org.springframework.data.mongodb.core.index.Indexed;

public record ContactEntity(
        @Indexed(unique = true)
        String email,
        String celphone
) {
}
