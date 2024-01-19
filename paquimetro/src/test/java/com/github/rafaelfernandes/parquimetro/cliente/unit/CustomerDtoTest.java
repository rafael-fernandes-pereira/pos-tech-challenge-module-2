package com.github.rafaelfernandes.parquimetro.cliente.unit;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.util.GenerateData;
import com.github.rafaelfernandes.parquimetro.cliente.entity.CustomerEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerDtoTest {

    @Test
    void retornaClienteNovoEntityComSucesso(){

        Customer customer = GenerateData.customer(Boolean.TRUE);

        CustomerEntity entity = CustomerEntity.from(customer, Boolean.TRUE);

        assertThat(customer.name()).isEqualTo(entity.name());
        assertThat(customer.document()).isEqualTo(entity.document());

        assertThat(customer.address().public_area()).isEqualTo(entity.address().public_area());
        assertThat(customer.address().number()).isEqualTo(entity.address().number());
        assertThat(customer.address().additional_address_details()).isEqualTo(entity.address().additional_address_details());
        assertThat(customer.address().neighborhood()).isEqualTo(entity.address().neighborhood());
        assertThat(customer.address().city()).isEqualTo(entity.address().city());
        assertThat(customer.address().state()).isEqualTo(entity.address().state());

        assertThat(customer.payment_method()).isEqualTo(entity.paymentMethod());





    }

}
