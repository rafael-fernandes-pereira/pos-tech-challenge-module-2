package com.github.rafaelfernandes.parquimetro.estacionamento.dto;

import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.Estacionamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.MessageEstacionamento;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class MessageEstacionamentoDTO {

    public static MessageEstacionamento error(HttpStatus httpStatus, String erro){
        ArrayList<String> erros = new ArrayList<>();
        erros.add(erro);
        return new MessageEstacionamento(null, httpStatus.value(), erros);
    }

    public static MessageEstacionamento error(HttpStatus httpStatus, List<String> erros){
        return new MessageEstacionamento(null, httpStatus.value(), erros != null ? erros : new ArrayList<>());
    }

    public static MessageEstacionamento success(HttpStatus httpStatus, Estacionamento estacionamento){
        ArrayList<Estacionamento> estacionamentos = new ArrayList<>();
        estacionamentos.add(estacionamento);
        return new MessageEstacionamento(estacionamentos, httpStatus.value(), new ArrayList<>());
    }

    public static MessageEstacionamento success(HttpStatus httpStatus, List<Estacionamento> estacionamentos){
        return new MessageEstacionamento(estacionamentos, httpStatus.value(), new ArrayList<>());
    }

}
