package com.github.rafaelfernandes.parquimetro.cliente.e2e;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Contact;
import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.util.GenerateData;
import com.github.rafaelfernandes.parquimetro.cliente.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.cliente.entity.CustomerEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.State;
import com.github.rafaelfernandes.parquimetro.cliente.repository.CustomerRepository;
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
import java.util.Set;
import java.util.TreeSet;

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
    private CustomerRepository repository;

    @BeforeEach
    void setup(){
        repository.deleteAll();
    }

    @NotNull
    private CustomerEntity createNewCustomer() {
        Customer customer = GenerateData.customer(Boolean.TRUE);
        CustomerEntity customerEntity = ClienteDto.from(customer, Boolean.TRUE);

        return repository.save(customerEntity);
    }

    @Test
    void deveRetornarDadosDeUmClienteQuandoExistirNaBase(){

        CustomerEntity clienteSalvo = createNewCustomer();

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

        Customer customer = GenerateData.customer(Boolean.TRUE);

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

        Customer customer = GenerateData.customer(Boolean.TRUE);

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
            Customer customer = GenerateData.customer(Boolean.TRUE);
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

        JSONArray page = documentContext.read("$.content[*]");

        assertThat(page.size()).isEqualTo(50);

    }

    @Test
    void deveRetornarComQuantidadeMaxima(){

        List<CustomerEntity> clienteEntities = new ArrayList<>();

        Boolean continueLoop = Boolean.TRUE;
        Integer i = 1;

        while (continueLoop){

            Customer customer = GenerateData.customer(Boolean.TRUE);
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

        JSONArray page = documentContext.read("$.content[*]");

        assertThat(page.size()).isEqualTo(1000);

    }

    @Test
    void deveRetornarNadaNaPagina1(){

        List<CustomerEntity> clienteEntities = new ArrayList<>();

        Boolean continueLoop = Boolean.TRUE;
        Integer i = 1;

        while (continueLoop){

            Customer customer = GenerateData.customer(Boolean.TRUE);
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

        JSONArray page = documentContext.read("$.content[*]");

        assertThat(page.size()).isEqualTo(0);

    }

    @Test
    void shouldUpdateCustomer(){

        CustomerEntity customerSaved = createNewCustomer();

        Customer customer = Customer.from(customerSaved);

        String name = faker.name().fullName();

        Customer customerUpdate = new Customer(
                customerSaved.id(),
                name,
                customer.document(),
                customer.address(),
                customer.payment_method(),
                customer.contact(),
                customer.cars()
        );

        HttpEntity<Customer> request = new HttpEntity<>(customerUpdate);

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + customerSaved.id(),
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + customerSaved.id(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.id");
        String nomeSaved =  documentContext.read("$.name");

        assertThat(id).isEqualTo(customerSaved.id().toString());
        assertThat(nomeSaved).isEqualTo(name);

    }

    @Test
    @DisplayName("Should Return Not Found When Customer Not Exists Updating Data")
    void shouldReturnNotFoundWhenCustomerNotExistsUpdatingData(){

        String notExistsId = faker.internet().uuid();

        Customer customer = GenerateData.customer(Boolean.TRUE);

        HttpEntity<Customer> request = new HttpEntity<>(customer);

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + notExistsId,
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    @DisplayName("Should Return Duplicate When Updating Customer Using Emails Other People")
    void shouldReturnDuplicateWhenUpdatingEmailOtherPeople(){

        CustomerEntity customerOriginalEmail = createNewCustomer();

        String emailOtherPeople = customerOriginalEmail.contact().email();

        CustomerEntity customerSaved = createNewCustomer();

        Customer customer = Customer.from(customerSaved);

        Customer customerUpdate = new Customer(
                customerSaved.id(),
                customer.name(),
                customer.document(),
                customer.address(),
                customer.payment_method(),
                new Contact(
                        emailOtherPeople,
                        customer.contact().cellphone()
                ),
                customer.cars()
        );

        HttpEntity<Customer> request = new HttpEntity<>(customerUpdate);

        ResponseEntity<String> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + customerUpdate.id(),
                        HttpMethod.PUT,
                        request,
                        String.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        DocumentContext documentContext = JsonPath.parse(responseUpdate.getBody());

        List<String> errors = documentContext.read("$.errors");

        assertThat(errors)
                .anyMatch(erro -> erro.equalsIgnoreCase("Campo document e/ou campo email já existem!"))
        ;

    }

    @Test
    @DisplayName("Should Return Duplicate When Updating Customer Using Document From Other People")
    void shouldReturnDuplicateWhenUpdatingDocumentOtherPeople(){

        CustomerEntity customerOriginalEmail = createNewCustomer();

        Long documentOtherPeople = customerOriginalEmail.document();

        CustomerEntity customerSaved = createNewCustomer();

        Customer customer = Customer.from(customerSaved);

        Customer customerUpdate = new Customer(
                customerSaved.id(),
                customer.name(),
                documentOtherPeople,
                customer.address(),
                customer.payment_method(),
                customer.contact(),
                customer.cars()
        );

        HttpEntity<Customer> request = new HttpEntity<>(customerUpdate);

        ResponseEntity<String> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + customerUpdate.id(),
                        HttpMethod.PUT,
                        request,
                        String.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        DocumentContext documentContext = JsonPath.parse(responseUpdate.getBody());

        List<String> errors = documentContext.read("$.errors");

        assertThat(errors)
                .anyMatch(erro -> erro.equalsIgnoreCase("Campo document e/ou campo email já existem!"))
        ;

    }

    @Test
    @DisplayName("Should Delete Customer")
    void shouldDeleteCustomer(){

        CustomerEntity customer = createNewCustomer();

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange(
                        "/customers/" + customer.id(),
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Void> response = restTemplate
                .getForEntity(
                        "/customers/" + customer.id(),
                        Void.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);


    }

    @Test
    @DisplayName("Should Return Not Found When Trying Delete Customer Not Exists")
    void shouldReturnNotFoundWhenTryingDeleteCustomerNotExists() {

        String customerNotExistsId = faker.internet().uuid();

        ResponseEntity<String> response = restTemplate
                .exchange(
                        "/customers/" + customerNotExistsId,
                        HttpMethod.DELETE,
                        null,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> errors = documentContext.read("$.errors");

        assertThat(errors)
                .anyMatch(erro -> erro.equalsIgnoreCase("Cliente não existe!"))
        ;

    }

    @Test
    @DisplayName("Should Add New Car In Customer")
    void shouldAddNewCarInCustomer(){

        CustomerEntity newCustomer = createNewCustomer();

        String car = GenerateData.placa();

        List<String> cars = new ArrayList<>();

        cars.add(car);

        HttpEntity<List<String>> request = new HttpEntity<>(cars);

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + newCustomer.id() + "/cars",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        List<String> allCars = new ArrayList<>();
        allCars.addAll(newCustomer.cars());
        allCars.addAll(cars);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + newCustomer.id(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.id");
        List<String> savedCars =  documentContext.read("$.cars");

        assertThat(id).isEqualTo(newCustomer.id().toString());
        assertTrue(CollectionUtils.isEqualCollection(savedCars, allCars));

    }

    @Test
    @DisplayName("Should Return Bad Request When Add Car Empty")
    void shouldReturnBadRequestWhenAddCarEmpty(){

        Customer customer = GenerateData.customer(Boolean.FALSE);

        HttpEntity<List<String>> request = new HttpEntity<>(new ArrayList<>());

        ResponseEntity<String> response = restTemplate
                .exchange(
                        "/customers/" + customer.id() + "/cars",
                        HttpMethod.PUT,
                        request,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> errors = documentContext.read("$.errors");

        assertThat(errors)
                .anyMatch(erro -> erro.equalsIgnoreCase("Deve enviar pelo menos um carro!"))
        ;

    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExisteAoTentarAtualizar(){

        Customer customer = GenerateData.customer(Boolean.FALSE);

        List<String> cars = GenerateData.placas();

        HttpEntity<List<String>> request = new HttpEntity<>(cars);

        ResponseEntity<String> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + customer.id() + "/cars",
                        HttpMethod.PUT,
                        request,
                        String.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(responseUpdate.getBody());

        List<String> errors = documentContext.read("$.errors");

        assertThat(errors)
                .anyMatch(erro -> erro.equalsIgnoreCase("Cliente não existe!"))
        ;

    }

    @Test
    @DisplayName("Should Return All Cars From Customer")
    void shouldReturnAllCarsFromCustomer(){

        CustomerEntity customerSaved = createNewCustomer();

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + customerSaved.id() + "/cars",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> carros =  documentContext.read("$.*");

        assertTrue(CollectionUtils.isEqualCollection(carros, customerSaved.cars()));

    }

    @Test
    @DisplayName("Should Return Customer Not Found When Get Cars From Customer Not Exists")
    void shouldReturnCustomerNotFoundWhenGetCarsFromCustomerNotExists(){

        Customer customer = GenerateData.customer(Boolean.FALSE);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + customer.id() + "/cars",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> errors = documentContext.read("$.errors");

        assertThat(errors)
                .anyMatch(erro -> erro.equalsIgnoreCase("Cliente não existe!"))
        ;

    }

    @Test
    @DisplayName("DELETE /customers/customerId/car -> OK")
    void shouldDeleteCar(){

        CustomerEntity customerSaved = createNewCustomer();

        String carro = customerSaved.cars().get(0);

        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange(
                        "/customers/" + customerSaved.id() + "/" + carro,
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + customerSaved.id() + "/cars" ,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> carros =  documentContext.read("$.*");

        assertFalse(carros.contains(carro));

    }

    @Test
    @DisplayName("DELETE /customers/customerId/car -> Should Return Not Found When Customer Not Exists")
    void shouldReturnNotFoundWhenDeleteCarOfCustomerNotExists(){

        Customer customer = GenerateData.customer(Boolean.FALSE);

        String carro = "AABBCCD";

        ResponseEntity<String> response = restTemplate
                .exchange(
                        "/customers/" + customer.id() + "/" + carro,
                        HttpMethod.DELETE,
                        null,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> errors = documentContext.read("$.errors");

        assertThat(errors)
                .anyMatch(erro -> erro.equalsIgnoreCase("Cliente não existe!"))
        ;

    }



    @Test
    @DisplayName("DELETE /customers/customerId/car -> Should Return Not Found When Car Not Exists In Customer")
    void shouldReturnNotFoundWhenCarNotExistsInCustomer(){

        CustomerEntity customerSaved = createNewCustomer();

        String carro = GenerateData.placa();

        ResponseEntity<String> response = restTemplate
                .exchange(
                        "/customers/" + customerSaved.id() + "/" + carro,
                        HttpMethod.DELETE,
                        null,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> errors = documentContext.read("$.errors");

        assertThat(errors)
                .anyMatch(erro -> erro.equalsIgnoreCase("Carro não existe para esse cliente"))
        ;

    }

    @Test
    @DisplayName("DELETE /customers/customerId/car -> Should Return Bad Request When Delete On Last Car")
    void shouldBadRequestWhenDeleteOnLastCar(){

        CustomerEntity customerSaved = createNewCustomer();

        String car = customerSaved.cars().get(0);

        Set<String> newCars = new TreeSet<>();
        newCars.add(car);
        CustomerEntity customerToSave = CustomerEntity.updateCars(customerSaved, newCars);

        this.repository.save(customerToSave);

        ResponseEntity<String> response = restTemplate
                .exchange(
                        "/customers/" + customerSaved.id() + "/" + car,
                        HttpMethod.DELETE,
                        null,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> errors = documentContext.read("$.errors");

        assertThat(errors)
                .anyMatch(erro -> erro.equalsIgnoreCase("Não é possível deletar o único carro do customer. Adicione outro e, depois, delete este."))
        ;

    }

    @Test
    void deveAlterarFormaDePagamento(){

        CustomerEntity customerEntity = createNewCustomer();

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

        Customer customer = GenerateData.customer(Boolean.FALSE);

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

        CustomerEntity cliente = createNewCustomer();
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

        CustomerEntity clienteSalvo = createNewCustomer();

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
        Customer customer = GenerateData.customer(Boolean.FALSE);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + customer.id() + "/formaPagamento",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
