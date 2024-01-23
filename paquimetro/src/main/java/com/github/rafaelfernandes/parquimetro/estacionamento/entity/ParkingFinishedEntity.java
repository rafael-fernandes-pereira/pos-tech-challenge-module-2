package com.github.rafaelfernandes.parquimetro.estacionamento.entity;

import com.github.rafaelfernandes.parquimetro.cliente.entity.ContactEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.ParkingOpened;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.Receipt;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.ParkingType;
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
        Integer duration,
        Receipt receipt
) {
        public static ParkingFinishedEntity from (ParkingOpened parkingOpened, Receipt bill){
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
                        parkingOpened.duration(),
                        bill
                );
        }
}
