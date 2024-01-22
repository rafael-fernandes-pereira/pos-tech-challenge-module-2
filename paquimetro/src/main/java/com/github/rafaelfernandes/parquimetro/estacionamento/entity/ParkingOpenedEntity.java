package com.github.rafaelfernandes.parquimetro.estacionamento.entity;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.entity.ContactEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.ParkingType;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document("estacionamento_aberto")
public record ParkingOpenedEntity(
        UUID id,
        UUID clienteId,
        @Indexed(unique = true)
        String carro,
        String nome,
        ContactEntity contato,
        PaymentMethod paymentMethod,
        ParkingType parkingType,
        Integer duracaoFixa,
        LocalDateTime inicio
) {
        public static ParkingOpenedEntity create(Customer customer, String carro, ParkingType parkingType, Integer duracao){

                return new ParkingOpenedEntity(
                        UUID.randomUUID(),
                        customer.id(),
                        carro,
                        customer.name(),
                        new ContactEntity(
                                customer.contact().email(),
                                customer.contact().cellphone()
                        ),
                        customer.payment_method(),
                        parkingType,
                        parkingType.equals(ParkingType.FIX) ? duracao : 1,
                        LocalDateTime.now()
                );

        }
}
