package com.github.rafaelfernandes.parquimetro.estacionamento.dto;

import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.ParkingOpened;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.MessageAberto;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class MessageEstacionamentoDTO {

    public static MessageAberto error(HttpStatus httpStatus, String erro){
        ArrayList<String> erros = new ArrayList<>();
        erros.add(erro);
        return new MessageAberto(null, httpStatus.value(), erros);
    }

    public static MessageAberto error(HttpStatus httpStatus, List<String> erros){
        return new MessageAberto(null, httpStatus.value(), erros != null ? erros : new ArrayList<>());
    }

    public static MessageAberto success(HttpStatus httpStatus, ParkingOpened parkingOpened){
        ArrayList<ParkingOpened> parkingOpeneds = new ArrayList<>();
        parkingOpeneds.add(parkingOpened);
        return new MessageAberto(parkingOpeneds, httpStatus.value(), new ArrayList<>());
    }

    public static MessageAberto success(HttpStatus httpStatus, List<ParkingOpened> parkingOpeneds){
        return new MessageAberto(parkingOpeneds, httpStatus.value(), new ArrayList<>());
    }

}
