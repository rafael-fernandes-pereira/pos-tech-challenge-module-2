package com.github.rafaelfernandes.parquimetro.estacionamento.repository;

import com.github.rafaelfernandes.parquimetro.estacionamento.entity.ParkingOpenedEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoEncerradoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface EstacionamentoEncerradoRepository extends MongoRepository<EstacionamentoEncerradoEntity, UUID> {

    ParkingOpenedEntity findByClienteIdAndCarro(UUID clienteId, String carro);


}
