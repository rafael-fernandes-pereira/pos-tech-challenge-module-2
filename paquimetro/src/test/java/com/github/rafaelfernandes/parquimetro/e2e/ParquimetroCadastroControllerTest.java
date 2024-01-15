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
import net.minidev.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DirtiesContext
public class ParquimetroCadastroControllerTest {

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

    @Test
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

    @Test
    void deveRetornarDuplicateRecordAoCadastrarUmNovoCliente(){

        Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);

        ResponseEntity<Void> createResponse = this.restTemplate
                .postForEntity(
                        "/clientes/",
                        cliente,
                        Void.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> createResponseDuplicate = this.restTemplate
                .postForEntity(
                        "/clientes/",
                        cliente,
                        Void.class
                );

        assertThat(createResponseDuplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void deveRetornarComQuantidadePadrao(){

        List<ClienteEntity> clienteEntities = new ArrayList<>();

        for (int i =1; i <= 100; i++){
            Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);
            ClienteEntity clienteEntity = ClienteDto.from(cliente, Boolean.TRUE);
            clienteEntities.add(clienteEntity);
        }

        repository.saveAll(clienteEntities);

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/clientes/",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        JSONArray page = documentContext.read("$.[*]");

        assertThat(page.size()).isEqualTo(50);

    }

    @Test
    void deveRetornarComQuantidadeMaxima(){

        List<ClienteEntity> clienteEntities = new ArrayList<>();

        Boolean continueLoop = Boolean.TRUE;
        Integer i = 1;

        while (continueLoop){

            Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);
            ClienteEntity clienteEntity = ClienteDto.from(cliente, Boolean.TRUE);

            Boolean isEmpty = clienteEntities.stream()
                    .filter(clienteEntity1 ->
                        clienteEntity1.documento().equals(clienteEntity.documento()) ||
                        clienteEntity1.contato().email().equals(clienteEntity.contato().email())
                    )
                    .toList()
                    .isEmpty();


            if (!isEmpty) continue;

            i++;
            if (i > 2000) continueLoop = Boolean.FALSE;

            clienteEntities.add(clienteEntity);


        }

        repository.saveAll(clienteEntities);

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/clientes/?page=0&size=2000",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        JSONArray page = documentContext.read("$.[*]");

        assertThat(page.size()).isEqualTo(1000);

    }

    @Test
    void deveRetornarNadaNaPagina1(){

        List<ClienteEntity> clienteEntities = new ArrayList<>();

        Boolean continueLoop = Boolean.TRUE;
        Integer i = 1;

        while (continueLoop){

            Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);
            ClienteEntity clienteEntity = ClienteDto.from(cliente, Boolean.TRUE);

            Boolean isEmpty = clienteEntities.stream()
                    .filter(clienteEntity1 ->
                            clienteEntity1.documento().equals(clienteEntity.documento()) ||
                                    clienteEntity1.contato().email().equals(clienteEntity.contato().email())
                    )
                    .toList()
                    .isEmpty();


            if (!isEmpty) continue;

            i++;
            if (i > 50) continueLoop = Boolean.FALSE;

            clienteEntities.add(clienteEntity);


        }

        repository.saveAll(clienteEntities);

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/clientes/?page=1",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        JSONArray page = documentContext.read("$.[*]");

        assertThat(page.size()).isEqualTo(0);

    }




}
