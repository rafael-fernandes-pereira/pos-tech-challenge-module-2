package com.github.rafaelfernandes.parquimetro.cliente.service;

import com.github.rafaelfernandes.parquimetro.cliente.entity.CustomerEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.cliente.exception.CustomerNotFoundException;
import com.github.rafaelfernandes.parquimetro.cliente.exception.PaymentMethodNull;
import com.github.rafaelfernandes.parquimetro.cliente.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentMethodService {

    @Autowired private CustomerRepository repository;

    public void change(UUID customerId, String paymenteMethodStr){

        PaymentMethod paymentMethod = PaymentMethod.getByName(paymenteMethodStr);

        if (paymentMethod == null) throw new PaymentMethodNull();

        Optional<CustomerEntity> customer = this.repository.findById(customerId);

        if (customer.isEmpty()) throw new CustomerNotFoundException();

        CustomerEntity customerEntity = new CustomerEntity(
                customer.get().id(),
                customer.get().name(),
                customer.get().document(),
                customer.get().address(),
                paymentMethod,
                customer.get().contact(),
                customer.get().cars()
        );

        this.repository.save(customerEntity);
    }

    public PaymentMethod get(UUID customerId) {
        Optional<CustomerEntity> customerEntity = this.repository.findById(customerId);

        return customerEntity
                .map(CustomerEntity::paymentMethod)
                .orElseThrow(CustomerNotFoundException::new)
        ;
    }
}
