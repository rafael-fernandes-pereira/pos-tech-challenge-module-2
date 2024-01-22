package com.github.rafaelfernandes.parquimetro.estacionamento.repository;

import com.github.rafaelfernandes.parquimetro.estacionamento.entity.ParkingOpenedEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.ParkingEndedRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface EstacionamentoEncerradoRepository extends MongoRepository<ParkingEndedRepository, UUID> {

    ParkingOpenedEntity findByClienteIdAndCarro(UUID clienteId, String carro);


}
