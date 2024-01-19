package com.github.rafaelfernandes.parquimetro.estacionamento.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ParkingMinimumDuration1HourException extends RuntimeException{

    private static final String error = "Tempo mínimo de 1 hora";

    private final List<String> errors;

    public ParkingMinimumDuration1HourException() {
        super(ParkingMinimumDuration1HourException.error);
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }
}
