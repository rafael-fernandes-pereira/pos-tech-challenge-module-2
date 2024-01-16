package com.github.rafaelfernandes.parquimetro.estacionamento.controller.response;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record MessageFinalizado(
        Recibo recibo,
        Integer http_status_code,

        List<String> erros
) {

    public static MessageFinalizado error(HttpStatus httpStatus, String erro){
        ArrayList<String> erros = new ArrayList<>();
        erros.add(erro);
        return new MessageFinalizado(null, httpStatus.value(), erros);
    }

    public static MessageFinalizado error(HttpStatus httpStatus, List<String> erros){
        return new MessageFinalizado(null, httpStatus.value(), erros);
    }



}
