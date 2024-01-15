package com.github.rafaelfernandes.parquimetro.validation;

import com.github.rafaelfernandes.parquimetro.controller.request.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.request.Contato;
import com.github.rafaelfernandes.parquimetro.controller.request.Endereco;
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

    public List<String> cliente(Cliente cliente){

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

}
