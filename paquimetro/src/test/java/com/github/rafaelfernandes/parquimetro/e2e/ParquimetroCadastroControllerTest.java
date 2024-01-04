package com.github.rafaelfernandes.parquimetro.e2e;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.dados.GerarCadastro;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ParquimetroCadastroControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void deveRetornarDadosDeUmClienteQuandoExistirNaBase(){

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/clientes/7ffb4be7-985c-483e-ac17-bf899a172b4e",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.id");
        assertEquals("7ffb4be7-985c-483e-ac17-bf899a172b4e", id);

        String nome = documentContext.read("$.nome");
        assertEquals("Luisa Antero", nome);

        int documento = documentContext.read("$.documento");
        assertEquals(12345678, documento);

        String logradouro = documentContext.read("$.endereco.logradouro");
        assertEquals("Rua projetada 3", logradouro);

        int numero = documentContext.read("$.endereco.numero");
        assertEquals(123, numero);

        String observacao = documentContext.read("$.endereco.observacao");
        assertEquals("Muro Azul", observacao);

        String bairro = documentContext.read("$.endereco.bairro");
        assertEquals("Anhumas", bairro);

        String cidade = documentContext.read("$.endereco.cidade");
        assertEquals("São Paulo", cidade);

        String estado = documentContext.read("$.endereco.estado");
        assertEquals("MG", estado);

        String formaPagamento = documentContext.read("$.forma_pagamento");
        assertEquals("CARTAO_CREDITO", formaPagamento);

        String email = documentContext.read("$.contato.email");
        assertEquals("luisa.pereira@fiap.com.br", email);

        String telefone = documentContext.read("$.contato.telefone");
        assertEquals("11999887766", telefone);

        List<String> carros = documentContext.read("$.carros");
        assertEquals(Arrays.asList("IUW8E56", "JEZ8A17", "YIT8U05"), carros);

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

    }







}
