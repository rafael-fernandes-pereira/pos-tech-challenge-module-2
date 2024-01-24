package com.github.rafaelfernandes.parquimetro.parking.entity;

import com.github.rafaelfernandes.parquimetro.customer.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.customer.entity.ContactEntity;
import com.github.rafaelfernandes.parquimetro.customer.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.parking.enums.ParkingType;
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
        @Indexed
        ParkingType parkingType,
        LocalDateTime start,
        @Indexed
        LocalDateTime expectedEndTime
) {
        public static ParkingOpenedEntity create(Customer customer, String car, ParkingType parkingType, Long duration){

                LocalDateTime start = LocalDateTime.now();

                Long durationHours = parkingType.equals(ParkingType.FIX) ? duration : 1L;

                LocalDateTime expectedEndTime = LocalDateTime.now().plusHours(durationHours);

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
                        start,
                        expectedEndTime

                );

        }
}
