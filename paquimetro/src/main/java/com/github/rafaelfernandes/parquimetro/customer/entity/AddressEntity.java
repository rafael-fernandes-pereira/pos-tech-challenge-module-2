package com.github.rafaelfernandes.parquimetro.customer.entity;

import com.github.rafaelfernandes.parquimetro.customer.enums.State;

public record AddressEntity(
        String public_area,
        Integer number,
        String additional_address_details,
        String neighborhood,
        String city,
        State state
) {
}

