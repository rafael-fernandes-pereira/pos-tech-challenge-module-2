package com.github.rafaelfernandes.parquimetro.estacionamento.entity;

import com.github.rafaelfernandes.parquimetro.cliente.entity.ContactEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.ParkingOpened;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.Recibo;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.ParkingType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("estacionamento_encerrado")
public record EstacionamentoEncerradoEntity(
        UUID id,
        UUID clienteId,
        String carro,
        String nome,
        ContactEntity contato,
        PaymentMethod paymentMethod,
        ParkingType parkingType,
        Integer duracaoFixa,
        Recibo recibo
) {
        public static EstacionamentoEncerradoEntity from (ParkingOpened parkingOpened, Recibo recibo){
                return new EstacionamentoEncerradoEntity(
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
                        recibo
                );
        }
}
