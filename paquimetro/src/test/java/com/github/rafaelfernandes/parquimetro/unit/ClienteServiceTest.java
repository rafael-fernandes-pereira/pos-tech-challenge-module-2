package com.github.rafaelfernandes.parquimetro.unit;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.Contato;
import com.github.rafaelfernandes.parquimetro.controller.response.Message;
import com.github.rafaelfernandes.parquimetro.dados.GerarCadastro;
import com.github.rafaelfernandes.parquimetro.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.service.ClienteService;
import com.github.rafaelfernandes.parquimetro.util.MongoContainers;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

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
    @DirtiesContext
    void deveRetornarClienteDuplicado(){

        Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);

        ClienteEntity clienteEntity = ClienteDto.from(cliente, Boolean.TRUE);

        this.repository.insert(clienteEntity);

        Optional<Message> message = this.service.registro(cliente);

        assertThat(message.get().errors())
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

        message = this.service.registro(clienteEmail);

        assertThat(message.get().errors())
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

        message = this.service.registro(clienteEmail);

        assertThat(message.get().errors())
                .anyMatch(erro -> erro.equalsIgnoreCase("Campo documento e/ou campo email já existem!"))
        ;
    }

}