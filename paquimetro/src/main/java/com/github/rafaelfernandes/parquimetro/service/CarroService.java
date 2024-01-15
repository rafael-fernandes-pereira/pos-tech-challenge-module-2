package com.github.rafaelfernandes.parquimetro.service;

import com.github.rafaelfernandes.parquimetro.controller.response.MessageCarros;
import com.github.rafaelfernandes.parquimetro.controller.response.MessageCliente;
import com.github.rafaelfernandes.parquimetro.dto.MessageDTO;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CarroService {

    @Autowired private ClienteRepository repository;

    public MessageCarros incluir(UUID requestId, List<String> carros){

        List<String> erros = new ArrayList<>();

        if (carros == null || carros.isEmpty() ){
            return MessageDTO.carrosError(HttpStatus.BAD_REQUEST, erros);
        }

        Optional<ClienteEntity> cliente = this.repository.findById(requestId);

        if (cliente.isEmpty()) {
            return MessageDTO.carrosError(HttpStatus.NOT_FOUND, erros);
        }

        List<String> carrosSalvos = cliente.get().carros();

        Set<String> novosCarros = new TreeSet<>();

        novosCarros.addAll(carrosSalvos.stream().toList());
        novosCarros.addAll(carros.stream().toList());

        ClienteEntity clienteEntity = new ClienteEntity(
                requestId,
                cliente.get().nome(),
                cliente.get().documento(),
                cliente.get().endereco(),
                cliente.get().forma_pagamento(),
                cliente.get().contato(),
                novosCarros.stream().toList()
        );

        this.repository.save(clienteEntity);

        return MessageDTO.carrosSuccess(HttpStatus.NO_CONTENT, null);

    }

    public MessageCarros obterCarros(UUID requestId){

        Optional<ClienteEntity> cliente = this.repository.findById(requestId);

        return cliente
                .map(clienteEntity -> MessageDTO.carrosSuccess(HttpStatus.OK, clienteEntity.carros()))
                .orElseGet(() -> MessageDTO.carrosError(HttpStatus.NOT_FOUND, null));


    }

}
