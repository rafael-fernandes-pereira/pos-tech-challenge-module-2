package com.github.rafaelfernandes.parquimetro.estacionamento.repository;

import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoAbertoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstacionamentoRepository extends MongoRepository<EstacionamentoAbertoEntity, UUID> {

    EstacionamentoAbertoEntity findByClienteIdAndCarro(UUID clienteId, String carro);


}