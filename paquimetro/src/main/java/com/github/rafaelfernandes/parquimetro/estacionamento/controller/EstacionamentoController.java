package com.github.rafaelfernandes.parquimetro.estacionamento.controller;

import com.github.rafaelfernandes.parquimetro.estacionamento.controller.request.Fixo;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.ParkingOpened;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.MessageAberto;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.MessageEncerrado;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.ParkingType;
import com.github.rafaelfernandes.parquimetro.estacionamento.service.EstacionamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/parking")
public class EstacionamentoController {

    @Autowired private EstacionamentoService estacionamentoService;

    @GetMapping("/{customerId}/{car}/opened")
    ResponseEntity<ParkingOpened> getOpened(@PathVariable UUID customerId, @PathVariable String car){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.estacionamentoService.getOpenedByCustomerIdAndCar(customerId, car));

    }

    @PostMapping("/{requestId}/{car}/fixo")
    ResponseEntity<MessageAberto> registrarFixo(@PathVariable UUID requestId,
                                                @PathVariable String carro,
                                                @RequestBody Fixo fixo,
                                                UriComponentsBuilder uriComponentsBuilder){

        MessageAberto messageAberto = this.estacionamentoService.registrar(ParkingType.FIXO, requestId, carro, fixo.duracao_fixa());

        URI location = uriComponentsBuilder
                .path("estacionamento/{requestId}/{car}/aberto")
                .buildAndExpand(requestId, carro)
                .toUri();

        return ResponseEntity
                .status(messageAberto.http_status_code())
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .body(messageAberto);
    }

    @PostMapping("/{requestId}/{car}/hora")
    ResponseEntity<MessageAberto> registrarHora(@PathVariable UUID requestId, @PathVariable String carro, UriComponentsBuilder uriComponentsBuilder){

        MessageAberto messageAberto = this.estacionamentoService.registrar(ParkingType.HORA, requestId, carro, null);

        URI location = uriComponentsBuilder
                .path("estacionamento/{requestId}/{car}/aberto")
                .buildAndExpand(requestId, carro)
                .toUri();

        return ResponseEntity
                .status(messageAberto.http_status_code())
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .body(messageAberto);
    }

    @PostMapping("/{requestId}/{car}/finalizar")
    ResponseEntity<MessageEncerrado> finalizar(@PathVariable UUID requestId, @PathVariable String carro, UriComponentsBuilder uriComponentsBuilder){

        MessageEncerrado messageEncerrado = this.estacionamentoService.finalizar(requestId, carro);

        if (!messageEncerrado.erros().isEmpty()) {
            return ResponseEntity
                    .status(messageEncerrado.http_status_code())
                    .body(messageEncerrado);
        }

        UUID encerradoId = messageEncerrado.estacionamentos().get(0).id();

        URI location = uriComponentsBuilder
                .path("estacionamento/{requestId}/encerrado")
                .buildAndExpand(encerradoId)
                .toUri();

        return ResponseEntity
                .status(messageEncerrado.http_status_code())
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .body(messageEncerrado);


    }

    @GetMapping("/{requestId}/encerrado")
    ResponseEntity<MessageEncerrado> obterDadosEncerrado(@PathVariable UUID requestId){

        MessageEncerrado estacionamento = this.estacionamentoService.obterEncerrado(requestId);

        return ResponseEntity
                .status(estacionamento.http_status_code())
                .body(estacionamento);

    }





}
