package com.github.rafaelfernandes.parquimetro.estacionamento.repository;

import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoAbertoEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoEncerradoEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoEnvioRecibo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface EstacionamentoEnvioReciboRepository extends MongoRepository<EstacionamentoEnvioRecibo, UUID> {


}
