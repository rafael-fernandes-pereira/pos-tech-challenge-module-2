package com.github.rafaelfernandes.parquimetro.service;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.response.MessageCliente;
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

    @Autowired private GenerateMessage generateMessage;

    public MessageCliente registro(Cliente cliente){

        List<String> erros = validacaoRequest.cliente(cliente);

        if (!erros.isEmpty()){
            return generateMessage.errors(HttpStatus.BAD_REQUEST, erros);
        }

        ClienteEntity clienteASalvar = ClienteDto.from(cliente, Boolean.TRUE);

        try {

            ClienteEntity clienteSalvo = repository.insert(clienteASalvar);

            List<Cliente> clientes = ClienteDto.getListFrom(clienteSalvo);

             return generateMessage.success(HttpStatus.CREATED, clientes);

        } catch (DuplicateKeyException ex){
            erros.add("Campo documento e/ou campo email já existem!");
            return generateMessage.errors(HttpStatus.CONFLICT, erros);
        }


    }

    public MessageCliente obterPorId(UUID requestId){
        Optional<ClienteEntity> clienteEntity = repository.findById(requestId);

        if (clienteEntity.isPresent()) {

            List<Cliente> clientes = ClienteDto.getListFrom(clienteEntity.get());

            return generateMessage.success(HttpStatus.OK, clientes);
        }

        return generateMessage.errors(HttpStatus.NOT_FOUND, null);

    }

    public Iterable<MessageCliente> obterTodos(Pageable pageable){

        Page<ClienteEntity> clienteEntities =  this.repository.findAll(pageable);

        if (clienteEntities.isEmpty()) return new ArrayList<>();

        return clienteEntities.stream()
                .map(clienteEntity -> generateMessage.success(HttpStatus.OK, ClienteDto.getListFrom(clienteEntity)))
                .collect(Collectors.toList());


    }

    public Boolean alterar(UUID requestId, Cliente cliente){

        if (this.repository.existsById(requestId)){
            ClienteEntity entity = ClienteDto.from(cliente, Boolean.FALSE);
            this.repository.save(entity);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;

    }

    public Boolean deletar(UUID requestId) {
        if (this.repository.existsById(requestId)){
            this.repository.deleteById(requestId);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;

    }
}
