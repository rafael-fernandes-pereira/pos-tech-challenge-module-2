package com.github.rafaelfernandes.parquimetro.customer.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PaymentMethodNull extends RuntimeException{

    private static final String error = "Metodo de pagamento não aceito!";

    private final List<String> errors;

    public PaymentMethodNull() {
        super(PaymentMethodNull.error);
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }
}
