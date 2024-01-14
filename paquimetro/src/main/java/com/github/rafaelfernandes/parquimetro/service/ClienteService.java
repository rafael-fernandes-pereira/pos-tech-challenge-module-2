package com.github.rafaelfernandes.parquimetro.service;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.response.Message;
import com.github.rafaelfernandes.parquimetro.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.validation.ValidacaoRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {

    @Autowired private ClienteRepository repository;

    @Autowired private ValidacaoRequest validacaoRequest;

    public Message registro(Cliente cliente){

        List<String> erros = validacaoRequest.execute(cliente);

        if (!erros.isEmpty()){
            return new Message(null, Boolean.TRUE, HttpStatus.BAD_REQUEST.value(), erros);

        }

        ClienteEntity clienteASalvar = ClienteDto.from(cliente, Boolean.TRUE);

        try {

            ClienteEntity clienteSalvo = repository.insert(clienteASalvar);

            Cliente clienteResponse = ClienteDto.from(clienteSalvo);

            return new Message(clienteResponse, Boolean.FALSE, HttpStatus.CREATED.value(), null);

        } catch (DuplicateKeyException ex){
            erros.add("Campo documento e/ou campo email já existem!");
            return new Message(null, Boolean.TRUE, HttpStatus.CONFLICT.value(), erros);
        }


    }

}
