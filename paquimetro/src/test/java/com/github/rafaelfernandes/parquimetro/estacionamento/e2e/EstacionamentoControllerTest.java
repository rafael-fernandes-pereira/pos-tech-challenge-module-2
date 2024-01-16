package com.github.rafaelfernandes.parquimetro.estacionamento.e2e;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Cliente;
import com.github.rafaelfernandes.parquimetro.cliente.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.cliente.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.cliente.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import com.github.rafaelfernandes.parquimetro.util.ClienteCarro;
import com.github.rafaelfernandes.parquimetro.util.GerarCadastro;
import com.github.rafaelfernandes.parquimetro.util.MongoContainers;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class EstacionamentoControllerTest {

    @Container //
    private static MongoDBContainer mongoDBContainer = MongoContainers.getDefaultContainer();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.auto-index-creation", MongoContainers::getTrue);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClienteRepository repository;

    @BeforeEach
    void setup(){
        repository.deleteAll();
    }

    @NotNull
    private ClienteCarro cadastrarNovoCliente() {
        Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);
        ClienteEntity clienteEntity = ClienteDto.from(cliente, Boolean.TRUE);

        ClienteEntity clienteSavo = repository.save(clienteEntity);

        return new ClienteCarro(clienteSavo.id(), clienteSavo.carros().get(0));
    }

    @Test
    void deveRetornarEstacionamento(){

        ClienteCarro clienteCarro = cadastrarNovoCliente();

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/estacionamento/"+ clienteCarro.id() + "/" + clienteCarro.carro(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.estacionamentos[0].cliente_id");

        assertThat(id).isEqualTo(clienteCarro.id().toString());

        String carro = documentContext.read("$.estacionamentos[0].carro");

        assertThat(carro).isEqualTo(clienteCarro.carro());

        String formaPagamento = documentContext.read("$.estacionamentos[0].forma_pagamento");

        assertThat(formaPagamento).isEqualTo(FormaPagamento.PIX.name());

        String tipoPeriodo = documentContext.read("$.estacionamentos[0].tipo_periodo");

        assertThat(tipoPeriodo).isEqualTo(TipoPeriodo.FIXO.name());

        Integer duracaoFixa = documentContext.read("$.estacionamentos[0].duracao_fixa");

        assertThat(duracaoFixa).isEqualTo(3);

        String inicio = documentContext.read("$.estacionamentos[0].inicio");

        assertThat(inicio).isEqualTo("2024-01-01T10:50:00");

        Integer httpStatusCode = documentContext.read("$.http_status_code");
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK.value());

        List<String> erros = documentContext.read("$.erros");
        assertThat(erros).isNull();






    }

}
