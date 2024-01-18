package com.github.rafaelfernandes.parquimetro.cliente.exception;

import lombok.Getter;
import org.apache.tomcat.util.buf.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CustomerValidationException extends RuntimeException{

    private final List<String> errors;

    public CustomerValidationException(List<String> errors) {
        super(StringUtils.join(errors, ';'));
        this.errors = errors;
    }




}
