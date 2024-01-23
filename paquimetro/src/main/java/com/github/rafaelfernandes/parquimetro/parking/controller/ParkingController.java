package com.github.rafaelfernandes.parquimetro.parking.controller;

import com.github.rafaelfernandes.parquimetro.parking.controller.request.FixTime;
import com.github.rafaelfernandes.parquimetro.parking.controller.response.aberto.ParkingOpened;
import com.github.rafaelfernandes.parquimetro.parking.controller.response.encerrado.ParkingFinished;
import com.github.rafaelfernandes.parquimetro.parking.enums.ParkingType;
import com.github.rafaelfernandes.parquimetro.parking.service.ParkingService;
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

    @GetMapping("/{customerId}/{car}/open")
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
                .path("parking/{requestId}/{car}/open")
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

        this.parkingService.register(ParkingType.HOUR, customerId, car, 1);

        URI location = uriComponentsBuilder
                .path("parking/{requestId}/{car}/open")
                .buildAndExpand(customerId, car)
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .build();
    }

    @PostMapping("/{customerId}/{car}/finish")
    ResponseEntity<ParkingFinished> finish(@PathVariable UUID customerId,
                                           @PathVariable String car,
                                           UriComponentsBuilder uriComponentsBuilder){

        ParkingFinished parkingFinished = this.parkingService.finish(customerId, car);

        UUID finished = parkingFinished.id();

        URI location = uriComponentsBuilder
                .path("parking/{finishedId}/finish")
                .buildAndExpand(finished)
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .body(parkingFinished);


    }

    @GetMapping("/{requestId}/finish")
    ResponseEntity<ParkingFinished> getParkingFinished(@PathVariable UUID requestId){

        ParkingFinished parking = this.parkingService.getFinished(requestId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(parking);

    }





}
