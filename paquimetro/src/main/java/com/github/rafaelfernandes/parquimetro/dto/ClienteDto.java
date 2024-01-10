package com.github.rafaelfernandes.parquimetro.dto;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.entity.ContatoEntity;
import com.github.rafaelfernandes.parquimetro.entity.EnderecoEntity;

import java.util.UUID;

public class ClienteDto {

    public static ClienteEntity from(Cliente cliente, Boolean isNew){

        EnderecoEntity enderecoEntity = new EnderecoEntity(
                cliente.endereco().logradouro(),
                cliente.endereco().numero(),
                cliente.endereco().observacao(),
                cliente.endereco().bairro(),
                cliente.endereco().cidade(),
                cliente.endereco().estado()
        );

        ContatoEntity contatoEntity = new ContatoEntity(
                cliente.contato().email(),
                cliente.contato().telefone()
        );


        return new ClienteEntity(
                isNew? UUID.randomUUID() : cliente.id(),
                cliente.nome(),
                cliente.documento(),
                enderecoEntity,
                cliente.forma_pagamento(),
                contatoEntity,
                cliente.carros()
        );
    }

}
