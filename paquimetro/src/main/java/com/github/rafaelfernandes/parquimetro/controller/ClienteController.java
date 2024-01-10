package com.github.rafaelfernandes.parquimetro.controller;

import com.github.rafaelfernandes.parquimetro.enums.FormaPagamento;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private Cliente clienteTest = new Cliente(
            UUID.fromString("7ffb4be7-985c-483e-ac17-bf899a172b4e"),
            "Luisa Antero",
            12345678L,
            new Endereco(
                    "Rua projetada 3",
                    123,
                    "Muro Azul",
                    "Anhumas",
                    "São Paulo",
                    "MG"
            ),
            FormaPagamento.CARTAO_CREDITO,
            new Contato(
                    "luisa.pereira@fiap.com.br",
                    "11999887766"
            ),
            List.of("IUW8E56", "JEZ8A17", "YIT8U05")
    );


    @GetMapping("/{requestId}")
    private ResponseEntity<Cliente> findById(@PathVariable UUID requestId){

        if (requestId.equals(this.clienteTest.id()))
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(this.clienteTest);

        return ResponseEntity.notFound().build();

    }

    @PostMapping("/")
    private ResponseEntity<Void> cadastrarNovoCliente(@RequestBody Cliente cliente){

        Cliente novoCliente = new Cliente(null,
                cliente.nome(),
                cliente.documento(),
                cliente.endereco(),
                cliente.forma_pagamento(),
                cliente.contato(),
                cliente.carros());


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }

}
