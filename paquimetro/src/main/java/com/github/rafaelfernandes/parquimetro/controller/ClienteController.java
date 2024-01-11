package com.github.rafaelfernandes.parquimetro.controller;

import com.github.rafaelfernandes.parquimetro.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/{requestId}")
    private ResponseEntity<Cliente> findById(@PathVariable UUID requestId){

        Optional<ClienteEntity> clienteEntity = repository.findById(requestId);

        if (clienteEntity.isPresent()){
            Cliente cliente = ClienteDto.from(clienteEntity.get());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(cliente);

        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build()
        ;
    }

    @PostMapping("/")
    private ResponseEntity<Void> cadastrarNovoCliente(@RequestBody Cliente cliente, UriComponentsBuilder uriComponentsBuilder){

        ClienteEntity clienteASalvar = ClienteDto.from(cliente, Boolean.TRUE);

        ClienteEntity clienteSalvo = repository.save(clienteASalvar);

        URI location = uriComponentsBuilder
                .path("clientes/{id}")
                .buildAndExpand(clienteSalvo.id())
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .build();

    }

}
