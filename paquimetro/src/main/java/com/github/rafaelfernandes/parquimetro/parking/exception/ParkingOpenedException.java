package com.github.rafaelfernandes.parquimetro.parking.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ParkingOpenedException extends RuntimeException{

    private static final String error = "Registro não encontrado";

    private final List<String> errors;


    public ParkingOpenedException() {
        super(ParkingOpenedException.error);
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }
}
