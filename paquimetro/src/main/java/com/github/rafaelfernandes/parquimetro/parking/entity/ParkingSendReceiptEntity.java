package com.github.rafaelfernandes.parquimetro.parking.entity;

import com.github.rafaelfernandes.parquimetro.parking.controller.response.encerrado.Receipt;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("parking_send_receipt")
public record ParkingSendReceiptEntity(
        UUID parkingFinishedId,
        String name,
        String email,
        String cellphone,
        Receipt receipt
) {
}
