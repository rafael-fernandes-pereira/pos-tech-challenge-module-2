package com.github.rafaelfernandes.parquimetro.customer.controller;

import com.github.rafaelfernandes.parquimetro.customer.controller.response.CustomerError;
import com.github.rafaelfernandes.parquimetro.customer.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.customer.exception.CustomerNotFoundException;
import com.github.rafaelfernandes.parquimetro.customer.service.CarService;
import com.github.rafaelfernandes.parquimetro.customer.service.CustomerService;
import com.github.rafaelfernandes.parquimetro.customer.service.PaymentMethodService;
import com.github.rafaelfernandes.parquimetro.customer.controller.request.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customer", description = "Customer Endpoint - Responsável por todo gerenciamento de clientes")
public class CustomerController {

    @Autowired private CustomerService customerService;
    @Autowired private CarService carService;

    @Autowired private PaymentMethodService paymentMethodService;

    @Operation(summary = "02 - Obter cliente")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "200", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class))}),
            @ApiResponse(description = "Cliente não encontrado", responseCode = "404", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))})

    })
    @GetMapping("/{customerId}")
    private ResponseEntity<Customer> findById(@PathVariable final UUID customerId){

        Customer customer = this.customerService.findBydId(customerId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customer);

    }

    @Operation(summary = "01 - Cadastrar cliente")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "201", headers = {@Header(name = "/customer/customerId", description = "Location do customer criado")}),
            @ApiResponse(description = "Problemas de validação e/ou tentativa de registrar duplidade", responseCode = "400", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))})

    })
    @PostMapping("/")
    private ResponseEntity<Void> createCustomer(@RequestBody final Customer customer, UriComponentsBuilder uriComponentsBuilder){

        Customer customerSaved = this.customerService.create(customer);

        URI location = uriComponentsBuilder
                .path("customers/{id}")
                .buildAndExpand(customerSaved.id())
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .build();

    }

    @Operation(summary = "03 - Obter clientes", description = "03")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "200", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))})
    })
    @GetMapping("/")
    ResponseEntity<Page<Customer>> getAll(Pageable pageable){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.customerService.getAll(pageable));
    }

    @Operation(summary = "Alterar cliente")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "201"),
            @ApiResponse(description = "Problemas de validação e/ou tentativa de registrar duplidade", responseCode = "400", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))})
    })
    @PutMapping("/{customerId}")
    ResponseEntity<Void> update(@PathVariable UUID customerId, @RequestBody Customer customer){

        this.customerService.update(customerId, customer);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "Deletar cliente")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "201")
    })
    @DeleteMapping("/{customerId}")
    ResponseEntity<Void> delete(@PathVariable("customerId") UUID customerId){

        this.customerService.delete(customerId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();

    }

    @Operation(summary = "Adicionar carros")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "200"),
            @ApiResponse(description = "Cliente não encontrado", responseCode = "404", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))})
    })
    @PutMapping("/{customerId}/cars")
    ResponseEntity<Void> addCar(@PathVariable UUID customerId, @RequestBody List<String> cars){

        this.carService.addCars(customerId, cars);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();

    }

    @Operation(summary = "Exibir carros")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "200"),
            @ApiResponse(description = "Cliente não encontrado", responseCode = "404", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))})
    })
    @GetMapping("/{customerId}/cars")
    ResponseEntity<List<String>> getCars(@PathVariable UUID customerId){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.carService.getCars(customerId));
    }

    @Operation(summary = "Deletar carros")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "200"),
            @ApiResponse(description = "Cliente não encontrado", responseCode = "404", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))}),
            @ApiResponse(description = "Deletar o único carro / Deletar carro não existente / Enviar dados vazios", responseCode = "400", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))})
    })
    @DeleteMapping("/{customerId}/{car}")
    ResponseEntity<Void> deleteCar(@PathVariable UUID customerId, @PathVariable String car){

        this.carService.delete(customerId, car);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "Mudar método de pagamento")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "201"),
            @ApiResponse(description = "Método de pagamento inválido", responseCode = "400", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))}),
            @ApiResponse(description = "Cliente não encontrado", responseCode = "404", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))})
    })
    @PutMapping("/{customerId}/paymentMethod")
    ResponseEntity<Void> changePaymentMethod(@PathVariable UUID customerId, @RequestBody @Parameter(description = "Método de pagamento", examples = {@ExampleObject("PIX"), @ExampleObject("CREDIT_CARD"), @ExampleObject("DEBIT_CARD")}) String paymentMethod){

        this.paymentMethodService.change(customerId, paymentMethod);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "Buscar método de pagamento")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "200"),
            @ApiResponse(description = "Cliente não encontrado", responseCode = "404", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerError.class))})
    })
    @GetMapping("/{customerId}/paymentMethod")
    ResponseEntity<PaymentMethod> getPaymentMethod(@PathVariable UUID customerId){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.paymentMethodService.get(customerId));
    }

}
