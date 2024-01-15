package com.github.rafaelfernandes.parquimetro.dados;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.controller.Contato;
import com.github.rafaelfernandes.parquimetro.controller.Endereco;
import com.github.rafaelfernandes.parquimetro.enums.Estados;
import com.github.rafaelfernandes.parquimetro.enums.FormaPagamento;
import net.datafaker.Faker;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class GerarCadastro {

    private static final Faker faker = new Faker(new Locale("pt", "BR"));

    private static final Random random = new Random();

    public static Cliente cliente(Boolean novo){

        Integer randFormaDePagamento = random.nextInt(0, 2);
        FormaPagamento formaPagamento = FormaPagamento.values()[randFormaDePagamento];

        return new Cliente(
                novo ? null : UUID.fromString(faker.internet().uuid()),

                faker.name().fullName(),
                Long.valueOf(faker.cpf().valid(false)),
                new Endereco(
                        faker.address().streetAddress(),
                        Integer.valueOf(faker.address().streetAddressNumber()),
                        faker.address().secondaryAddress(),
                        faker.address().secondaryAddress(),
                        faker.address().city(),
                        Estados.valueOf(faker.address().stateAbbr())
                ),
                formaPagamento,
                new Contato(
                        faker.internet().emailAddress(),
                        faker.phoneNumber().cellPhone().replaceAll("[(),\\-, ]", "")
                )
        );
    }


    private static String placa(){
        return new StringBuilder()
                .append(gerarLetras(3).toUpperCase())
                .append(gerarDigitos(1))
                .append(gerarLetras(1).toUpperCase())
                .append(gerarDigitos(2))
                .toString();
    }

    private static String gerarLetras(int quantidade) {
        Random random = new Random();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < quantidade; i++) {
            char letra = (char) ('A' + random.nextInt(26));
            result.append(letra);
        }

        return result.toString();
    }

    private static String gerarDigitos(int quantidade) {
        Random random = new Random();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < quantidade; i++) {
            int digito = random.nextInt(10);
            result.append(digito);
        }

        return result.toString();
    }

}
