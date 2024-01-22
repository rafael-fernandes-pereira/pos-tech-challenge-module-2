package com.github.rafaelfernandes.parquimetro.estacionamento.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ParkingRegisterHourTypeAndPaymentMethodPixBadRequestException extends RuntimeException{

    private static final String error = "Forma de pagamento não permitido para o tipo de periodo escolhido!";

    private final List<String> errors;

    public ParkingRegisterHourTypeAndPaymentMethodPixBadRequestException() {
        super(ParkingRegisterHourTypeAndPaymentMethodPixBadRequestException.error);
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }
}
