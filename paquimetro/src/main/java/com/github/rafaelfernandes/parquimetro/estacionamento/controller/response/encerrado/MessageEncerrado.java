package com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public record MessageEncerrado(

        List<EstacionamentoEncerrado> estacionamentos,
        Integer http_status_code,

        List<String> erros
) {

    public static MessageEncerrado success(HttpStatus httpStatus, EstacionamentoEncerrado estacionamentoEncerrado){
        var estacionamentos = new ArrayList<EstacionamentoEncerrado>();
        estacionamentos.add(estacionamentoEncerrado);
        return new MessageEncerrado(estacionamentos, httpStatus.value(), new ArrayList<>());
    }

    public static MessageEncerrado error(HttpStatus httpStatus, String erro){
        ArrayList<String> erros = new ArrayList<>();
        erros.add(erro);
        return new MessageEncerrado(null, httpStatus.value(), erros);
    }

    public static MessageEncerrado error(HttpStatus httpStatus, List<String> erros){
        return new MessageEncerrado(null, httpStatus.value(), erros);
    }



}
