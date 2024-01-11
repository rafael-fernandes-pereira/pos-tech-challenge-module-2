package com.github.rafaelfernandes.parquimetro.dto;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.Contato;
import com.github.rafaelfernandes.parquimetro.controller.Endereco;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.entity.ContatoEntity;
import com.github.rafaelfernandes.parquimetro.entity.EnderecoEntity;

import java.util.UUID;

public class ClienteDto {

    public static ClienteEntity from(Cliente cliente, Boolean isNew){

        EnderecoEntity enderecoEntity = new EnderecoEntity(
                cliente.endereco().logradouro(),
                cliente.endereco().numero(),
                cliente.endereco().complemento(),
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


    public static Cliente from(ClienteEntity cliente){

        Endereco endereco = new Endereco(
                cliente.endereco().logradouro(),
                cliente.endereco().numero(),
                cliente.endereco().complemento(),
                cliente.endereco().bairro(),
                cliente.endereco().cidade(),
                cliente.endereco().estado()
        );

        Contato contato = new Contato(
                cliente.contato().email(),
                cliente.contato().telefone()
        );


        return new Cliente(
                cliente.id(),
                cliente.nome(),
                cliente.documento(),
                endereco,
                cliente.forma_pagamento(),
                contato,
                cliente.carros()
        );
    }

}
