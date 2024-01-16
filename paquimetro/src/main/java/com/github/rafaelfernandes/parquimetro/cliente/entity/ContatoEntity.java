package com.github.rafaelfernandes.parquimetro.cliente.entity;

import org.springframework.data.mongodb.core.index.Indexed;

public record ContatoEntity(
        @Indexed(unique = true)
        String email,
        String telefone
) {
}
