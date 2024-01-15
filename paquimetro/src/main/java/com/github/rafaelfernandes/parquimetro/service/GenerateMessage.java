package com.github.rafaelfernandes.parquimetro.service;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.response.MessageCliente;
import com.github.rafaelfernandes.parquimetro.enums.FormaPagamento;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GenerateMessage {

    public MessageCliente errors(HttpStatus httpStatus, List<String> erros){
        return new MessageCliente(null, httpStatus.value(), erros == null ? new ArrayList<>() : erros);
    }

    public MessageCliente success(HttpStatus httpStatus, List<Cliente> clientes){
        return new MessageCliente(clientes, httpStatus.value(), new ArrayList<>());
    }

}
