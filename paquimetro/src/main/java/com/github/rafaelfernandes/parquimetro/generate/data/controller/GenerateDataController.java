package com.github.rafaelfernandes.parquimetro.generate.data.controller;

import com.github.rafaelfernandes.parquimetro.customer.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.customer.entity.ContactEntity;
import com.github.rafaelfernandes.parquimetro.customer.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.customer.service.CustomerService;
import com.github.rafaelfernandes.parquimetro.customer.service.PaymentMethodService;
import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingOpenedEntity;
import com.github.rafaelfernandes.parquimetro.parking.enums.ParkingType;
import com.github.rafaelfernandes.parquimetro.parking.repository.ParkingOpenedRepository;
import com.github.rafaelfernandes.parquimetro.parking.service.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/generate/data")
@Tag(name = "Generate Data", description = "Enpoint para gerar dados de testes :)")
public class GenerateDataController {


    @Autowired
    private ParkingService parkingService;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Operation(summary = "Gerar cliente")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "200", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class))})
    })
    @GetMapping("/customer")
    ResponseEntity<Customer> generateCustomer(){

        Customer customer = GenerateData.customer(true);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customer);

    }

    @Operation(summary = "Alterar inicio e fim de estacionamento")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "200")
    })
    @PutMapping("/parking/updateStartEnd/{customerId}/{car}")
    ResponseEntity<Void> updateParking(@PathVariable UUID customerId, @PathVariable String car, @RequestBody ParkingData parkingData){

        this.paymentMethodService.change(customerId, PaymentMethod.CREDIT_CARD.name());

        this.parkingService.updateStartEnd(customerId, car, parkingData.start_minus_minus(), parkingData.end_minutes_plus());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();

    }


}
