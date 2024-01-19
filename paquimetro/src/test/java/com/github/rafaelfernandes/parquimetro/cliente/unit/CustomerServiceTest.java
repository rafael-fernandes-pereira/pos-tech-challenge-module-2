package com.github.rafaelfernandes.parquimetro.cliente.unit;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Contact;
import com.github.rafaelfernandes.parquimetro.cliente.exception.CustomerDuplicateException;
import com.github.rafaelfernandes.parquimetro.util.GenerateData;
import com.github.rafaelfernandes.parquimetro.cliente.entity.CustomerEntity;
import com.github.rafaelfernandes.parquimetro.cliente.repository.CustomerRepository;
import com.github.rafaelfernandes.parquimetro.cliente.service.CustomerService;
import com.github.rafaelfernandes.parquimetro.util.MongoContainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
public class CustomerServiceTest {

    @Container //
    private static MongoDBContainer mongoDBContainer = MongoContainers.getDefaultContainer();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.auto-index-creation", MongoContainers::getTrue);
    }

    @Autowired private CustomerRepository repository;
    @Autowired private CustomerService service;

    @Test
    void deveRetornarClienteDuplicado(){

        this.repository.deleteAll();

        Customer customer = GenerateData.customer(Boolean.TRUE);

        CustomerEntity customerEntity = CustomerEntity.from(customer, Boolean.TRUE);

        this.repository.insert(customerEntity);

        assertThatThrownBy(() -> {
            this.service.create(customer);
        })
                .isInstanceOf(CustomerDuplicateException.class)
                .hasMessageContaining("Campo document e/ou campo email já existem!");

        String email = GenerateData.email();

        Long cpf = Long.valueOf(GenerateData.cpf());

        Customer customerEmail = new Customer(
                null,
                customer.name(),
                cpf,
                customer.address(),
                customer.payment_method(),
                customer.contact(),
                customer.cars()
        );

        assertThatThrownBy(() -> {
            this.service.create(customerEmail);
        })
                .isInstanceOf(CustomerDuplicateException.class)
                .hasMessageContaining("Campo document e/ou campo email já existem!");

        Customer customerDocumento = new Customer(
                null,
                customer.name(),
                customer.document(),
                customer.address(),
                customer.payment_method(),
                new Contact(
                        email,
                        GenerateData.celular()
                ),
                customer.cars()
        );

        assertThatThrownBy(() -> {
            this.service.create(customerDocumento);
        })
                .isInstanceOf(CustomerDuplicateException.class)
                .hasMessageContaining("Campo document e/ou campo email já existem!");
    }

}
