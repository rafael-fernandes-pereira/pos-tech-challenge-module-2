package com.github.rafaelfernandes.parquimetro.cliente.controller;

import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.cliente.service.CarService;
import com.github.rafaelfernandes.parquimetro.cliente.service.CustomerService;
import com.github.rafaelfernandes.parquimetro.cliente.service.FormaPagamentoService;
import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired private CustomerService customerService;
    @Autowired private CarService carService;

    @Autowired private FormaPagamentoService formaPagamentoService;

    @GetMapping("/{customerId}")
    private ResponseEntity<Customer> findById(@PathVariable final UUID customerId){

        Customer customer = this.customerService.findBydId(customerId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customer);

    }

    @PostMapping("/")
    private ResponseEntity<Customer> createCustomer(@RequestBody final Customer customer, UriComponentsBuilder uriComponentsBuilder){

        Customer customerSaved = this.customerService.create(customer);

        URI location = uriComponentsBuilder
                .path("customers/{id}")
                .buildAndExpand(customerSaved.id())
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .body(customerSaved);

    }

    @GetMapping("/")
    ResponseEntity<Page<Customer>> getAll(Pageable pageable){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.customerService.getAll(pageable));
    }

    @PutMapping("/{customerId}")
    ResponseEntity<Void> update(@PathVariable UUID customerId, @RequestBody Customer customer){

        this.customerService.update(customerId, customer);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/{customerId}")
    ResponseEntity<Void> delete(@PathVariable("customerId") UUID customerId){

        this.customerService.delete(customerId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();

    }

    @PutMapping("/{customerId}/cars")
    ResponseEntity<Void> addCar(@PathVariable UUID customerId, @RequestBody List<String> cars){

        this.carService.addCars(customerId, cars);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();

    }

    @GetMapping("/{customerId}/cars")
    ResponseEntity<List<String>> getCars(@PathVariable UUID customerId){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.carService.getCars(customerId));
    }

    @DeleteMapping("/{customerId}/{car}")
    ResponseEntity<Void> delete(@PathVariable UUID customerId, @PathVariable String car){

        this.carService.delete(customerId, car);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{customerId}/paymentMethod")
    ResponseEntity<Void> changePaymentMethod(@PathVariable UUID customerId, @RequestBody String paymentMethod){

        this.formaPagamentoService.change(customerId, paymentMethod);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{customerId}/paymentMethod")
    ResponseEntity<PaymentMethod> getPaymentMethod(@PathVariable UUID customerId){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.formaPagamentoService.get(customerId));
    }

}
