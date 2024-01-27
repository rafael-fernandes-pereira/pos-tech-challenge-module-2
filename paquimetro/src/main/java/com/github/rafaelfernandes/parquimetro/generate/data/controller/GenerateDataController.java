package com.github.rafaelfernandes.parquimetro.generate.data.controller;

import com.github.rafaelfernandes.parquimetro.customer.controller.request.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/generate/data")
@Tag(name = "Generate Data", description = "Enpoint para gerar dados de testes :)")
public class GenerateDataController {


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
            @ApiResponse(description = "Sucesso", responseCode = "200", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class))})
    })
    @PostMapping("/parking/{parkingId}")
    ResponseEntity<Void> updateParking(@PathVariable UUID parkingId, @RequestBody ParkingData parkingData){



    }


}
