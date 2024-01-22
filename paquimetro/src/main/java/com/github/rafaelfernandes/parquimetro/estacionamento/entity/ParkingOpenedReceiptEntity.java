package com.github.rafaelfernandes.parquimetro.estacionamento.entity;

import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.Receipt;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("estacionamento_envio_recibo")
public record ParkingOpenedReceiptEntity(
        UUID estacionamentoEncerradoId,
        String nome,
        String email,
        String telefone,
        Receipt bill
) {
}
