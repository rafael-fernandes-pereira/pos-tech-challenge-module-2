package com.github.rafaelfernandes.parquimetro.parking.controller.response.close;

import com.github.rafaelfernandes.parquimetro.customer.controller.request.Contact;
import com.github.rafaelfernandes.parquimetro.customer.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingFinishedEntity;
import com.github.rafaelfernandes.parquimetro.parking.enums.ParkingType;

import java.util.UUID;

public record ParkingFinished(
        UUID id,
        UUID customer_id,
        String car,
        String name,
        Contact contact,
        PaymentMethod payment_method,
        ParkingType parking_type,
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
                entity.receipt()
        );
    }

}
