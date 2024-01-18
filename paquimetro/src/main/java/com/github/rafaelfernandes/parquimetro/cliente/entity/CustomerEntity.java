package com.github.rafaelfernandes.parquimetro.cliente.entity;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Document("customer")
public record CustomerEntity(

        UUID id,
        String name,
        @Indexed(unique = true)
        Long document,
        AddressEntity address,
        PaymentMethod payment_method,
        ContactEntity contact,
        List<String> cars
) {

        public static CustomerEntity from(Customer customer, Boolean isNew){

                AddressEntity addressEntity = new AddressEntity(
                        customer.address().public_area(),
                        customer.address().number(),
                        customer.address().additional_address_details(),
                        customer.address().neighborhood(),
                        customer.address().city(),
                        customer.address().state()
                );

                ContactEntity contactEntity = new ContactEntity(
                        customer.contact().email(),
                        customer.contact().cellphone()
                );


                return new CustomerEntity(
                        isNew? UUID.randomUUID() : customer.id(),
                        customer.name(),
                        customer.document(),
                        addressEntity,
                        customer.payment_method(),
                        contactEntity,
                        customer.cars()
                );
        }

        public static CustomerEntity updateCars(CustomerEntity customerEntity, Set<String> cars){
                return new CustomerEntity(
                        customerEntity.id(),
                        customerEntity.name(),
                        customerEntity.document(),
                        customerEntity.address(),
                        customerEntity.payment_method(),
                        customerEntity.contact(),
                        cars.stream().toList()
                );
        }
}
