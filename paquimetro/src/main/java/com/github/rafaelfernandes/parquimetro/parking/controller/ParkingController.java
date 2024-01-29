package com.github.rafaelfernandes.parquimetro.parking.controller;

import com.github.rafaelfernandes.parquimetro.customer.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.customer.controller.response.CustomerError;
import com.github.rafaelfernandes.parquimetro.parking.controller.request.FixTime;
import com.github.rafaelfernandes.parquimetro.parking.controller.response.open.ParkingOpened;
import com.github.rafaelfernandes.parquimetro.parking.controller.response.close.ParkingFinished;
import com.github.rafaelfernandes.parquimetro.parking.enums.ParkingType;
import com.github.rafaelfernandes.parquimetro.parking.service.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Parking", description = "Parking Endpoint - Responsável por todo gerenciamento de estacionamento")
public class ParkingController {

    @Autowired private ParkingService parkingService;

    @Operation(summary = "Obter tempo aberto")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "200", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingOpened.class))}),
            @ApiResponse(description = "Cliente ou carro não encontrado", responseCode = "404", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))})
    })
    @GetMapping("/{customerId}/{car}/open")
    ResponseEntity<ParkingOpened> getOpened(@PathVariable UUID customerId, @PathVariable String car){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.parkingService.getOpenedByCustomerIdAndCar(customerId, car));

    }

    @Operation(summary = "Iniciar tempo fixo")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "201", headers = {@Header(name = "/parking/customerId/car/open", description = "Url do parking criado")}),
            @ApiResponse(description = "Cliente ou carro não encontrado", responseCode = "404", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))}),
            @ApiResponse(description = "Tempo vazio, menor ou igual a zero / Registrar o mesmo tempo", responseCode = "400", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))})
    })
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

    @Operation(summary = "Iniciar tempo variável")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "201", headers = {@Header(name = "/parking/customerId/car/open", description = "Url do parking criado")}),
            @ApiResponse(description = "Cliente ou carro não encontrado", responseCode = "404", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))}),
            @ApiResponse(description = "Pagamento PIX / Registrar o mesmo tempo", responseCode = "400", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))})
    })
    @PostMapping("/{customerId}/{car}/hour")
    ResponseEntity<Void> createParkingHour(@PathVariable UUID customerId,
                                           @PathVariable String car,
                                           UriComponentsBuilder uriComponentsBuilder){

        this.parkingService.register(ParkingType.HOUR, customerId, car, 1L);

        URI location = uriComponentsBuilder
                .path("parking/{customerId}/{car}/open")
                .buildAndExpand(customerId, car)
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .build();
    }

    @Operation(summary = "Finalizar tempo variável")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "201", headers = {@Header(name = "/parking/finishedId/finish", description = "Url do parking encerrado")}),
            @ApiResponse(description = "Cliente ou carro não encontrado", responseCode = "404", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))})
    })
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

    @Operation(summary = "Obter tempo encerrado")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "200", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingFinished.class))}),
            @ApiResponse(description = "Cliente ou carro não encontrado", responseCode = "404", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))})
    })
    @GetMapping("/{parkingId}/finish")
    ResponseEntity<ParkingFinished> getParkingFinished(@PathVariable UUID parkingId){

        ParkingFinished parking = this.parkingService.getFinished(parkingId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(parking);

    }





}
