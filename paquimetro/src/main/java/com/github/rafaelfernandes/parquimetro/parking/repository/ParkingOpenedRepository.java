package com.github.rafaelfernandes.parquimetro.parking.repository;

import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingOpenedEntity;
import com.github.rafaelfernandes.parquimetro.parking.enums.ParkingType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ParkingOpenedRepository extends MongoRepository<ParkingOpenedEntity, UUID> {

    ParkingOpenedEntity findByCustomerIdAndCar(UUID customerId, String car);

    @Query("{ 'start' : { $lte : ?0 }, 'parkingType' : ?1 }")
    List<ParkingOpenedEntity> findByParkingOpened(LocalDateTime endTime, ParkingType parkingType);


}
