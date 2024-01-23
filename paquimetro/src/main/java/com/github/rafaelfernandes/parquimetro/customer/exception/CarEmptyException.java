package com.github.rafaelfernandes.parquimetro.customer.exception;



import lombok.Getter;
import org.apache.tomcat.util.buf.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CarEmptyException extends RuntimeException{

    private static final String error = "Deve enviar pelo menos um car!";

    private final List<String> errors;

    public CarEmptyException(List<String> errors){
        super(StringUtils.join(errors, ';'));
        this.errors = errors;
    }

    public CarEmptyException(){
        super(CarEmptyException.error);
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }



}
