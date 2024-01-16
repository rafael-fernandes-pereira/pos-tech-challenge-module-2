package com.github.rafaelfernandes.parquimetro.cliente.repository;

import com.github.rafaelfernandes.parquimetro.cliente.entity.ClienteEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ClienteRepository extends MongoRepository<ClienteEntity, UUID>, PagingAndSortingRepository<ClienteEntity, UUID> {
}