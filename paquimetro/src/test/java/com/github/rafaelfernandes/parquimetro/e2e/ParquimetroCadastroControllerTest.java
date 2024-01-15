package com.github.rafaelfernandes.parquimetro.e2e;

import com.github.rafaelfernandes.parquimetro.controller.request.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.response.MessageCliente;
import com.github.rafaelfernandes.parquimetro.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.util.GerarCadastro;
import com.github.rafaelfernandes.parquimetro.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.enums.Estados;
import com.github.rafaelfernandes.parquimetro.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.util.MongoContainers;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.datafaker.Faker;
import net.minidev.json.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ParquimetroCadastroControllerTest {

    private final Faker faker = new Faker();

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
    private ClienteEntity cadastrarNovoCliente() {
        Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);
        ClienteEntity clienteEntity = ClienteDto.from(cliente, Boolean.TRUE);

        return repository.save(clienteEntity);
    }

    @Test
    void deveRetornarDadosDeUmClienteQuandoExistirNaBase(){

        ClienteEntity clienteSalvo = cadastrarNovoCliente();

        String requestId = clienteSalvo.id().toString();

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/clientes/"+ requestId,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.clientes[0].id");
        assertEquals(clienteSalvo.id().toString(), id);

        String nome = documentContext.read("$.clientes[0].nome");
        assertEquals(clienteSalvo.nome(), nome);

        Long documento = documentContext.read("$.clientes[0].documento");
        assertEquals(clienteSalvo.documento(), documento);

        String logradouro = documentContext.read("$.clientes[0].endereco.logradouro");
        assertEquals(clienteSalvo.endereco().logradouro(), logradouro);

        Integer numero = documentContext.read("$.clientes[0].endereco.numero");
        assertEquals(clienteSalvo.endereco().numero(), numero);

        String complemento = documentContext.read("$.clientes[0].endereco.complemento");
        assertEquals(clienteSalvo.endereco().complemento(), complemento);

        String bairro = documentContext.read("$.clientes[0].endereco.bairro");
        assertEquals(clienteSalvo.endereco().bairro(), bairro);

        String cidade = documentContext.read("$.clientes[0].endereco.cidade");
        assertEquals(clienteSalvo.endereco().cidade(), cidade);

        Estados estado = Estados.valueOf(documentContext.read("$.clientes[0].endereco.estado"));
        assertEquals(clienteSalvo.endereco().estado(), estado);

        String formaPagamento = documentContext.read("$.clientes[0].forma_pagamento");
        assertEquals(clienteSalvo.forma_pagamento().toString(), formaPagamento);

        String email = documentContext.read("$.clientes[0].contato.email");
        assertEquals(clienteSalvo.contato().email(), email);

        String telefone = documentContext.read("$.clientes[0].contato.celular");
        assertEquals(clienteSalvo.contato().telefone(), telefone);

        List<String> carros = documentContext.read("$.clientes[0].carros");
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

        ResponseEntity<MessageCliente> createResponse = this.restTemplate
                .postForEntity(
                        "/clientes/",
                        cliente,
                        MessageCliente.class
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
            if (i > 10) continueLoop = Boolean.FALSE;

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

    @Test
    void deveAlterarDados(){

        ClienteEntity clienteSalvo = cadastrarNovoCliente();

        Cliente cliente = ClienteDto.from(clienteSalvo);

        String nome = faker.name().fullName();

        Cliente clienteUpdate = new Cliente(
                clienteSalvo.id(),
                nome,
                cliente.documento(),
                cliente.endereco(),
                cliente.forma_pagamento(),
                cliente.contato(),
                cliente.carros()
        );

        HttpEntity<Cliente> request = new HttpEntity<>(clienteUpdate);

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/clientes/" + clienteSalvo.id(),
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/clientes/" + clienteSalvo.id(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.clientes[0].id");
        String nomeSaved =  documentContext.read("$.clientes[0].nome");

        assertThat(id).isEqualTo(clienteSalvo.id().toString());
        assertThat(nomeSaved).isEqualTo(nome);

    }

    @Test
    void deveRetornarNotFoundAoAlterar(){

        String naoExisteId = faker.internet().uuid();

        Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);

        HttpEntity<Cliente> request = new HttpEntity<>(cliente);

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/clientes/" + naoExisteId,
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void deveDeletarCliente(){

        ClienteEntity clienteSalvo = cadastrarNovoCliente();

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange(
                        "/clientes/" + clienteSalvo.id(),
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Void> response = restTemplate
                .getForEntity(
                        "/clientes/" + clienteSalvo.id(),
                        Void.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);


    }

    @Test
    void deveRetornarNotFoundQuantoDeletarEClienteNaoExistir(){

        String naoExisteId = faker.internet().uuid();

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/clientes/" + naoExisteId,
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void deveAdicionarCarro(){

        ClienteEntity clienteSalvo = cadastrarNovoCliente();

        String placa = GerarCadastro.placa();

        List<String> carros = new ArrayList<>();

        carros.add(placa);

        HttpEntity<List<String>> request = new HttpEntity<>(carros);

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/clientes/" + clienteSalvo.id() + "/carros",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        List<String> todosCarros = new ArrayList<>();
        todosCarros.addAll(clienteSalvo.carros());
        todosCarros.addAll(carros);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/clientes/" + clienteSalvo.id(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.clientes[0].id");
        List<String> carrosSalvos =  documentContext.read("$.clientes[0].carros");

        assertThat(id).isEqualTo(clienteSalvo.id().toString());
        assertTrue(CollectionUtils.isEqualCollection(carrosSalvos, todosCarros));

    }

    @Test
    void deveRetornarBadRequestQuandoNaoEnviaCarros(){

        Cliente cliente = GerarCadastro.cliente(Boolean.FALSE);

        HttpEntity<List<String>> request = new HttpEntity<>(new ArrayList<>());

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/clientes/" + cliente.id() + "/carros",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExisteAoTentarAtualizar(){

        Cliente cliente = GerarCadastro.cliente(Boolean.FALSE);

        List<String> carros = GerarCadastro.placas();

        HttpEntity<List<String>> request = new HttpEntity<>(carros);

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/clientes/" + cliente.id() + "/carros",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void deveRetornarCarros(){

        ClienteEntity clienteSalvo = cadastrarNovoCliente();

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/clientes/" + clienteSalvo.id() + "/carros",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> carros =  documentContext.read("$.carros");

        assertTrue(CollectionUtils.isEqualCollection(carros, clienteSalvo.carros()));

    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExisteAoTentarObter(){

        Cliente cliente = GerarCadastro.cliente(Boolean.FALSE);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/clientes/" + cliente.id() + "/carros",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void deveDeletarCarro(){

        ClienteEntity clienteSalvo = cadastrarNovoCliente();

        String carro = clienteSalvo.carros().get(0);

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange(
                        "/clientes/" + clienteSalvo.id() + "/" + carro,
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/clientes/" + clienteSalvo.id() + "/carros" ,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> carros =  documentContext.read("$.carros");

        assertFalse(carros.contains(carro));

    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExisteAoTentarExluir(){

        ClienteEntity clienteSalvo = cadastrarNovoCliente();

        String carro = "AABBCCD";

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/clientes/" + clienteSalvo.id() + "/" + carro,
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }



    @Test
    void deveRetornarNotFoundQuandoCarroNaoExisteAoTentarExluir(){

        Cliente cliente = GerarCadastro.cliente(Boolean.FALSE);

        String carro = GerarCadastro.placa();

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/clientes/" + cliente.id() + "/" + carro,
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void deveAlterarFormaDePagamento(){

        ClienteEntity clienteEntity = cadastrarNovoCliente();

        FormaPagamento[] formaPagamentos = FormaPagamento.values();

        FormaPagamento novaFormaPagamento = null;

        for (FormaPagamento formaPagamento : formaPagamentos){

            if (formaPagamento.equals(clienteEntity.forma_pagamento()))
                continue;

            novaFormaPagamento = formaPagamento;
            break;
        }

        HttpEntity<String> request = new HttpEntity<>(novaFormaPagamento.name());

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/clientes/" + clienteEntity.id() + "/formaPagamento",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/clientes/" + clienteEntity.id(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String formaPagamentoAlterado = documentContext.read("$.clientes[0].forma_pagamento");
        assertThat(novaFormaPagamento.name()).isEqualTo(formaPagamentoAlterado);
    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExiste(){

        Cliente cliente = GerarCadastro.cliente(Boolean.FALSE);

        HttpEntity<String> request = new HttpEntity<>(FormaPagamento.PIX.name());

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/clientes/" + cliente.id() + "/formaPagamento",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }


    @Test
    void deveRetornarBadRequestQuandoClienteNaoExiste(){

        ClienteEntity cliente = cadastrarNovoCliente();
        HttpEntity<String> request = new HttpEntity<>("DOC_TED");

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/clientes/" + cliente.id() + "/formaPagamento",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    void deveObterFormaPagamento(){

        ClienteEntity clienteSalvo = cadastrarNovoCliente();

        FormaPagamento formaPagamento = clienteSalvo.forma_pagamento();

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/clientes/" + clienteSalvo.id() + "/formaPagamento",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String formaPagamentoCliente =  documentContext.read("$.forma_pagamento");

        assertThat(formaPagamentoCliente).isEqualTo(formaPagamento.name());

    }

    @Test
    void deveRetornarNotFoundQuandoTentarObterFormaPagamentoDEClienteNaoExistente(){
        Cliente cliente = GerarCadastro.cliente(Boolean.FALSE);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/clientes/" + cliente.id() + "/formaPagamento",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
