package com.github.rafaelfernandes.parquimetro.estacionamento.controller;

import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.request.Fixo;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.Estacionamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.MessageEstacionamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.dto.MessageEstacionamentoDTO;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import com.github.rafaelfernandes.parquimetro.estacionamento.service.EstacionamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/estacionamento")
public class EstacionamentoController {

    @Autowired private EstacionamentoService estacionamentoService;

    @GetMapping("/{requestId}/{carro}")
    ResponseEntity<MessageEstacionamento> obterDados(@PathVariable UUID requestId, @PathVariable String carro){

        MessageEstacionamento estacionamento = this.estacionamentoService.obterAbertoPorCarro(requestId, carro);

        return ResponseEntity
                .status(estacionamento.http_status_code())
                .body(estacionamento);

    }

    @PostMapping("/{requestId}/{carro}/fixo")
    ResponseEntity<MessageEstacionamento> registrarFixo(@PathVariable UUID requestId, @PathVariable String carro, @RequestBody Fixo fixo, UriComponentsBuilder uriComponentsBuilder){

        MessageEstacionamento messageEstacionamento = this.estacionamentoService.registrar(TipoPeriodo.FIXO, requestId, carro, fixo.duracao_fixa());

        URI location = uriComponentsBuilder
                .path("estacionamento/{requestId}/{carro}")
                .buildAndExpand(requestId, carro)
                .toUri();

        return ResponseEntity
                .status(messageEstacionamento.http_status_code())
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .body(messageEstacionamento);
    }



}
