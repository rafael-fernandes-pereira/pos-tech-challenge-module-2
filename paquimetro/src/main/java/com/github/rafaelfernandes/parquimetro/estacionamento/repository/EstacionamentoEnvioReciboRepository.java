package com.github.rafaelfernandes.parquimetro.estacionamento.repository;

import com.github.rafaelfernandes.parquimetro.estacionamento.entity.ParkingOpenedReceiptEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface EstacionamentoEnvioReciboRepository extends MongoRepository<ParkingOpenedReceiptEntity, UUID> {


}
