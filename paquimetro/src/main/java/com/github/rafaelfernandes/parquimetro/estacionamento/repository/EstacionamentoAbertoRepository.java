package com.github.rafaelfernandes.parquimetro.estacionamento.repository;

import com.github.rafaelfernandes.parquimetro.estacionamento.entity.ParkingOpenedEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface EstacionamentoAbertoRepository extends MongoRepository<ParkingOpenedEntity, UUID> {

    ParkingOpenedEntity findByClienteIdAndCarro(UUID clienteId, String carro);


}
