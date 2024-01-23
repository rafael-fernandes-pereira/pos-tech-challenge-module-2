package com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Contact;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.ParkingFinishedEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.ParkingType;

import java.util.UUID;

public record ParkingFinished(
        UUID id,
        UUID customer_id,
        String car,
        String name,
        Contact contact,
        PaymentMethod payment_method,
        ParkingType parking_type,
        Integer duraration,
        Receipt receipt

) {

    public static ParkingFinished fromEstacionamentoEncerradoEntity(ParkingFinishedEntity entity){
        return new ParkingFinished(
                entity.id(),
                entity.customerId(),
                entity.car(),
                entity.name(),
                new Contact(
                        entity.contact().email(),
                        entity.contact().celphone()
                ),
                entity.paymentMethod(),
                entity.parkingType(),
                entity.duration(),
                entity.receipt()
        );
    }

}
