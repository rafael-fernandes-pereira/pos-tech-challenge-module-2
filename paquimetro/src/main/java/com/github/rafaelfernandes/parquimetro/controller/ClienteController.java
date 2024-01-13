package com.github.rafaelfernandes.parquimetro.controller;

import com.github.rafaelfernandes.parquimetro.controller.response.Message;
import com.github.rafaelfernandes.parquimetro.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.validation.ValidacaoRequest;
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

    @Autowired private ValidacaoRequest validacaoRequest;

    @GetMapping("/{requestId}")
    private ResponseEntity<Message> findById(@PathVariable UUID requestId){

        Optional<ClienteEntity> clienteEntity = repository.findById(requestId);

        if (clienteEntity.isPresent()){
            Cliente cliente = ClienteDto.from(clienteEntity.get());

            Message message = new Message(cliente, Boolean.FALSE, null);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(message);

        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build()
        ;
    }

    @PostMapping("/")
    private ResponseEntity<Message> cadastrarNovoCliente(@RequestBody Cliente cliente, UriComponentsBuilder uriComponentsBuilder){

        List<String> erros = validacaoRequest.execute(cliente);

        if (!erros.isEmpty()){
            Message message = new Message(null, Boolean.TRUE, erros);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(message);
        }

        ClienteEntity clienteASalvar = ClienteDto.from(cliente, Boolean.TRUE);

        ClienteEntity clienteSalvo = repository.save(clienteASalvar);

        Cliente clienteResponse = ClienteDto.from(clienteSalvo);

        Message message = new Message(clienteResponse, Boolean.FALSE, null);

        URI location = uriComponentsBuilder
                .path("clientes/{id}")
                .buildAndExpand(clienteSalvo.id())
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .body(message);

    }

}
