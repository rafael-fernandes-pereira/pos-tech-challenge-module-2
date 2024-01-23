package com.github.rafaelfernandes.parquimetro.parking.repository;

import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingOpenedEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ParkingOpenedRepository extends MongoRepository<ParkingOpenedEntity, UUID> {

    ParkingOpenedEntity findByCustomerIdAndCar(UUID customerId, String car);


}
