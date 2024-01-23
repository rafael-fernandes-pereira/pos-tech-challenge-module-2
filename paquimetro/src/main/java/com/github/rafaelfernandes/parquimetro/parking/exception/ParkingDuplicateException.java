package com.github.rafaelfernandes.parquimetro.parking.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ParkingDuplicateException extends RuntimeException{

    private static final String error = "Carro já está com tempo lançado!";

    private final List<String> errors;

    public ParkingDuplicateException() {
        super(error);
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }
}
