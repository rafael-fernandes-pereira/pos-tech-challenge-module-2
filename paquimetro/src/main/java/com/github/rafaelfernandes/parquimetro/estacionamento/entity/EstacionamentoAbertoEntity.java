package com.github.rafaelfernandes.parquimetro.estacionamento.entity;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.entity.ContactEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document("estacionamento_aberto")
public record EstacionamentoAbertoEntity(
        UUID id,
        UUID clienteId,
        @Indexed(unique = true)
        String carro,
        String nome,
        ContactEntity contato,
        PaymentMethod paymentMethod,
        TipoPeriodo tipoPeriodo,
        Integer duracaoFixa,
        LocalDateTime inicio
) {
        public static EstacionamentoAbertoEntity novo (Customer customer, String carro, TipoPeriodo tipoPeriodo, Integer duracao){

                return new EstacionamentoAbertoEntity(
                        UUID.randomUUID(),
                        customer.id(),
                        carro,
                        customer.name(),
                        new ContactEntity(
                                customer.contact().email(),
                                customer.contact().cellphone()
                        ),
                        customer.payment_method(),
                        tipoPeriodo,
                        tipoPeriodo.equals(TipoPeriodo.FIXO) ? duracao : null,
                        LocalDateTime.now()
                );

        }
}
