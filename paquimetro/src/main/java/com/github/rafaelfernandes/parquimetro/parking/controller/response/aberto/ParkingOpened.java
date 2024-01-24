package com.github.rafaelfernandes.parquimetro.parking.controller.response.aberto;

import com.github.rafaelfernandes.parquimetro.customer.controller.request.Contact;
import com.github.rafaelfernandes.parquimetro.customer.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingOpenedEntity;
import com.github.rafaelfernandes.parquimetro.parking.enums.ParkingType;

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
        LocalDateTime start,
        LocalDateTime expectedEndTime

) {

    public static ParkingOpened fromOpenedParking(ParkingOpenedEntity entity){
        return new ParkingOpened(
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
                entity.start(),
                entity.expectedEndTime()
        );
    }

}
