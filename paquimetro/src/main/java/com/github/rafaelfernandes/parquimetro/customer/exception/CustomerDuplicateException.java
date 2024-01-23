package com.github.rafaelfernandes.parquimetro.customer.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CustomerDuplicateException extends RuntimeException{

    private static final String error = "Campo document e/ou campo email já existem!";

    private final List<String> errors;

    public CustomerDuplicateException() {
        super(error);
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }
}
