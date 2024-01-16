package com.github.rafaelfernandes.parquimetro.cliente.service;

import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageCliente;
import com.github.rafaelfernandes.parquimetro.cliente.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.cliente.dto.MessageDTO;
import com.github.rafaelfernandes.parquimetro.cliente.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.cliente.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.cliente.validation.ValidacaoRequest;
import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Cliente;

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

    public MessageCliente registro(Cliente cliente){

        List<String> erros = validacaoRequest.cliente(cliente);

        if (!erros.isEmpty()){
            return MessageDTO.clienteError(HttpStatus.BAD_REQUEST, erros);
        }

        ClienteEntity clienteASalvar = ClienteDto.from(cliente, Boolean.TRUE);

        try {

            ClienteEntity clienteSalvo = repository.insert(clienteASalvar);

            List<Cliente> clientes = ClienteDto.getListFrom(clienteSalvo);

             return MessageDTO.clienteSuccess(HttpStatus.CREATED, clientes);

        } catch (DuplicateKeyException ex){
            erros.add("Campo documento e/ou campo email já existem!");
            return MessageDTO.clienteError(HttpStatus.CONFLICT, erros);
        }


    }

    public MessageCliente obterPorId(UUID requestId){
        Optional<ClienteEntity> clienteEntity = repository.findById(requestId);

        if (clienteEntity.isPresent()) {

            List<Cliente> clientes = ClienteDto.getListFrom(clienteEntity.get());

            return MessageDTO.clienteSuccess(HttpStatus.OK, clientes);
        }

        return MessageDTO.clienteError(HttpStatus.NOT_FOUND, null);

    }

    public Iterable<MessageCliente> obterTodos(Pageable pageable){

        Page<ClienteEntity> clienteEntities =  this.repository.findAll(pageable);

        if (clienteEntities.isEmpty()) return new ArrayList<>();

        return clienteEntities.stream()
                .map(clienteEntity -> MessageDTO.clienteSuccess(HttpStatus.OK, ClienteDto.getListFrom(clienteEntity)))
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
