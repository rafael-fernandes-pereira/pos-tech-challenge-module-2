package com.github.rafaelfernandes.parquimetro.customer.service;

import com.github.rafaelfernandes.parquimetro.customer.entity.CustomerEntity;
import com.github.rafaelfernandes.parquimetro.customer.exception.CarEmptyAfterDeleteException;
import com.github.rafaelfernandes.parquimetro.customer.exception.CarEmptyException;
import com.github.rafaelfernandes.parquimetro.customer.exception.CarNotFoundException;
import com.github.rafaelfernandes.parquimetro.customer.exception.CustomerNotFoundException;
import com.github.rafaelfernandes.parquimetro.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
