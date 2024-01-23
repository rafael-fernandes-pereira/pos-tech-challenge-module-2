package com.github.rafaelfernandes.parquimetro.estacionamento.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ParkingOpenNotFound extends RuntimeException{

    private static final String error = "Estacionamento não encontrado!";

    private final List<String> errors;

    public ParkingOpenNotFound() {
        super(error);
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }
}
