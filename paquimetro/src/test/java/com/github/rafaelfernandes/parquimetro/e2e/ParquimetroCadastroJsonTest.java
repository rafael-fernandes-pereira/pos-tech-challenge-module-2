package com.github.rafaelfernandes.parquimetro.e2e;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.Contato;
import com.github.rafaelfernandes.parquimetro.controller.Endereco;
import com.github.rafaelfernandes.parquimetro.enums.FormaPagamento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ParquimetroCadastroJsonTest {

    @Autowired
    private JacksonTester<Cliente> json;

    private Cliente cliente = new Cliente(
            UUID.fromString("7ffb4be7-985c-483e-ac17-bf899a172b4e"),
            "Luisa Antero",
            12345678L,
            new Endereco(
                    "Rua projetada 3",
                    123,
                    "Muro Azul",
                    "Anhumas",
                    "São Paulo",
                    "MG"
            ),
            FormaPagamento.CARTAO_CREDITO,
            new Contato(
                    "luisa.pereira@fiap.com.br",
                    "11999887766"
            ),
            List.of("IUW8E56", "JEZ8A17", "YIT8U05")
    );

    @Test
    void verificaParametrosDeCadastroJsonSerializado() throws IOException {



        JsonContent<Cliente> dadosCadastro = json.write(cliente);

        assertThat(dadosCadastro).isStrictlyEqualToJson("esperado.json");

        assertThat(dadosCadastro).hasJsonPathValue("@.id");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.id")
                .isEqualTo("7ffb4be7-985c-483e-ac17-bf899a172b4e");

        assertThat(dadosCadastro).hasJsonPathValue("@.nome");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.nome")
                .isEqualTo("Luisa Antero");

        assertThat(dadosCadastro).hasJsonPathNumberValue("@.documento");

        assertThat(dadosCadastro)
                .extractingJsonPathNumberValue("@.documento")
                .isEqualTo(12345678);

        assertThat(dadosCadastro).hasJsonPathValue("@.endereco.logradouro");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.endereco.logradouro")
                .isEqualTo("Rua projetada 3");


        assertThat(dadosCadastro).hasJsonPathNumberValue("@.endereco.numero");

        assertThat(dadosCadastro)
                .extractingJsonPathNumberValue("@.endereco.numero")
                .isEqualTo(123);


        assertThat(dadosCadastro).hasJsonPathValue("@.endereco.observacao");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.endereco.observacao")
                .isEqualTo("Muro Azul");


        assertThat(dadosCadastro).hasJsonPathValue("@.endereco.bairro");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.endereco.bairro")
                .isEqualTo("Anhumas");

        assertThat(dadosCadastro).hasJsonPathValue("@.endereco.cidade");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.endereco.cidade")
                .isEqualTo("São Paulo");

        assertThat(dadosCadastro).hasJsonPathValue("@.endereco.estado");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.endereco.estado")
                .isEqualTo("MG");

        assertThat(dadosCadastro).hasJsonPathValue("@.forma_pagamento");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.forma_pagamento")
                .isEqualTo("CARTAO_CREDITO");

        assertThat(dadosCadastro).hasJsonPathValue("@.contato.email");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.contato.email")
                .isEqualTo("luisa.pereira@fiap.com.br");

        assertThat(dadosCadastro).hasJsonPathValue("@.contato.telefone");

        assertThat(dadosCadastro)
                .extractingJsonPathValue("@.contato.telefone")
                .isEqualTo("11999887766");

        assertThat(dadosCadastro).hasJsonPathArrayValue("@.carros");

        assertThat(dadosCadastro)
                .extractingJsonPathArrayValue("@.carros")
                .isEqualTo(List.of("IUW8E56", "JEZ8A17", "YIT8U05"));


    }

    @Test
    void verificaParametrosDeCadastroJsonDeserializado() throws IOException {
        String esperado = """
                {
                  "id": "7ffb4be7-985c-483e-ac17-bf899a172b4e",
                  "nome": "Luisa Antero",
                  "documento": 12345678,
                  "endereco": {
                    "logradouro": "Rua projetada 3",
                    "numero": 123,
                    "observacao": "Muro Azul",
                    "bairro": "Anhumas",
                    "cidade": "São Paulo",
                    "estado": "MG"
                  },
                  "forma_pagamento": "CARTAO_CREDITO",
                  "contato": {
                    "email": "luisa.pereira@fiap.com.br",
                    "telefone": 11999887766
                  },
                  "carros": [
                    "IUW8E56",
                    "JEZ8A17",
                    "YIT8U05"
                  ]
                }
                """;


        assertThat(json.parse(esperado)).isEqualTo(cliente);

        Cliente clienteConfirmado = json.parseObject(esperado);

        assertThat(clienteConfirmado.id()).isEqualTo(UUID.fromString("7ffb4be7-985c-483e-ac17-bf899a172b4e"));

        assertThat(clienteConfirmado.nome()).isEqualTo("Luisa Antero");

        assertThat(clienteConfirmado.documento()).isEqualTo(12345678);

        assertThat(clienteConfirmado.endereco().logradouro()).isEqualTo("Rua projetada 3");

        assertThat(clienteConfirmado.endereco().numero()).isEqualTo(123);

        assertThat(clienteConfirmado.endereco().observacao()).isEqualTo("Muro Azul");

        assertThat(clienteConfirmado.endereco().bairro()).isEqualTo("Anhumas");

        assertThat(clienteConfirmado.endereco().cidade()).isEqualTo("São Paulo");

        assertThat(clienteConfirmado.endereco().estado()).isEqualTo("MG");

        assertThat(clienteConfirmado.forma_pagamento().toString()).isEqualTo("CARTAO_CREDITO");

        assertThat(clienteConfirmado.contato().email()).isEqualTo("luisa.pereira@fiap.com.br");

        assertThat(clienteConfirmado.contato().telefone()).isEqualTo("11999887766");

        assertThat(clienteConfirmado.carros()).isEqualTo(List.of("IUW8E56", "JEZ8A17", "YIT8U05"));
    }

}
