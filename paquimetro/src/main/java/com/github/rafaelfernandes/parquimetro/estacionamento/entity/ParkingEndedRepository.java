package com.github.rafaelfernandes.parquimetro.estacionamento.entity;

import com.github.rafaelfernandes.parquimetro.cliente.entity.ContactEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.ParkingOpened;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.Receipt;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.ParkingType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("estacionamento_encerrado")
public record ParkingEndedRepository(
        UUID id,
        UUID clienteId,
        String carro,
        String nome,
        ContactEntity contato,
        PaymentMethod paymentMethod,
        ParkingType parkingType,
        Integer duracaoFixa,
        Receipt bill
) {
        public static ParkingEndedRepository from (ParkingOpened parkingOpened, Receipt bill){
                return new ParkingEndedRepository(
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
