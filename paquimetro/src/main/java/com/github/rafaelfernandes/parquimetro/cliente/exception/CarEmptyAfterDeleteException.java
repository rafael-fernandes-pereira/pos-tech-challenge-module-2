package com.github.rafaelfernandes.parquimetro.cliente.exception;



import lombok.Getter;
import org.apache.tomcat.util.buf.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CarEmptyAfterDeleteException extends RuntimeException{

    private static final String error = "Não é possível deletar o único carro do customer. Adicione outro e, depois, delete este.";

    private final List<String> errors;

    public CarEmptyAfterDeleteException(List<String> errors){
        super(StringUtils.join(errors, ';'));
        this.errors = errors;
    }

    public CarEmptyAfterDeleteException(){
        super(CarEmptyAfterDeleteException.error);
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }



}
