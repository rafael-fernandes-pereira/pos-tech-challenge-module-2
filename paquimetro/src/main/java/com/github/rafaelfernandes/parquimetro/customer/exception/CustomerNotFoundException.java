package com.github.rafaelfernandes.parquimetro.customer.exception;



import lombok.Getter;
import org.apache.tomcat.util.buf.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CustomerNotFoundException extends RuntimeException{

    private static final String error = "Cliente não existe!";

    private final List<String> errors;

    public CustomerNotFoundException(List<String> errors){
        super(StringUtils.join(errors, ';'));
        this.errors = errors;
    }

    public CustomerNotFoundException(){
        super(CustomerNotFoundException.error);
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }



}
