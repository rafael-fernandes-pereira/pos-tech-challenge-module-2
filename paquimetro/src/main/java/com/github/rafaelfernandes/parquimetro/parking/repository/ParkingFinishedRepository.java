package com.github.rafaelfernandes.parquimetro.parking.repository;

import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingFinishedEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ParkingFinishedRepository extends MongoRepository<ParkingFinishedEntity, UUID> {

}
