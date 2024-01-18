package com.github.rafaelfernandes.parquimetro.cliente.service;

import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageFormaPagamento;
import com.github.rafaelfernandes.parquimetro.cliente.dto.MessageDTO;
import com.github.rafaelfernandes.parquimetro.cliente.entity.CustomerEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.cliente.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class FormaPagamentoService {

    @Autowired private CustomerRepository repository;

    public MessageFormaPagamento alterar(UUID requestId, String formaPagamentoStr){

        PaymentMethod paymentMethod = PaymentMethod.obterPorNome(formaPagamentoStr);

        if (paymentMethod == null){
            return MessageDTO.formaPagamentoError(HttpStatus.BAD_REQUEST, null);
        }


        Optional<CustomerEntity> cliente = this.repository.findById(requestId);

        if (cliente.isEmpty()) {
            return MessageDTO.formaPagamentoError(HttpStatus.NOT_FOUND, null);
        }

        CustomerEntity customerEntity = new CustomerEntity(
                requestId,
                cliente.get().name(),
                cliente.get().document(),
                cliente.get().address(),
                paymentMethod,
                cliente.get().contact(),
                cliente.get().cars()
        );

        this.repository.save(customerEntity);

        return MessageDTO.formaPagamentoSuccess(HttpStatus.NO_CONTENT, null);

    }


    public MessageFormaPagamento obter(UUID requestId) {
        Optional<CustomerEntity> cliente = this.repository.findById(requestId);

        return cliente
                .map(clienteEntity -> MessageDTO.formaPagamentoSuccess(HttpStatus.OK, clienteEntity.payment_method()))
                .orElseGet(() -> MessageDTO.formaPagamentoError(HttpStatus.NOT_FOUND, null));
    }
}
