package com.github.rafaelfernandes.parquimetro.customer.exception;

import lombok.Getter;
import org.apache.tomcat.util.buf.StringUtils;

import java.util.List;

@Getter
public class CustomerValidationException extends RuntimeException{

    private final List<String> errors;

    public CustomerValidationException(List<String> errors) {
        super(StringUtils.join(errors, ';'));
        this.errors = errors;
    }




}
