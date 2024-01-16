package com.github.rafaelfernandes.parquimetro.cliente.unit;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Cliente;
import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Contato;
import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageCliente;
import com.github.rafaelfernandes.parquimetro.util.GerarCadastro;
import com.github.rafaelfernandes.parquimetro.cliente.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.cliente.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.cliente.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.cliente.service.ClienteService;
import com.github.rafaelfernandes.parquimetro.util.MongoContainers;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class ClienteServiceTest {

    @Container //
    private static MongoDBContainer mongoDBContainer = MongoContainers.getDefaultContainer();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.auto-index-creation", MongoContainers::getTrue);
    }

    @Autowired private ClienteRepository repository;
    @Autowired private ClienteService service;

    private final Faker faker = new Faker();


    @Test
    void deveRetornarClienteDuplicado(){

        this.repository.deleteAll();

        Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);

        ClienteEntity clienteEntity = ClienteDto.from(cliente, Boolean.TRUE);

        this.repository.insert(clienteEntity);

        MessageCliente messageCliente = this.service.registro(cliente);

        assertThat(messageCliente.errors())
                .anyMatch(erro -> erro.equalsIgnoreCase("Campo documento e/ou campo email já existem!"))
        ;


        String email = faker.internet().emailAddress();

        Long cpf = Long.valueOf(faker.cpf().valid(false));

        Cliente clienteEmail = new Cliente(
                null,
                cliente.nome(),
                cpf,
                cliente.endereco(),
                cliente.forma_pagamento(),
                cliente.contato(),
                cliente.carros()
        );

        messageCliente = this.service.registro(clienteEmail);

        assertThat(messageCliente.errors())
                .anyMatch(erro -> erro.equalsIgnoreCase("Campo documento e/ou campo email já existem!"))
        ;

        Cliente clienteDocumento = new Cliente(
                null,
                cliente.nome(),
                cliente.documento(),
                cliente.endereco(),
                cliente.forma_pagamento(),
                new Contato(
                        email,
                        faker.phoneNumber().cellPhone().replaceAll("[(),\\-, ]", "")
                ),
                cliente.carros()
        );

        messageCliente = this.service.registro(clienteEmail);

        assertThat(messageCliente.errors())
                .anyMatch(erro -> erro.equalsIgnoreCase("Campo documento e/ou campo email já existem!"))
        ;
    }

}
