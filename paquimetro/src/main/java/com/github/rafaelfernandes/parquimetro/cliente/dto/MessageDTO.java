package com.github.rafaelfernandes.parquimetro.cliente.dto;

import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageFormaPagamento;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import org.springframework.http.HttpStatus;


import java.util.ArrayList;
import java.util.List;

public class MessageDTO {

    public static MessageFormaPagamento formaPagamentoError(HttpStatus httpStatus, List<String> erros){
        return new MessageFormaPagamento(null, httpStatus.value(), erros == null ? new ArrayList<>() : erros);
    }

    public static MessageFormaPagamento formaPagamentoSuccess(HttpStatus httpStatus, PaymentMethod paymentMethod){
        return new MessageFormaPagamento(paymentMethod, httpStatus.value(), new ArrayList<>());
    }

}
