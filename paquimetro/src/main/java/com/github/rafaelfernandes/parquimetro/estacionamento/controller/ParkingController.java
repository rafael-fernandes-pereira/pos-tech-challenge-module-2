package com.github.rafaelfernandes.parquimetro.estacionamento.controller;

import com.github.rafaelfernandes.parquimetro.estacionamento.controller.request.FixTime;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.ParkingOpened;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.MessageEncerrado;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.ParkingType;
import com.github.rafaelfernandes.parquimetro.estacionamento.service.ParkingService;
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
public class ParkingController {

    @Autowired private ParkingService parkingService;

    @GetMapping("/{customerId}/{car}/opened")
    ResponseEntity<ParkingOpened> getOpened(@PathVariable UUID customerId, @PathVariable String car){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.parkingService.getOpenedByCustomerIdAndCar(customerId, car));

    }

    @PostMapping("/{customerId}/{car}/fix")
    ResponseEntity<Void> createParkingFix(@PathVariable UUID customerId,
                                                   @PathVariable String car,
                                                   @RequestBody FixTime fixTime,
                                                   UriComponentsBuilder uriComponentsBuilder){

        this.parkingService.register(ParkingType.FIX, customerId, car, fixTime.duration());

        URI location = uriComponentsBuilder
                .path("parking/{requestId}/{car}/opened")
                .buildAndExpand(customerId, car)
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .build();
    }

    @PostMapping("/{customerId}/{car}/hour")
    ResponseEntity<Void> createParkingHour(@PathVariable UUID customerId,
                                           @PathVariable String car,
                                           UriComponentsBuilder uriComponentsBuilder){

        this.parkingService.register(ParkingType.HOUR, customerId, car, null);

        URI location = uriComponentsBuilder
                .path("parking/{requestId}/{car}/opened")
                .buildAndExpand(customerId, car)
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .build();
    }

    @PostMapping("/{requestId}/{car}/finalizar")
    ResponseEntity<MessageEncerrado> finalizar(@PathVariable UUID requestId, @PathVariable String carro, UriComponentsBuilder uriComponentsBuilder){

        MessageEncerrado messageEncerrado = this.parkingService.finalizar(requestId, carro);

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

        MessageEncerrado estacionamento = this.parkingService.obterEncerrado(requestId);

        return ResponseEntity
                .status(estacionamento.http_status_code())
                .body(estacionamento);

    }





}
