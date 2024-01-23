package com.github.rafaelfernandes.parquimetro.parking.repository;

import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingSendReceiptEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ParkingSendReceiptRepository extends MongoRepository<ParkingSendReceiptEntity, UUID> {


}
