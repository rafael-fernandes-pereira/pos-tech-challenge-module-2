package com.github.rafaelfernandes.parquimetro.util;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Contact;
import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Address;
import com.github.rafaelfernandes.parquimetro.cliente.enums.State;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import net.datafaker.Faker;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class GerarCadastro {

    private static final Faker faker = new Faker(new Locale("pt", "BR"));

    private static final Random random = new Random();

    public static Customer cliente(Boolean novo){

        Integer randFormaDePagamento = random.nextInt(0, 2);
        PaymentMethod paymentMethod = PaymentMethod.values()[randFormaDePagamento];

        return new Customer(
                novo ? null : UUID.fromString(faker.internet().uuid()),

                faker.name().fullName(),
                Long.valueOf(faker.cpf().valid(false)),
                new Address(
                        faker.address().streetAddress(),
                        Integer.valueOf(faker.address().streetAddressNumber()),
                        faker.address().secondaryAddress(),
                        faker.address().secondaryAddress(),
                        faker.address().city(),
                        State.valueOf(faker.address().stateAbbr())
                ),
                paymentMethod,
                new Contact(
                        faker.internet().emailAddress(),
                        faker.phoneNumber().phoneNumberNational().replaceAll("[(),\\-, ]", "")
                ),
                placas()
        );
    }



    public static List<String> placas(){

        return faker.collection(
                        GerarCadastro::placa)
                .len(1, 5)
                .generate();

    }

    public static String placa(){
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

    public static String email(){
        return faker.internet().emailAddress();
    }

    public static String cpf(){
        return faker.cpf().valid(false);
    }

    public static String celular(){
        return faker.phoneNumber().cellPhone().replaceAll("[(),\\-, ]", "");
    }



}
