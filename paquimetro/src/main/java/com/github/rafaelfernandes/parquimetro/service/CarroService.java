package com.github.rafaelfernandes.parquimetro.service;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CarroService {

    @Autowired private ClienteRepository repository;

    public Boolean incluir(UUID requestId, List<String> carros){

        if (carros == null || carros.isEmpty() ) return Boolean.FALSE;

        Optional<ClienteEntity> cliente = this.repository.findById(requestId);

        if (cliente.isEmpty()) return Boolean.FALSE;

        List<String> carrosSalvos = cliente.get().carros();

        Set<String> novosCarros = new TreeSet<>();

        novosCarros.addAll(carrosSalvos.stream().toList());
        novosCarros.addAll(carros.stream().toList());


        if (novosCarros.isEmpty()) return Boolean.FALSE;

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

        return Boolean.TRUE;






    }

}
