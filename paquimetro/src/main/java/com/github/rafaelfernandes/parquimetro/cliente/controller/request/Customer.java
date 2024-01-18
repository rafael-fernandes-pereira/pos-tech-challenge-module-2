package com.github.rafaelfernandes.parquimetro.cliente.controller.request;

import com.github.rafaelfernandes.parquimetro.cliente.entity.CustomerEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.UUID;

public record Customer(

        UUID id,
        @NotEmpty(message = "O campo name deve estar preenchido")
        @Length(min = 10, max = 100, message = "O campo name deve ter no mínimo de 20 e no máximo de 100 caracteres")
        String name,
        @NotNull(message = "O campo document deve estar preenchido")
        Long document,
        @NotNull(message = "O campo address deve estar preenchido")
        Address address,
        @NotNull(message = "O campo paymentMethod deve estar preenchido")
        PaymentMethod payment_method,
        @NotNull(message = "O campo contact deve estar preenchido")
        Contact contact,
        @NotEmpty(message = "O campo cars deve ter pelo menos uma placa")
        @Size(min = 1, message = "O campo cars deve ter pelo menos uma placa")
        List<String> cars
) {

        public static Customer from(CustomerEntity cliente){

                Address address = new Address(
                        cliente.address().public_area(),
                        cliente.address().number(),
                        cliente.address().additional_address_details(),
                        cliente.address().neighborhood(),
                        cliente.address().city(),
                        cliente.address().state()
                );

                Contact contact = new Contact(
                        cliente.contact().email(),
                        cliente.contact().celphone()
                );


                return new Customer(
                        cliente.id(),
                        cliente.name(),
                        cliente.document(),
                        address,
                        cliente.paymentMethod(),
                        contact,
                        cliente.cars()
                );
        }
}
