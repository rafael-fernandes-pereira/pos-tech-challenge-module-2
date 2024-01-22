package com.github.rafaelfernandes.parquimetro.estacionamento.entity;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.entity.ContactEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.ParkingType;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document("parking_opened")
public record ParkingOpenedEntity(
        UUID id,
        UUID customerId,
        @Indexed(unique = true)
        String car,
        String name,
        ContactEntity contact,
        PaymentMethod paymentMethod,
        ParkingType parkingType,
        Integer duration,
        LocalDateTime start
) {
        public static ParkingOpenedEntity create(Customer customer, String car, ParkingType parkingType, Integer duration){

                return new ParkingOpenedEntity(
                        UUID.randomUUID(),
                        customer.id(),
                        car,
                        customer.name(),
                        new ContactEntity(
                                customer.contact().email(),
                                customer.contact().cellphone()
                        ),
                        customer.payment_method(),
                        parkingType,
                        parkingType.equals(ParkingType.FIX) ? duration : 1,
                        LocalDateTime.now()
                );

        }
}
