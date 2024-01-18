package com.github.rafaelfernandes.parquimetro.cliente.dto;

import com.github.rafaelfernandes.parquimetro.cliente.entity.CustomerEntity;
import com.github.rafaelfernandes.parquimetro.cliente.entity.ContactEntity;
import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.entity.AddressEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClienteDto {

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


}
