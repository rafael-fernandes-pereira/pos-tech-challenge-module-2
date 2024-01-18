package com.github.rafaelfernandes.parquimetro.cliente.entity;

import com.github.rafaelfernandes.parquimetro.cliente.enums.State;

public record AddressEntity(
        String public_area,
        Integer number,
        String additional_address_details,
        String neighborhood,
        String city,
        State state
) {
}

