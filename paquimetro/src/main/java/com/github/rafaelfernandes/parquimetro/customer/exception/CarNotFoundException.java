package com.github.rafaelfernandes.parquimetro.customer.exception;



import lombok.Getter;
import org.apache.tomcat.util.buf.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CarNotFoundException extends RuntimeException{

    private static final String error = "Carro não existe para esse cliente";
    private final List<String> errors;

    public CarNotFoundException(List<String> errors){
        super(StringUtils.join(errors, ';'));
        this.errors = errors;
    }

    public CarNotFoundException(){
        super(CarNotFoundException.error);
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }



}
