package com.github.rafaelfernandes.parquimetro.unit;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.Contato;
import com.github.rafaelfernandes.parquimetro.controller.Endereco;
import com.github.rafaelfernandes.parquimetro.dados.GerarCadastro;
import com.github.rafaelfernandes.parquimetro.util.MongoContainers;
import com.github.rafaelfernandes.parquimetro.validation.ValidacaoRequest;
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
        Cliente cliente = new Cliente(null, null, null,null,null,null, null);

        List<String> erros = this.validacaoRequest.execute(cliente);

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo nome deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo documento deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo forma_pagamento deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo contato deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo carro deve ter pelo menos uma placa"))
        ;

        cliente = new Cliente(null, "Teste", null,null,null,null, null);

        erros = this.validacaoRequest.execute(cliente);

        assertThat(erros)
                .anyMatch(erro -> erro.equals("O campo nome deve ter no mínimo de 20 e no máximo de 100 caracteres"))
        ;

        cliente = new Cliente(null, "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste", null,null,null,null, new ArrayList<String>());

        erros = this.validacaoRequest.execute(cliente);

        assertThat(erros)
                .anyMatch(erro -> erro.equals("O campo nome deve ter no mínimo de 20 e no máximo de 100 caracteres"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo carro deve ter pelo menos uma placa"))
        ;

        Endereco endereco = new Endereco(null, null, null, null, null, null);
        cliente = new Cliente(null, null, null,endereco,null,null, null);

        erros = this.validacaoRequest.execute(cliente);


        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco.logradouro deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco.numero deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco.bairro deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco.cidade deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco.cidade deve estar preenchido"))

        ;


        endereco = new Endereco(
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                -1,
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                null
        );

        cliente = new Cliente(null, null, null,endereco,null,null, null);

        erros = this.validacaoRequest.execute(cliente);

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco.logradouro deve ter no máximo 150 caracteres"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco.numero deve ser maior que zero (0)"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco.complemento deve ter no máximo 150 caracteres"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco.complemento deve ter no máximo 60 caracteres"))
        ;

        endereco = new Endereco(
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                0,
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                "TesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTesteTeste",
                null
        );

        cliente = new Cliente(null, null, null,endereco,null,null, null);

        erros = this.validacaoRequest.execute(cliente);

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco.logradouro deve ter no máximo 150 caracteres"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco.numero deve ser maior que zero (0)"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco.complemento deve ter no máximo 150 caracteres"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo endereco.complemento deve ter no máximo 60 caracteres"))
        ;

        Contato contato = new Contato(null, null);

        cliente = new Cliente(null, null, null,null,null,contato, null);

        erros = this.validacaoRequest.execute(cliente);

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo contato.email deve estar preenchido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo contato.celular deve estar preenchido"))

        ;

        contato = new Contato("teste", "bn78b2ty3784n6y7823");

        cliente = new Cliente(null, null, null,null,null,contato, null);

        erros = this.validacaoRequest.execute(cliente);

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo contato.email deve ser um email válido"))
                .anyMatch(erro -> erro.equalsIgnoreCase("O campo contato.celular está com formatação inválida"))

        ;

    }


    @Test
    void deveRetornarSucesso(){
        Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);

        List<String> erros = this.validacaoRequest.execute(cliente);

        assertThat(erros).isEmpty();

    }

}
