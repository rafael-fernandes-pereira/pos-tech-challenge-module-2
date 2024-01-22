package com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Contact;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.ParkingOpenedEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.ParkingType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParkingOpened(
        UUID id,
        UUID customer_id,
        String car,
        String name,
        Contact contact,
        PaymentMethod payment_method,
        ParkingType parking_type,
        Integer duration,
        LocalDateTime start

) {

    public static ParkingOpened fromOpenedParking(ParkingOpenedEntity entity){
        return new ParkingOpened(
                entity.id(),
                entity.clienteId(),
                entity.carro(),
                entity.nome(),
                new Contact(
                        entity.contato().email(),
                        entity.contato().celphone()
                ),
                entity.paymentMethod(),
                entity.parkingType(),
                entity.duracaoFixa(),
                entity.inicio()
        );
    }

}
