package com.github.rafaelfernandes.parquimetro.cliente.e2e;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.util.GerarCadastro;
import com.github.rafaelfernandes.parquimetro.cliente.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.cliente.entity.CustomerEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.State;
import com.github.rafaelfernandes.parquimetro.cliente.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.util.MongoContainers;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.datafaker.Faker;
import net.minidev.json.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
public class CustomerControllerTest {

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
    private CustomerEntity cadastrarNovoCliente() {
        Customer customer = GerarCadastro.cliente(Boolean.TRUE);
        CustomerEntity customerEntity = ClienteDto.from(customer, Boolean.TRUE);

        return repository.save(customerEntity);
    }

    @Test
    void deveRetornarDadosDeUmClienteQuandoExistirNaBase(){

        CustomerEntity clienteSalvo = cadastrarNovoCliente();

        String requestId = clienteSalvo.id().toString();

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/customers/"+ requestId,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.id");
        assertEquals(clienteSalvo.id().toString(), id);

        String nome = documentContext.read("$.name");
        assertEquals(clienteSalvo.name(), nome);

        Long documento = documentContext.read("$.document");
        assertEquals(clienteSalvo.document(), documento);

        String logradouro = documentContext.read("$.address.public_area");
        assertEquals(clienteSalvo.address().public_area(), logradouro);

        Integer numero = documentContext.read("$.address.number");
        assertEquals(clienteSalvo.address().number(), numero);

        String complemento = documentContext.read("$.address.additional_address_details");
        assertEquals(clienteSalvo.address().additional_address_details(), complemento);

        String bairro = documentContext.read("$.address.neighborhood");
        assertEquals(clienteSalvo.address().neighborhood(), bairro);

        String cidade = documentContext.read("$.address.city");
        assertEquals(clienteSalvo.address().city(), cidade);

        State estado = State.valueOf(documentContext.read("$.address.state"));
        assertEquals(clienteSalvo.address().state(), estado);

        String formaPagamento = documentContext.read("$.payment_method");
        assertEquals(clienteSalvo.payment_method().toString(), formaPagamento);

        String email = documentContext.read("$.contact.email");
        assertEquals(clienteSalvo.contact().email(), email);

        String telefone = documentContext.read("$.contact.cellphone");
        assertEquals(clienteSalvo.contact().celphone(), telefone);

        List<String> carros = documentContext.read("$.cars");
        assertEquals(clienteSalvo.cars(), carros);

    }

    @Test
    @DisplayName("Should return not found when customer not exists")
    void shouldReturnNotFoundWhenCustomerNotExists(){
        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/customers/be16f7b8-8da4-4930-b2e2-bf912dcfc8a8",
                        String.class
                );

        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> erros = documentContext.read("$.errors");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Cliente não existe!"));
        ;


    }

    @Test
    void deveRetornarBadRequestQuandoNaoPassarUUID(){
        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/customers/99",
                        String.class
                );

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

    }

    @Test
    void deveCadastrarUmNovoCliente(){

        Customer customer = GerarCadastro.cliente(Boolean.TRUE);

        ResponseEntity<Void> createResponse = this.restTemplate
                .postForEntity(
                        "/customers/",
                        customer,
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
    @DisplayName("Should return bad request when try register new customer sending data null")
    void shouldReturnBadRequestWhenTryRegisterNewCustomerSendingDataNull(){

        Customer customer = new Customer(null, null, null, null, null, null, null);

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/customers/",
                        customer,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext documentContext = JsonPath.parse(createResponse.getBody());

        List<String> errors = documentContext.read("$.errors");

        assertThat(errors)
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo name deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo document deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo payment_method deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo contact deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo cars deve ter pelo menos uma placa"))
        ;

    }

    @Test
    @DisplayName("Should Return Duplicate Data When Create New Customer Has Exists")
    void shouldReturnDuplicateDataWhenCreateNewCustomerHasExists(){

        Customer customer = GerarCadastro.cliente(Boolean.TRUE);

        ResponseEntity<Void> createResponse = this.restTemplate
                .postForEntity(
                        "/customers/",
                        customer,
                        Void.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> createResponseDuplicate = this.restTemplate
                .postForEntity(
                        "/customers/",
                        customer,
                        String.class
                );

        assertThat(createResponseDuplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        DocumentContext documentContext = JsonPath.parse(createResponseDuplicate.getBody());

        List<String> errors = documentContext.read("$.errors");

        assertThat(errors)
                .anyMatch(erro -> erro.equalsIgnoreCase("Campo document e/ou campo email já existem!"))
        ;


    }

    @Test
    void deveRetornarComQuantidadePadrao(){

        List<CustomerEntity> clienteEntities = new ArrayList<>();

        for (int i =1; i <= 100; i++){
            Customer customer = GerarCadastro.cliente(Boolean.TRUE);
            CustomerEntity customerEntity = ClienteDto.from(customer, Boolean.TRUE);
            clienteEntities.add(customerEntity);
        }

        repository.saveAll(clienteEntities);

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/customers/",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        JSONArray page = documentContext.read("$.[*]");

        assertThat(page.size()).isEqualTo(50);

    }

    @Test
    void deveRetornarComQuantidadeMaxima(){

        List<CustomerEntity> clienteEntities = new ArrayList<>();

        Boolean continueLoop = Boolean.TRUE;
        Integer i = 1;

        while (continueLoop){

            Customer customer = GerarCadastro.cliente(Boolean.TRUE);
            CustomerEntity customerEntity = ClienteDto.from(customer, Boolean.TRUE);

            Boolean isEmpty = clienteEntities.stream()
                    .filter(clienteEntity1 ->
                        clienteEntity1.document().equals(customerEntity.document()) ||
                        clienteEntity1.contact().email().equals(customerEntity.contact().email())
                    )
                    .toList()
                    .isEmpty();


            if (!isEmpty) continue;

            i++;
            if (i > 2000) continueLoop = Boolean.FALSE;

            clienteEntities.add(customerEntity);


        }

        repository.saveAll(clienteEntities);

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/customers/?page=0&size=2000",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        JSONArray page = documentContext.read("$.[*]");

        assertThat(page.size()).isEqualTo(1000);

    }

    @Test
    void deveRetornarNadaNaPagina1(){

        List<CustomerEntity> clienteEntities = new ArrayList<>();

        Boolean continueLoop = Boolean.TRUE;
        Integer i = 1;

        while (continueLoop){

            Customer customer = GerarCadastro.cliente(Boolean.TRUE);
            CustomerEntity customerEntity = ClienteDto.from(customer, Boolean.TRUE);

            Boolean isEmpty = clienteEntities.stream()
                    .filter(clienteEntity1 ->
                            clienteEntity1.document().equals(customerEntity.document()) ||
                                    clienteEntity1.contact().email().equals(customerEntity.contact().email())
                    )
                    .toList()
                    .isEmpty();


            if (!isEmpty) continue;

            i++;
            if (i > 10) continueLoop = Boolean.FALSE;

            clienteEntities.add(customerEntity);


        }

        repository.saveAll(clienteEntities);

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/customers/?page=1",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        JSONArray page = documentContext.read("$.[*]");

        assertThat(page.size()).isEqualTo(0);

    }

    @Test
    void deveAlterarDados(){

        CustomerEntity clienteSalvo = cadastrarNovoCliente();

        Customer customer = Customer.from(clienteSalvo);

        String nome = faker.name().fullName();

        Customer customerUpdate = new Customer(
                clienteSalvo.id(),
                nome,
                customer.document(),
                customer.address(),
                customer.payment_method(),
                customer.contact(),
                customer.cars()
        );

        HttpEntity<Customer> request = new HttpEntity<>(customerUpdate);

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + clienteSalvo.id(),
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + clienteSalvo.id(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.id");
        String nomeSaved =  documentContext.read("$.name");

        assertThat(id).isEqualTo(clienteSalvo.id().toString());
        assertThat(nomeSaved).isEqualTo(nome);

    }

    @Test
    void deveRetornarNotFoundAoAlterar(){

        String naoExisteId = faker.internet().uuid();

        Customer customer = GerarCadastro.cliente(Boolean.TRUE);

        HttpEntity<Customer> request = new HttpEntity<>(customer);

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + naoExisteId,
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void deveDeletarCliente(){

        CustomerEntity clienteSalvo = cadastrarNovoCliente();

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange(
                        "/customers/" + clienteSalvo.id(),
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Void> response = restTemplate
                .getForEntity(
                        "/customers/" + clienteSalvo.id(),
                        Void.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);


    }

    @Test
    void deveRetornarNotFoundQuantoDeletarEClienteNaoExistir(){

        String naoExisteId = faker.internet().uuid();

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + naoExisteId,
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void deveAdicionarCarro(){

        CustomerEntity clienteSalvo = cadastrarNovoCliente();

        String placa = GerarCadastro.placa();

        List<String> carros = new ArrayList<>();

        carros.add(placa);

        HttpEntity<List<String>> request = new HttpEntity<>(carros);

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + clienteSalvo.id() + "/cars",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        List<String> todosCarros = new ArrayList<>();
        todosCarros.addAll(clienteSalvo.cars());
        todosCarros.addAll(carros);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + clienteSalvo.id(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.id");
        List<String> carrosSalvos =  documentContext.read("$.cars");

        assertThat(id).isEqualTo(clienteSalvo.id().toString());
        assertTrue(CollectionUtils.isEqualCollection(carrosSalvos, todosCarros));

    }

    @Test
    void deveRetornarBadRequestQuandoNaoEnviaCarros(){

        Customer customer = GerarCadastro.cliente(Boolean.FALSE);

        HttpEntity<List<String>> request = new HttpEntity<>(new ArrayList<>());

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + customer.id() + "/cars",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExisteAoTentarAtualizar(){

        Customer customer = GerarCadastro.cliente(Boolean.FALSE);

        List<String> carros = GerarCadastro.placas();

        HttpEntity<List<String>> request = new HttpEntity<>(carros);

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + customer.id() + "/cars",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void deveRetornarCarros(){

        CustomerEntity clienteSalvo = cadastrarNovoCliente();

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + clienteSalvo.id() + "/cars",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> carros =  documentContext.read("$.cars");

        assertTrue(CollectionUtils.isEqualCollection(carros, clienteSalvo.cars()));

    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExisteAoTentarObter(){

        Customer customer = GerarCadastro.cliente(Boolean.FALSE);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + customer.id() + "/cars",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void deveDeletarCarro(){

        CustomerEntity clienteSalvo = cadastrarNovoCliente();

        String carro = clienteSalvo.cars().get(0);

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange(
                        "/customers/" + clienteSalvo.id() + "/" + carro,
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + clienteSalvo.id() + "/cars" ,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> carros =  documentContext.read("$.cars");

        assertFalse(carros.contains(carro));

    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExisteAoTentarExluir(){

        CustomerEntity clienteSalvo = cadastrarNovoCliente();

        String carro = "AABBCCD";

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + clienteSalvo.id() + "/" + carro,
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }



    @Test
    void deveRetornarNotFoundQuandoCarroNaoExisteAoTentarExluir(){

        Customer customer = GerarCadastro.cliente(Boolean.FALSE);

        String carro = GerarCadastro.placa();

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + customer.id() + "/" + carro,
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void deveAlterarFormaDePagamento(){

        CustomerEntity customerEntity = cadastrarNovoCliente();

        PaymentMethod[] paymentMethods = PaymentMethod.values();

        PaymentMethod novaPaymentMethod = null;

        for (PaymentMethod paymentMethod : paymentMethods){

            if (paymentMethod.equals(customerEntity.payment_method()))
                continue;

            novaPaymentMethod = paymentMethod;
            break;
        }

        HttpEntity<String> request = new HttpEntity<>(novaPaymentMethod.name());

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + customerEntity.id() + "/formaPagamento",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + customerEntity.id(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String formaPagamentoAlterado = documentContext.read("$.payment_method");
        assertThat(novaPaymentMethod.name()).isEqualTo(formaPagamentoAlterado);
    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExiste(){

        Customer customer = GerarCadastro.cliente(Boolean.FALSE);

        HttpEntity<String> request = new HttpEntity<>(PaymentMethod.PIX.name());

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + customer.id() + "/formaPagamento",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }


    @Test
    void deveRetornarBadRequestQuandoClienteNaoExiste(){

        CustomerEntity cliente = cadastrarNovoCliente();
        HttpEntity<String> request = new HttpEntity<>("DOC_TED");

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + cliente.id() + "/formaPagamento",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    void deveObterFormaPagamento(){

        CustomerEntity clienteSalvo = cadastrarNovoCliente();

        PaymentMethod paymentMethod = clienteSalvo.payment_method();

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + clienteSalvo.id() + "/formaPagamento",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String formaPagamentoCliente =  documentContext.read("$.payment_method");

        assertThat(formaPagamentoCliente).isEqualTo(paymentMethod.name());

    }

    @Test
    void deveRetornarNotFoundQuandoTentarObterFormaPagamentoDEClienteNaoExistente(){
        Customer customer = GerarCadastro.cliente(Boolean.FALSE);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + customer.id() + "/formaPagamento",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
