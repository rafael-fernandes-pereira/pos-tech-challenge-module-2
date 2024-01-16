package com.github.rafaelfernandes.parquimetro.estacionamento.controller;

import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.request.Fixo;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.Estacionamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.MessageEstacionamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/estacionamento")
public class EstacionamentoController {

    @GetMapping("/{requestId}/{carro}")
    ResponseEntity<MessageEstacionamento> obterDados(@PathVariable UUID requestId, @PathVariable String carro){

        Estacionamento estacionamento = new Estacionamento(
                requestId,
                carro,
                FormaPagamento.PIX,
                TipoPeriodo.FIXO,
                3,
                LocalDateTime.of(2024, Month.JANUARY, 1, 10, 50, 0)
        );

        ArrayList<Estacionamento> estacionamentoList = new ArrayList<>();
        estacionamentoList.add(estacionamento);

        MessageEstacionamento messageEstacionamento = new MessageEstacionamento(estacionamentoList, HttpStatus.OK.value(), null);

        return ResponseEntity
                .status(messageEstacionamento.http_status_code())
                .body(messageEstacionamento);

    }

    @PostMapping("/{requestId}/{carro}/fixo")
    ResponseEntity<MessageEstacionamento> registrarFixo(@PathVariable UUID requestId, @PathVariable String carro, @RequestBody Fixo fixo){



    }



}
