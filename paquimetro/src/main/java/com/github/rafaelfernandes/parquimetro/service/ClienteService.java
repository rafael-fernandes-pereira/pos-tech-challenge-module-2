package com.github.rafaelfernandes.parquimetro.service;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.response.Message;
import com.github.rafaelfernandes.parquimetro.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.validation.ValidacaoRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired private ClienteRepository repository;

    @Autowired private ValidacaoRequest validacaoRequest;

    public Optional<Message> registro(Cliente cliente){

        List<String> erros = validacaoRequest.execute(cliente);

        if (!erros.isEmpty()){
            return Optional.of(new Message(null, Boolean.TRUE, HttpStatus.BAD_REQUEST.value(), erros));

        }

        ClienteEntity clienteASalvar = ClienteDto.from(cliente, Boolean.TRUE);

        try {

            ClienteEntity clienteSalvo = repository.insert(clienteASalvar);

            Cliente clienteResponse = ClienteDto.from(clienteSalvo);

            return Optional.of(new Message(clienteResponse, Boolean.FALSE, HttpStatus.CREATED.value(), null));

        } catch (DuplicateKeyException ex){
            erros.add("Campo documento e/ou campo email já existem!");
            return Optional.of(new Message(null, Boolean.TRUE, HttpStatus.CONFLICT.value(), erros));
        }


    }

    public Optional<Message> obterPorId(UUID requestId){
        Optional<ClienteEntity> clienteEntity = repository.findById(requestId);

        if (clienteEntity.isPresent()) {
            Cliente cliente = ClienteDto.from(clienteEntity.get());

            return Optional.of(new Message(cliente, Boolean.FALSE, HttpStatus.OK.value(), null));
        }

        return Optional.empty();

    }

    public Optional<Iterable<Message>> obterTodos(Pageable pageable){

        Page<ClienteEntity> clienteEntities =  this.repository.findAll(pageable);

        if (clienteEntities.isEmpty()) return Optional.of(new ArrayList<>());

        Iterable<Message> messages = clienteEntities.stream()
                .map(clienteEntity -> new Message(ClienteDto.from(clienteEntity), Boolean.TRUE, HttpStatus.OK.value(), null))
                .collect(Collectors.toList());

        return Optional.of(messages);


    }

    public Boolean alterar(UUID requestId, Cliente cliente){

        if (this.repository.existsById(requestId)){
            ClienteEntity entity = ClienteDto.from(cliente, Boolean.FALSE);
            this.repository.save(entity);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;

    }

}
