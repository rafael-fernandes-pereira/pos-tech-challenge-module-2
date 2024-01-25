package com.github.rafaelfernandes.parquimetro.parking.entity;

import com.github.rafaelfernandes.parquimetro.customer.entity.ContactEntity;
import com.github.rafaelfernandes.parquimetro.customer.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.parking.controller.response.open.ParkingOpened;
import com.github.rafaelfernandes.parquimetro.parking.controller.response.close.Receipt;
import com.github.rafaelfernandes.parquimetro.parking.enums.ParkingType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("parking_finished")
public record ParkingFinishedEntity(
        UUID id,
        UUID customerId,
        String car,
        String name,
        ContactEntity contact,
        PaymentMethod paymentMethod,
        ParkingType parkingType,
        Receipt receipt
) {
        public static ParkingFinishedEntity from (ParkingOpened parkingOpened, Receipt receipt){
                return new ParkingFinishedEntity(
                        UUID.randomUUID(),
                        parkingOpened.customer_id(),
                        parkingOpened.car(),
                        parkingOpened.name(),
                        new ContactEntity(
                                parkingOpened.contact().email(),
                                parkingOpened.contact().cellphone()
                        ),
                        parkingOpened.payment_method(),
                        parkingOpened.parking_type(),
                        receipt
                );
        }
}
