package com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Contact;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.ParkingEndedRepository;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.ParkingType;

import java.util.UUID;

public record EstacionamentoEncerrado(
        UUID id,
        UUID cliente_id,
        String carro,
        String nome,
        Contact contact,
        PaymentMethod forma_pagamento,
        ParkingType tipo_periodo,
        Integer duracao_fixa,
        Receipt bill

) {

    public static EstacionamentoEncerrado fromEstacionamentoEncerradoEntity(ParkingEndedRepository entity){
        return new EstacionamentoEncerrado(
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
                entity.bill()
        );
    }

}
