package com.github.rafaelfernandes.parquimetro.customer.service;

import com.github.rafaelfernandes.parquimetro.customer.entity.CustomerEntity;
import com.github.rafaelfernandes.parquimetro.customer.exception.CustomerDuplicateException;
import com.github.rafaelfernandes.parquimetro.customer.exception.CustomerNotFoundException;
import com.github.rafaelfernandes.parquimetro.customer.exception.CustomerValidationException;
import com.github.rafaelfernandes.parquimetro.customer.repository.CustomerRepository;
import com.github.rafaelfernandes.parquimetro.customer.validation.ValidacaoRequest;
import com.github.rafaelfernandes.parquimetro.customer.controller.request.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired private CustomerRepository repository;

    @Autowired private ValidacaoRequest validacaoRequest;

    public Customer create(Customer customer){

        List<String> erros = validacaoRequest.cliente(customer);

        if (!erros.isEmpty()) throw new CustomerValidationException(erros);

        try {

            CustomerEntity customerToSave = CustomerEntity.from(customer, Boolean.TRUE);

            CustomerEntity customerSaved = repository.insert(customerToSave);

            return Customer.from(customerSaved);

        } catch (DuplicateKeyException ex) {
            throw new CustomerDuplicateException();
        }


    }

    public Customer findBydId(UUID requestId){

        Optional<CustomerEntity> customerEntity = repository.findById(requestId);

        return customerEntity
                .map(Customer::from)
                .orElseThrow(CustomerNotFoundException::new);

    }

    public Page<Customer> getAll(Pageable pageable){

        Page<CustomerEntity> customers =  this.repository.findAll(pageable);

        return customers.map(Customer::from);

    }

    public void update(UUID customerId, Customer customer){

        if (!this.repository.existsById(customerId)) throw new CustomerNotFoundException();

        try {

            CustomerEntity entity = CustomerEntity.from(customer, Boolean.FALSE);
            this.repository.save(entity);

        } catch (DuplicateKeyException ex) {

            throw new CustomerDuplicateException();

        }

    }

    public void delete(UUID customerId) {

        if (!this.repository.existsById(customerId)) throw new CustomerNotFoundException();

        this.repository.deleteById(customerId);
    }
}
