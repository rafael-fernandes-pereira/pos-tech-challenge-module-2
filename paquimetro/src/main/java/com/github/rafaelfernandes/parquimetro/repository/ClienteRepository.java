package com.github.rafaelfernandes.parquimetro.repository;

import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ClienteRepository extends CrudRepository<ClienteEntity, UUID> {
}
