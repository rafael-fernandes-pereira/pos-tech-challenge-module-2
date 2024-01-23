package com.github.rafaelfernandes.parquimetro.customer.repository;

import com.github.rafaelfernandes.parquimetro.customer.entity.CustomerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface CustomerRepository extends MongoRepository<CustomerEntity, UUID>, PagingAndSortingRepository<CustomerEntity, UUID> {
}
