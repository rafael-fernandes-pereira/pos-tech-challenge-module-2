package com.github.rafaelfernandes.parquimetro.entity;

import org.springframework.data.mongodb.core.index.Indexed;

public record ContatoEntity(
        @Indexed(unique = true)
        String email,
        String telefone
) {
}
