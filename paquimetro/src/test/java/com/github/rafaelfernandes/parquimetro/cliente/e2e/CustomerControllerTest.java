package com.github.rafaelfernandes.parquimetro.cliente.e2e;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Contact;
import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.util.GenerateData;
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
        CustomerEntity customerEntity = CustomerEntity.from(customer, Boolean.TRUE);

        return repository.save(customerEntity);
    }

    @Test
    @DisplayName("GET /customer/customerId -> Should Return Customer")
    void shouldReturnCustomer(){

        CustomerEntity customerSaved = createNewCustomer();

        String requestId = customerSaved.id().toString();

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/customers/"+ requestId,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.id");
        assertEquals(customerSaved.id().toString(), id);

        String nome = documentContext.read("$.name");
        assertEquals(customerSaved.name(), nome);

        Long documento = documentContext.read("$.document");
        assertEquals(customerSaved.document(), documento);

        String logradouro = documentContext.read("$.address.public_area");
        assertEquals(customerSaved.address().public_area(), logradouro);

        Integer numero = documentContext.read("$.address.number");
        assertEquals(customerSaved.address().number(), numero);

        String complemento = documentContext.read("$.address.additional_address_details");
        assertEquals(customerSaved.address().additional_address_details(), complemento);

        String bairro = documentContext.read("$.address.neighborhood");
        assertEquals(customerSaved.address().neighborhood(), bairro);

        String cidade = documentContext.read("$.address.city");
        assertEquals(customerSaved.address().city(), cidade);

        State estado = State.valueOf(documentContext.read("$.address.state"));
        assertEquals(customerSaved.address().state(), estado);

        String formaPagamento = documentContext.read("$.payment_method");
        assertEquals(customerSaved.paymentMethod().toString(), formaPagamento);

        String email = documentContext.read("$.contact.email");
        assertEquals(customerSaved.contact().email(), email);

        String telefone = documentContext.read("$.contact.cellphone");
        assertEquals(customerSaved.contact().celphone(), telefone);

        List<String> carros = documentContext.read("$.cars");
        assertEquals(customerSaved.cars(), carros);

    }

    @Test
    @DisplayName("GET /customers/customerId -> Should return not found when customer not exists")
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
    @DisplayName("GET /customers/customerId -> Should Return Bad Request When Dont Send UUID")
    void shouldReturnBadRequestWhenDontSendUUID(){
        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/customers/99",
                        String.class
                );

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

    }

    @Test
    @DisplayName("POST /customers/ -> Should Create New Customer")
    void shouldCreateNewCustomer(){

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
    @DisplayName("POST /customers/ -> Should return bad request when try register new customer sending data null")
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
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo paymentMethod deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo contact deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo cars deve ter pelo menos uma placa"))
        ;

    }

    @Test
    @DisplayName("POST /customers/ -> Should Return Duplicate Data When Create New Customer Has Exists")
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
    @DisplayName("GET /customers -> Should Return All Customers With Default Size")
    void shouldReturnAllCustomersWithDefaultSize(){

        List<CustomerEntity> customers = new ArrayList<>();

        for (int i =1; i <= 100; i++){
            Customer customer = GenerateData.customer(Boolean.TRUE);
            CustomerEntity customerEntity = CustomerEntity.from(customer, Boolean.TRUE);
            customers.add(customerEntity);
        }

        repository.saveAll(customers);

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
    @DisplayName("GET /customers -> Should Return Customers With Max Results")
    void shouldReturnCustomersWithMaxResults(){

        List<CustomerEntity> customers = new ArrayList<>();

        Boolean continueLoop = Boolean.TRUE;
        Integer i = 1;

        while (continueLoop){

            Customer customer = GenerateData.customer(Boolean.TRUE);
            CustomerEntity customerEntity = CustomerEntity.from(customer, Boolean.TRUE);

            Boolean isEmpty = customers.stream()
                    .filter(clienteEntity1 ->
                        clienteEntity1.document().equals(customerEntity.document()) ||
                        clienteEntity1.contact().email().equals(customerEntity.contact().email())
                    )
                    .toList()
                    .isEmpty();


            if (!isEmpty) continue;

            i++;
            if (i > 2000) continueLoop = Boolean.FALSE;

            customers.add(customerEntity);

        }

        repository.saveAll(customers);

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
    @DisplayName("GET /customers/ -> Should Return Nothing On Page One")
    void shouldReturnNothingOnPageOne(){

        List<CustomerEntity> customers = new ArrayList<>();

        Boolean continueLoop = Boolean.TRUE;
        Integer i = 1;

        while (continueLoop){

            Customer customer = GenerateData.customer(Boolean.TRUE);
            CustomerEntity customerEntity = CustomerEntity.from(customer, Boolean.TRUE);

            Boolean isEmpty = customers.stream()
                    .filter(clienteEntity1 ->
                            clienteEntity1.document().equals(customerEntity.document()) ||
                                    clienteEntity1.contact().email().equals(customerEntity.contact().email())
                    )
                    .toList()
                    .isEmpty();


            if (!isEmpty) continue;

            i++;
            if (i > 10) continueLoop = Boolean.FALSE;

            customers.add(customerEntity);


        }

        repository.saveAll(customers);

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
    @DisplayName("PUT /customers/customerId -> Should Update Customer")
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
    @DisplayName("PUT /customers/customerId -> Should Return Not Found When Customer Not Exists Updating Data")
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
    @DisplayName("PUT /customers/customerId -> Should Return Duplicate When Updating Customer Using Emails Other People")
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
    @DisplayName("PUT /customers/customerId -> Should Return Duplicate When Updating Customer Using Document From Other People")
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
    @DisplayName("DELETE /customers/customerId -> Should Delete Customer")
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
    @DisplayName("DELETE /customers/customerId Should Return Not Found When Trying Delete Customer Not Exists")
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
    @DisplayName("PUT /customer/customerId/cars -> Should Add New Car In Customer")
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
    @DisplayName("PUT /customers/customerId/car -> Should Return Bad Request When Add Car Empty")
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
                .anyMatch(erro -> erro.equalsIgnoreCase("Deve enviar pelo menos um car!"))
        ;

    }

    @Test
    @DisplayName("PUT /customers/customerId/cars -> Should Return Not Found When Add New Car On Customer Not Exists")
    void shouldReturnNotFoundWhenAddNewCarOnCustomerNotExists(){

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
    @DisplayName("GET /customers/customerId -> Should Return All Cars From Customer")
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
    @DisplayName("PUT /customers/customerId/cars -> Should Return Customer Not Found When Get Cars From Customer Not Exists")
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
                .anyMatch(erro -> erro.equalsIgnoreCase("Não é possível deletar o único car do customer. Adicione outro e, depois, delete este."))
        ;

    }

    @Test
    @DisplayName("PUT /customers/customerId/paymentMethod -> Should Change Payment Method")
    void shouldChangePaymentMethod(){

        CustomerEntity customerEntity = createNewCustomer();

        PaymentMethod[] paymentMethods = PaymentMethod.values();

        PaymentMethod paymentMethodNew = null;

        for (PaymentMethod paymentMethod : paymentMethods){

            if (paymentMethod.equals(customerEntity.paymentMethod()))
                continue;

            paymentMethodNew = paymentMethod;
            break;
        }

        HttpEntity<String> request = new HttpEntity<>(paymentMethodNew.name());

        ResponseEntity<Void> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + customerEntity.id() + "/paymentMethod",
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
        assertThat(paymentMethodNew.name()).isEqualTo(formaPagamentoAlterado);
    }

    @Test
    @DisplayName("PUT /customers/customerId/paymentMethod -> Should Return Not Found When Change Payment Method And Customer Not Exists")
    void shouldReturnNotFoundWhenChangePaymentMethodAndCustomerNotExists(){

        Customer customer = GenerateData.customer(Boolean.FALSE);

        HttpEntity<String> request = new HttpEntity<>(PaymentMethod.PIX.name());

        ResponseEntity<String> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + customer.id() + "/paymentMethod",
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
    @DisplayName("PUT /customers/customerId/paymentMethod -> Should Return Bad Request When Send Method Not Allowed")
    void shouldReturnBadRequestWhenSendMethodNotAllowed(){

        CustomerEntity customer = createNewCustomer();
        HttpEntity<String> request = new HttpEntity<>("DOC_TED");

        ResponseEntity<String> responseUpdate = restTemplate
                .exchange(
                        "/customers/" + customer.id() + "/paymentMethod",
                        HttpMethod.PUT,
                        request,
                        String.class
                );

        assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext documentContext = JsonPath.parse(responseUpdate.getBody());

        List<String> errors = documentContext.read("$.errors");

        assertThat(errors)
                .anyMatch(erro -> erro.equalsIgnoreCase("Metodo de pagamento não aceito!"))
        ;

    }

    @Test
    @DisplayName("GET /customers/customerId/paymentMethod -> Should Return Payment Method")
    void deveObterFormaPagamento(){

        CustomerEntity customerSaved = createNewCustomer();

        PaymentMethod paymentMethod = customerSaved.paymentMethod();

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + customerSaved.id() + "/paymentMethod",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String formaPagamentoCliente =  documentContext.read("$");

        assertThat(formaPagamentoCliente).isEqualTo(paymentMethod.name());

    }

    @Test
    @DisplayName("GET /customers/customerId/paymentMethod -> Should Return Not Found When Return Payment Method of Customer Not Exists")
    void shouldReturnNotFoundWhenReturnPaymentMethodOfCustomerNotExists(){
        Customer customer = GenerateData.customer(Boolean.FALSE);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        "/customers/" + customer.id() + "/paymentMethod",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<String> errors = documentContext.read("$.errors");

        assertThat(errors)
                .anyMatch(erro -> erro.equalsIgnoreCase("Cliente não existe!"))
        ;

    }

}
