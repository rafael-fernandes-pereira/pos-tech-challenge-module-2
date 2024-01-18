package com.github.rafaelfernandes.parquimetro.cliente.service;

import com.github.rafaelfernandes.parquimetro.cliente.dto.MessageDTO;
import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageCarros;
import com.github.rafaelfernandes.parquimetro.cliente.entity.CustomerEntity;
import com.github.rafaelfernandes.parquimetro.cliente.exception.CarEmptyAfterDeleteException;
import com.github.rafaelfernandes.parquimetro.cliente.exception.CarEmptyException;
import com.github.rafaelfernandes.parquimetro.cliente.exception.CarNotFoundException;
import com.github.rafaelfernandes.parquimetro.cliente.exception.CustomerNotFoundException;
import com.github.rafaelfernandes.parquimetro.cliente.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CarService {

    @Autowired private CustomerRepository repository;

    public void addCars(UUID customerId, List<String> carros){

        if (carros == null || carros.isEmpty() ) throw new CarEmptyException();

        Optional<CustomerEntity> customer = this.repository.findById(customerId);

        if (customer.isEmpty()) throw new CustomerNotFoundException();

        List<String> savedCars = customer.get().cars();

        Set<String> newCars = new TreeSet<>();

        newCars.addAll(savedCars.stream().toList());
        newCars.addAll(carros.stream().toList());

        CustomerEntity customerEntity = CustomerEntity.updateCars(customer.get(), newCars);

        this.repository.save(customerEntity);

    }

    public List<String> getCars(UUID customerId){

        Optional<CustomerEntity> customerEntity = this.repository.findById(customerId);

        return customerEntity
                .map(CustomerEntity::cars)
                .orElseThrow(CustomerNotFoundException::new)
                ;


    }

    public void delete(UUID customerId, String car) {

        Optional<CustomerEntity> customer = this.repository.findById(customerId);

        if (customer.isEmpty()) throw new CustomerNotFoundException();

        List<String> carsSaved = customer.get().cars();

        if (!carsSaved.contains(car)) throw new CarNotFoundException();

        Set<String> newCars = new TreeSet<>(carsSaved);
        newCars.remove(car);

        if (newCars.isEmpty()) throw new CarEmptyAfterDeleteException();

        CustomerEntity customerEntity = CustomerEntity.updateCars(customer.get(), newCars);

        this.repository.save(customerEntity);
    }
}
