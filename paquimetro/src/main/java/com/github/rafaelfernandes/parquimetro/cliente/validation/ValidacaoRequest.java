package com.github.rafaelfernandes.parquimetro.cliente.validation;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Contact;
import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Address;
import org.springframework.stereotype.Service;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
public class ValidacaoRequest {

    private Validator validator;

    public ValidacaoRequest(Validator validator){
        this.validator = validator;
    }

    public List<String> cliente(Customer customer){

        List<String> erros = new ArrayList<>();

        Set<ConstraintViolation<Customer>> violacoesCliente = validator.validate(customer);

        if (!violacoesCliente.isEmpty()){
            List<String> errosCliente = violacoesCliente.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();
            erros.addAll(errosCliente);
        }


        if (null != customer.address()){
            Set<ConstraintViolation<Address>> violacoesEndereco = validator.validate(customer.address());

            if (!violacoesEndereco.isEmpty()){
                List<String> errosEndereco = violacoesEndereco.stream()
                        .map(ConstraintViolation::getMessage)
                        .toList();
                erros.addAll(errosEndereco);
            }
        }

        if (null != customer.contact()){
            Set<ConstraintViolation<Contact>> violacoesContato = validator.validate(customer.contact());

            if (!violacoesContato.isEmpty()){
                List<String> errosContato = violacoesContato.stream()
                        .map(ConstraintViolation::getMessage)
                        .toList();
                erros.addAll(errosContato);
            }
        }

        return erros;

    }

}
