package com.github.rafaelfernandes.parquimetro.controller;

import com.github.rafaelfernandes.parquimetro.controller.request.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.response.MessageCarros;
import com.github.rafaelfernandes.parquimetro.controller.response.MessageCliente;
import com.github.rafaelfernandes.parquimetro.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.service.CarroService;
import com.github.rafaelfernandes.parquimetro.service.ClienteService;
import com.github.rafaelfernandes.parquimetro.validation.ValidacaoRequest;
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
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired private ClienteRepository repository;

    @Autowired private ValidacaoRequest validacaoRequest;

    @Autowired private ClienteService clienteService;
    @Autowired private CarroService carroService;

    @GetMapping("/{requestId}")
    private ResponseEntity<MessageCliente> findById(@PathVariable UUID requestId){

        MessageCliente messageCliente = this.clienteService.obterPorId(requestId);

        return ResponseEntity
                .status(messageCliente.httpStatusCode())
                .body(messageCliente);

    }

    @PostMapping("/")
    private ResponseEntity<MessageCliente> cadastrarNovoCliente(@RequestBody Cliente cliente, UriComponentsBuilder uriComponentsBuilder){

        MessageCliente messageCliente = this.clienteService.registro(cliente);

        if (!messageCliente.errors().isEmpty()) {
            return ResponseEntity
                    .status(messageCliente.httpStatusCode())
                    .body(messageCliente);
        }

        URI location = uriComponentsBuilder
                .path("clientes/{id}")
                .buildAndExpand(messageCliente.clientes().get(0).id())
                .toUri();

        return ResponseEntity
                .status(messageCliente.httpStatusCode())
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .body(messageCliente);



    }

    @GetMapping("/")
    ResponseEntity<Iterable<MessageCliente>> getAll(Pageable pageable){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.clienteService.obterTodos(pageable));
    }

    @PutMapping("/{requestId}")
    ResponseEntity<Void> alterar(@PathVariable UUID requestId, @RequestBody Cliente cliente){

        Boolean updated = this.clienteService.alterar(requestId, cliente);

        return ResponseEntity
                .status(updated ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND)
                .build();
    }

    @DeleteMapping("/{requestId}")
    ResponseEntity<Void> deletar(@PathVariable("requestId") UUID requestId){
        Boolean deleted = this.clienteService.deletar(requestId);

        return ResponseEntity
                .status(deleted ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND)
                .build();

    }

    @PutMapping("/{requestId}/carros")
    ResponseEntity<MessageCarros> incluirCarro(@PathVariable UUID requestId, @RequestBody List<String> carros){

        MessageCarros messageCarros = this.carroService.incluir(requestId, carros);

        return ResponseEntity
                .status(messageCarros.httpStatusCode())
                .body(messageCarros);

    }

    @GetMapping("/{requestId}/carros")
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


}
