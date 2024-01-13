package com.github.rafaelfernandes.parquimetro.validation;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.Contato;
import com.github.rafaelfernandes.parquimetro.controller.Endereco;
import com.github.rafaelfernandes.parquimetro.controller.response.Message;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ValidacaoRequest {

    private Validator validator;

    public ValidacaoRequest(Validator validator){
        this.validator = validator;
    }

    public List<String> execute(Cliente cliente){

        List<String> erros = new ArrayList<>();

        Set<ConstraintViolation<Cliente>> violacoesCliente = validator.validate(cliente);

        if (!violacoesCliente.isEmpty()){
            List<String> errosCliente = violacoesCliente.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();
            erros.addAll(errosCliente);
        }


        if (null != cliente.endereco()){
            Set<ConstraintViolation<Endereco>> violacoesEndereco = validator.validate(cliente.endereco());

            if (!violacoesEndereco.isEmpty()){
                List<String> errosEndereco = violacoesEndereco.stream()
                        .map(ConstraintViolation::getMessage)
                        .toList();
                erros.addAll(errosEndereco);
            }
        }

        if (null != cliente.contato()){
            Set<ConstraintViolation<Contato>> violacoesContato = validator.validate(cliente.contato());

            if (!violacoesContato.isEmpty()){
                List<String> errosContato = violacoesContato.stream()
                        .map(ConstraintViolation::getMessage)
                        .toList();
                erros.addAll(errosContato);
            }
        }

        return erros;


    }

    private List<String> getErrors(Set<ConstraintViolation<Cliente>> violations) {

        return new ArrayList<>();
    }

}
