package com.github.rafaelfernandes.parquimetro.cliente.controller.request;

import com.github.rafaelfernandes.parquimetro.cliente.enums.State;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record Address(
        @NotEmpty(message = "O campo address.public_area deve estar preenchido")
        @Length( min = 10, max = 150, message = "O campo address.public_area deve ter no máximo 150 caracteres")
        String public_area,

        @NotNull(message = "O campo address.number deve estar preenchido")
        @Positive(message = "O campo address.number deve ser maior que zero (0)")
        Integer number,

        @Length( min = 0, max = 150, message = "O campo address.additional_address_details deve ter no máximo 150 caracteres")
        String additional_address_details,
        @NotEmpty(message = "O campo address.neighborhood deve estar preenchido")
        @Length( min = 3, max = 30, message = "O campo address.neighborhood deve ter no máximo 30 caracteres")
        String neighborhood,

        @NotEmpty(message = "O campo address.city deve estar preenchido")
        @Length( min = 3, max = 60, message = "O campo address.city deve ter no máximo 60 caracteres")
        String city,

        @NotNull(message = "O campo address.state deve estar preenchido")
        State state
) {
}

