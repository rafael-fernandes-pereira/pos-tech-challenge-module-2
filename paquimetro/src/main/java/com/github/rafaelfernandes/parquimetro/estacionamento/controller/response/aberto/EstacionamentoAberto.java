package com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Contact;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoAbertoEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;

import java.time.LocalDateTime;
import java.util.UUID;

public record EstacionamentoAberto(
        UUID id,
        UUID cliente_id,
        String carro,
        String nome,
        Contact contact,
        PaymentMethod payment_method,
        TipoPeriodo tipo_periodo,
        Integer duracao_fixa,
        LocalDateTime inicio

) {

    public static EstacionamentoAberto fromEstacionamentoAberto(EstacionamentoAbertoEntity entity){
        return new EstacionamentoAberto(
                entity.id(),
                entity.clienteId(),
                entity.carro(),
                entity.nome(),
                new Contact(
                        entity.contato().email(),
                        entity.contato().celphone()
                ),
                entity.paymentMethod(),
                entity.tipoPeriodo(),
                entity.duracaoFixa(),
                entity.inicio()
        );
    }

}
