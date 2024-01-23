package com.github.rafaelfernandes.parquimetro.customer.unit;

import com.github.rafaelfernandes.parquimetro.customer.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.customer.controller.request.Contact;
import com.github.rafaelfernandes.parquimetro.customer.controller.request.Address;
import com.github.rafaelfernandes.parquimetro.util.GenerateData;
import com.github.rafaelfernandes.parquimetro.util.MongoContainers;
import com.github.rafaelfernandes.parquimetro.customer.validation.ValidacaoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
public class ValidationTest {

    @Autowired
    private ValidacaoRequest validacaoRequest;


    @Container //
    private static MongoDBContainer mongoDBContainer = MongoContainers.getDefaultContainer();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.auto-index-creation", MongoContainers::getTrue);
    }

    @Test
    void deveRetornarErros(){
        Customer customer = new Customer(null, null, null,null,null,null, null);

        List<String> erros = this.validacaoRequest.cliente(customer);

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo name deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo document deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo paymentMethod deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo contact deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo cars deve ter pelo menos uma placa"))
        ;

        customer = new Customer(null, "Teste", null,null,null,null, null);

        erros = this.validacaoRequest.cliente(customer);

        assertThat(erros)
                .anyMatch(erro -> erro.equals("O campo name deve ter no mínimo de 20 e no máximo de 100 caracteres"))
        ;

        customer = new Customer(null, "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste", null,null,null,null, new ArrayList<String>());

        erros = this.validacaoRequest.cliente(customer);

        assertThat(erros)
                .anyMatch(erro -> erro.equals("O campo name deve ter no mínimo de 20 e no máximo de 100 caracteres"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo cars deve ter pelo menos uma placa"))
        ;

        Address address = new Address(null, null, null, null, null, null);
        customer = new Customer(null, null, null, address,null,null, null);

        erros = this.validacaoRequest.cliente(customer);


        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address.public_area deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address.number deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address.neighborhood deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address.city deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address.state deve estar preenchido"))

        ;


        address = new Address(
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                -1,
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                null
        );

        customer = new Customer(null, null, null, address,null,null, null);

        erros = this.validacaoRequest.cliente(customer);

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address.public_area deve ter no máximo 150 caracteres"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address.number deve ser maior que zero (0)"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address.additional_address_details deve ter no máximo 150 caracteres"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address.city deve ter no máximo 60 caracteres"))
        ;

        address = new Address(
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                0,
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                null
        );

        customer = new Customer(null, null, null, address,null,null, null);

        erros = this.validacaoRequest.cliente(customer);

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address.public_area deve ter no máximo 150 caracteres"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address.number deve ser maior que zero (0)"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address.additional_address_details deve ter no máximo 150 caracteres"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo address.city deve ter no máximo 60 caracteres"))
        ;

        Contact contact = new Contact(null, null);

        customer = new Customer(null, null, null,null,null, contact, null);

        erros = this.validacaoRequest.cliente(customer);

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo contact.email deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo contact.cellphone deve estar preenchido"))

        ;

        contact = new Contact("teste", "bn78b2ty3784n6y7823");

        customer = new Customer(null, null, null,null,null, contact, null);

        erros = this.validacaoRequest.cliente(customer);

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo contact.email deve ser um email válido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo contact.cellphone está com formatação inválida"))

        ;

    }


    @Test
    void deveRetornarSucesso(){
        Customer customer = GenerateData.customer(Boolean.TRUE);

        List<String> erros = this.validacaoRequest.cliente(customer);

        assertThat(erros).isEmpty();

    }

}
