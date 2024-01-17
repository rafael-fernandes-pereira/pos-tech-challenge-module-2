package com.github.rafaelfernandes.parquimetro.estacionamento.repository;

import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoAbertoEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoEncerradoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface EstacionamentoEncerradoRepository extends MongoRepository<EstacionamentoEncerradoEntity, UUID> {

    EstacionamentoAbertoEntity findByClienteIdAndCarro(UUID clienteId, String carro);


}
