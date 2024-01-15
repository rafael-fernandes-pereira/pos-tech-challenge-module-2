package com.github.rafaelfernandes.parquimetro.e2e;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.Contato;
import com.github.rafaelfernandes.parquimetro.controller.Endereco;
import com.github.rafaelfernandes.parquimetro.controller.response.Message;
import com.github.rafaelfernandes.parquimetro.enums.Estados;
import com.github.rafaelfernandes.parquimetro.enums.FormaPagamento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ParquimetroCadastroJsonTest {

    @Autowired
    private JacksonTester<Message> json;

    private final Message message = new Message(
            new Cliente(
                UUID.fromString("7ffb4be7-985c-483e-ac17-bf899a172b4e"),
                "Luisa Antero",
                12345678L,
                new Endereco(
                        "Rua projetada 3",
                        123,
                        "Muro Azul",
                        "Anhumas",
                        "São Paulo",
                        Estados.MG
                ),
                FormaPagamento.CARTAO_CREDITO,
                new Contato(
                        "luisa.pereira@fiap.com.br",
                        "11999887766"
                )
            ),
            Boolean.FALSE,
            HttpStatus.OK.value(),
            List.of()
    );

    @Test
    void verificaParametrosDeCadastroJsonSerializado() throws IOException {

        JsonContent<Message> dadosCadastro = json.write(message);

        assertThat(dadosCadastro).isStrictlyEqualToJson("esperado.json");

        assertThat(dadosCadastro).hasJsonPathValue("@.cliente.id");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.cliente.id")
                .isEqualTo("7ffb4be7-985c-483e-ac17-bf899a172b4e");

        assertThat(dadosCadastro).hasJsonPathValue("@.cliente.nome");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.cliente.nome")
                .isEqualTo("Luisa Antero");

        assertThat(dadosCadastro).hasJsonPathNumberValue("@.cliente.documento");

        assertThat(dadosCadastro)
                .extractingJsonPathNumberValue("@.cliente.documento")
                .isEqualTo(12345678);

        assertThat(dadosCadastro).hasJsonPathValue("@.cliente.endereco.logradouro");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.cliente.endereco.logradouro")
                .isEqualTo("Rua projetada 3");


        assertThat(dadosCadastro).hasJsonPathNumberValue("@.cliente.endereco.numero");

        assertThat(dadosCadastro)
                .extractingJsonPathNumberValue("@.cliente.endereco.numero")
                .isEqualTo(123);


        assertThat(dadosCadastro).hasJsonPathValue("@.cliente.endereco.complemento");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.cliente.endereco.complemento")
                .isEqualTo("Muro Azul");


        assertThat(dadosCadastro).hasJsonPathValue("@.cliente.endereco.bairro");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.cliente.endereco.bairro")
                .isEqualTo("Anhumas");

        assertThat(dadosCadastro).hasJsonPathValue("@.cliente.endereco.cidade");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.cliente.endereco.cidade")
                .isEqualTo("São Paulo");

        assertThat(dadosCadastro).hasJsonPathValue("@.cliente.endereco.estado");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.cliente.endereco.estado")
                .isEqualTo("MG");

        assertThat(dadosCadastro).hasJsonPathValue("@.cliente.forma_pagamento");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.cliente.forma_pagamento")
                .isEqualTo("CARTAO_CREDITO");

        assertThat(dadosCadastro).hasJsonPathValue("@.cliente.contato.email");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.cliente.contato.email")
                .isEqualTo("luisa.pereira@fiap.com.br");

        assertThat(dadosCadastro).hasJsonPathValue("@.cliente.contato.celular");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.cliente.contato.celular")
                .isEqualTo("11999887766");


    }

    @Test
    void verificaParametrosDeCadastroJsonDeserializado() throws IOException {
        String esperado = """
              
                {
                   "cliente":{
                     "id": "7ffb4be7-985c-483e-ac17-bf899a172b4e",
                     "nome": "Luisa Antero",
                     "documento": 12345678,
                     "endereco": {
                       "logradouro": "Rua projetada 3",
                       "numero": 123,
                       "complemento": "Muro Azul",
                       "bairro": "Anhumas",
                       "cidade": "São Paulo",
                       "estado": "MG"
                     },
                     "forma_pagamento": "CARTAO_CREDITO",
                     "contato": {
                       "email": "luisa.pereira@fiap.com.br",
                       "celular": "11999887766"
                     },
                     "carros": [
                       "IUW8E56",
                       "JEZ8A17",
                       "YIT8U05"
                     ]
                   },
                   "isError": false,
                   "httpStatusCode": "200",
                   "errors": []
                 }
                """;


        assertThat(json.parse(esperado)).isEqualTo(message);

        Message messageEsperado = json.parseObject(esperado);



        assertThat(messageEsperado.cliente().id()).isEqualTo(UUID.fromString("7ffb4be7-985c-483e-ac17-bf899a172b4e"));

        assertThat(messageEsperado.cliente().nome()).isEqualTo("Luisa Antero");

        assertThat(messageEsperado.cliente().documento()).isEqualTo(12345678);

        assertThat(messageEsperado.cliente().endereco().logradouro()).isEqualTo("Rua projetada 3");

        assertThat(messageEsperado.cliente().endereco().numero()).isEqualTo(123);

        assertThat(messageEsperado.cliente().endereco().complemento()).isEqualTo("Muro Azul");

        assertThat(messageEsperado.cliente().endereco().bairro()).isEqualTo("Anhumas");

        assertThat(messageEsperado.cliente().endereco().cidade()).isEqualTo("São Paulo");

        assertThat(messageEsperado.cliente().endereco().estado()).isEqualTo(Estados.MG);

        assertThat(messageEsperado.cliente().forma_pagamento().toString()).isEqualTo("CARTAO_CREDITO");

        assertThat(messageEsperado.cliente().contato().email()).isEqualTo("luisa.pereira@fiap.com.br");

        assertThat(messageEsperado.cliente().contato().celular()).isEqualTo("11999887766");

    }

}
