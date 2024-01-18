package com.github.rafaelfernandes.parquimetro.cliente.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {

    PIX ("Pix"),
    CARTAO_CREDITO("Cartão de crédito"),
    CARTAO_DEBITO("Cartão de débito");

    private final String descricao;

    PaymentMethod(String descricao){
        this.descricao = descricao;
    }

    public static PaymentMethod getByName(String descricao) {
        for (PaymentMethod paymentMethod : values()) {
            if (paymentMethod.name().equalsIgnoreCase(descricao)) {
                return paymentMethod;
            }
        }
        return null;
    }

}
