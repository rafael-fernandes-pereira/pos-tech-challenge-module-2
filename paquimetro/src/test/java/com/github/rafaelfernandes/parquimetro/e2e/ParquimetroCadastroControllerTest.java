package com.github.rafaelfernandes.parquimetro.e2e;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.response.Message;
import com.github.rafaelfernandes.parquimetro.dados.GerarCadastro;
import com.github.rafaelfernandes.parquimetro.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.enums.Estados;
import com.github.rafaelfernandes.parquimetro.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.util.MongoContainers;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ParquimetroCadastroControllerTest {

    @Container //
    private static MongoDBContainer mongoDBContainer = MongoContainers.getDefaultContainer();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClienteRepository repository;

    @Test
    @DirtiesContext
    void deveRetornarDadosDeUmClienteQuandoExistirNaBase(){

        Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);
        ClienteEntity clienteEntity = ClienteDto.from(cliente, Boolean.TRUE);

        ClienteEntity clienteSalvo = repository.save(clienteEntity);

        String requestId = clienteSalvo.id().toString();

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/clientes/"+ requestId,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.cliente.id");
        assertEquals(clienteSalvo.id().toString(), id);

        String nome = documentContext.read("$.cliente.nome");
        assertEquals(clienteSalvo.nome(), nome);

        Long documento = documentContext.read("$.cliente.documento");
        assertEquals(clienteSalvo.documento(), documento);

        String logradouro = documentContext.read("$.cliente.endereco.logradouro");
        assertEquals(clienteSalvo.endereco().logradouro(), logradouro);

        Integer numero = documentContext.read("$.cliente.endereco.numero");
        assertEquals(clienteSalvo.endereco().numero(), numero);

        String complemento = documentContext.read("$.cliente.endereco.complemento");
        assertEquals(clienteSalvo.endereco().complemento(), complemento);

        String bairro = documentContext.read("$.cliente.endereco.bairro");
        assertEquals(clienteSalvo.endereco().bairro(), bairro);

        String cidade = documentContext.read("$.cliente.endereco.cidade");
        assertEquals(clienteSalvo.endereco().cidade(), cidade);

        Estados estado = Estados.valueOf(documentContext.read("$.cliente.endereco.estado"));
        assertEquals(clienteSalvo.endereco().estado(), estado);

        String formaPagamento = documentContext.read("$.cliente.forma_pagamento");
        assertEquals(clienteSalvo.forma_pagamento().toString(), formaPagamento);

        String email = documentContext.read("$.cliente.contato.email");
        assertEquals(clienteSalvo.contato().email(), email);

        String telefone = documentContext.read("$.cliente.contato.celular");
        assertEquals(clienteSalvo.contato().telefone(), telefone);

        List<String> carros = documentContext.read("$.cliente.carros");
        assertEquals(clienteSalvo.carros(), carros);

    }

    @Test
    void deveRetornarNotFoundQuandoNaoExistirNaBase(){
        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/clientes/be16f7b8-8da4-4930-b2e2-bf912dcfc8a8",
                        String.class
                );

        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();

    }

    @Test
    void deveRetornarBadRequestQuandoNaoPassarUUID(){
        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/clientes/99",
                        String.class
                );

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

    }

    @Test
    @DirtiesContext
    void deveCadastrarUmNovoCliente(){

        Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);

        ResponseEntity<Void> createResponse = this.restTemplate
                .postForEntity(
                        "/clientes/",
                        cliente,
                        Void.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI location = createResponse.getHeaders().getLocation();

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        location,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deveRetornarBadRequestAoCadastrarUmNovoCliente(){

        Cliente cliente = new Cliente(null, null, null, null, null, null, null);

        ResponseEntity<Message> createResponse = this.restTemplate
                .postForEntity(
                        "/clientes/",
                        cliente,
                        Message.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }


}
