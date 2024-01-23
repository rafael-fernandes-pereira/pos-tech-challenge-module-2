package com.github.rafaelfernandes.parquimetro.customer.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {

    PIX ("Pix"),
    CREDIT_CARD("Cartão de crédito"),
    DEBIT_CARD("Cartão de débito");

    private final String details;

    PaymentMethod(String details){
        this.details = details;
    }

    public static PaymentMethod getByName(String details) {
        for (PaymentMethod paymentMethod : values()) {
            if (paymentMethod.name().equalsIgnoreCase(details)) {
                return paymentMethod;
            }
        }
        return null;
    }

}
