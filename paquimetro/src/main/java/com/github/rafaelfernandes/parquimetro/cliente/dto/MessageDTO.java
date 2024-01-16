package com.github.rafaelfernandes.parquimetro.cliente.dto;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Cliente;
import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageCarros;
import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageCliente;
import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageFormaPagamento;
import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import org.springframework.http.HttpStatus;


import java.util.ArrayList;
import java.util.List;

public class MessageDTO {

    public static MessageCliente clienteError(HttpStatus httpStatus, List<String> erros){
        return new MessageCliente(null, httpStatus.value(), erros == null ? new ArrayList<>() : erros);
    }

    public static MessageCliente clienteError(HttpStatus httpStatus, String erro){
        var erros = new ArrayList<String>();
        erros.add(erro);
        return new MessageCliente(null, httpStatus.value(), erros);
    }

    public static MessageCliente clienteSuccess(HttpStatus httpStatus, List<Cliente> clientes){
        return new MessageCliente(clientes, httpStatus.value(), new ArrayList<>());
    }

    public static MessageCarros carrosError(HttpStatus httpStatus, List<String> erros){
        return new MessageCarros(null, httpStatus.value(), erros == null ? new ArrayList<>() : erros);
    }

    public static MessageCarros carrosSuccess(HttpStatus httpStatus, List<String> carros){
        return new MessageCarros(carros, httpStatus.value(), new ArrayList<>());
    }

    public static MessageFormaPagamento formaPagamentoError(HttpStatus httpStatus, List<String> erros){
        return new MessageFormaPagamento(null, httpStatus.value(), erros == null ? new ArrayList<>() : erros);
    }

    public static MessageFormaPagamento formaPagamentoSuccess(HttpStatus httpStatus, FormaPagamento formaPagamento){
        return new MessageFormaPagamento(formaPagamento, httpStatus.value(), new ArrayList<>());
    }

}
