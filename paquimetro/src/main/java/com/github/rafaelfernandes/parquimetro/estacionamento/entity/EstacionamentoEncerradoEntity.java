package com.github.rafaelfernandes.parquimetro.estacionamento.entity;

import com.github.rafaelfernandes.parquimetro.cliente.entity.ContactEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.EstacionamentoAberto;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.Recibo;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("estacionamento_encerrado")
public record EstacionamentoEncerradoEntity(
        UUID id,
        UUID clienteId,
        String carro,
        String nome,
        ContactEntity contato,
        PaymentMethod paymentMethod,
        TipoPeriodo tipoPeriodo,
        Integer duracaoFixa,
        Recibo recibo
) {
        public static EstacionamentoEncerradoEntity from (EstacionamentoAberto estacionamentoAberto, Recibo recibo){
                return new EstacionamentoEncerradoEntity(
                        UUID.randomUUID(),
                        estacionamentoAberto.cliente_id(),
                        estacionamentoAberto.carro(),
                        estacionamentoAberto.nome(),
                        new ContactEntity(
                                estacionamentoAberto.contact().email(),
                                estacionamentoAberto.contact().cellphone()
                        ),
                        estacionamentoAberto.payment_method(),
                        estacionamentoAberto.tipo_periodo(),
                        estacionamentoAberto.duracao_fixa(),
                        recibo
                );
        }
}
