package com.github.rafaelfernandes.parquimetro.controller;

import com.github.rafaelfernandes.parquimetro.controller.response.Message;
import com.github.rafaelfernandes.parquimetro.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.service.ClienteService;
import com.github.rafaelfernandes.parquimetro.validation.ValidacaoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired private ClienteRepository repository;

    @Autowired private ValidacaoRequest validacaoRequest;

    @Autowired private ClienteService service;

    @GetMapping("/{requestId}")
    private ResponseEntity<Message> findById(@PathVariable UUID requestId){

        Optional<Message> message = this.service.obterPorId(requestId);

        return message.map(value -> ResponseEntity
                .status(HttpStatus.OK)
                .body(value)).orElseGet(() -> ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build());

    }

    @PostMapping("/")
    private ResponseEntity<Message> cadastrarNovoCliente(@RequestBody Cliente cliente, UriComponentsBuilder uriComponentsBuilder){

        Optional<Message> message = this.service.registro(cliente);

        if (message.get().isError()) {
            return ResponseEntity
                    .status(message.get().httpStatusCode())
                    .body(message.get());
        }

        URI location = uriComponentsBuilder
                .path("clientes/{id}")
                .buildAndExpand(message.get().cliente().id())
                .toUri();

        return ResponseEntity
                .status(message.get().httpStatusCode())
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .body(message.get());



    }

    @GetMapping("/")
    ResponseEntity<Iterable<Message>> getAll(Pageable pageable){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.service.obterTodos(pageable).get());
    }

    @PutMapping("/{requestId}")
    ResponseEntity<Void> alterar(@PathVariable UUID requestId, @RequestBody Cliente cliente){

        if (this.service.alterar(requestId, cliente)){
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();

    }

    @DeleteMapping("/{requestId}")
    ResponseEntity<Void> deletar(@PathVariable("requestId") UUID requestId){
        if (this.service.deletar(requestId)){
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .build();
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

}
