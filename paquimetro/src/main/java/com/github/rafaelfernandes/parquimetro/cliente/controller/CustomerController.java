package com.github.rafaelfernandes.parquimetro.cliente.controller;

import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageCarros;
import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageCliente;
import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageFormaPagamento;
import com.github.rafaelfernandes.parquimetro.cliente.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.cliente.service.CarroService;
import com.github.rafaelfernandes.parquimetro.cliente.service.CustomerService;
import com.github.rafaelfernandes.parquimetro.cliente.service.FormaPagamentoService;
import com.github.rafaelfernandes.parquimetro.cliente.validation.ValidacaoRequest;
import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired private ClienteRepository repository;

    @Autowired private ValidacaoRequest validacaoRequest;

    @Autowired private CustomerService customerService;
    @Autowired private CarroService carroService;

    @Autowired private FormaPagamentoService formaPagamentoService;

    @GetMapping("/{requestId}")
    private ResponseEntity<Customer> findById(@PathVariable UUID requestId){

        Customer customer = this.customerService.findBydId(requestId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customer);

    }

    @PostMapping("/")
    private ResponseEntity<Customer> createCustomer(@RequestBody Customer customer, UriComponentsBuilder uriComponentsBuilder){

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
    ResponseEntity<Iterable<MessageCliente>> getAll(Pageable pageable){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.customerService.obterTodos(pageable));
    }

    @PutMapping("/{requestId}")
    ResponseEntity<Void> alterar(@PathVariable UUID requestId, @RequestBody Customer customer){

        Boolean updated = this.customerService.alterar(requestId, customer);

        return ResponseEntity
                .status(updated ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND)
                .build();
    }

    @DeleteMapping("/{requestId}")
    ResponseEntity<Void> deletar(@PathVariable("requestId") UUID requestId){
        Boolean deleted = this.customerService.deletar(requestId);

        return ResponseEntity
                .status(deleted ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND)
                .build();

    }

    @PutMapping("/{requestId}/cars")
    ResponseEntity<MessageCarros> incluirCarro(@PathVariable UUID requestId, @RequestBody List<String> carros){

        MessageCarros messageCarros = this.carroService.incluir(requestId, carros);

        return ResponseEntity
                .status(messageCarros.httpStatusCode())
                .body(messageCarros);

    }

    @GetMapping("/{requestId}/cars")
    ResponseEntity<MessageCarros> obterCarros(@PathVariable UUID requestId){

        MessageCarros messageCarros = this.carroService.obter(requestId);

        return ResponseEntity
                .status(messageCarros.httpStatusCode())
                .body(messageCarros);
    }

    @DeleteMapping("/{requestId}/{carro}")
    ResponseEntity<MessageCarros> excluirCarros(@PathVariable UUID requestId, @PathVariable String carro){

        MessageCarros messageCarros = this.carroService.deletar(requestId, carro);

        return ResponseEntity
                .status(messageCarros.httpStatusCode())
                .body(messageCarros);

    }

    @PutMapping("/{requestId}/formaPagamento")
    ResponseEntity<MessageFormaPagamento> alterarFormaPagamento(@PathVariable UUID requestId, @RequestBody String formaPagamento){

        MessageFormaPagamento messageFormaPagamento = this.formaPagamentoService.alterar(requestId, formaPagamento);

        return ResponseEntity
                .status(messageFormaPagamento.httpStatusCode())
                .body(messageFormaPagamento);


    }

    @GetMapping("/{requestId}/formaPagamento")
    ResponseEntity<MessageFormaPagamento> obterFormaPagamento(@PathVariable UUID requestId){

        MessageFormaPagamento messageFormaPagamento = this.formaPagamentoService.obter(requestId);

        return ResponseEntity
                .status(messageFormaPagamento.httpStatusCode())
                .body(messageFormaPagamento);

    }

}
