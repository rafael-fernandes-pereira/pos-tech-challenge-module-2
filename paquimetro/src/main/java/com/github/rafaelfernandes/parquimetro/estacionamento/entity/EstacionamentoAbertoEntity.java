package com.github.rafaelfernandes.parquimetro.estacionamento.entity;

import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
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

        FormaPagamento formaPagamento,
        TipoPeriodo tipoPeriodo,
        Integer duracaoFixa,
        LocalDateTime inicio
) {
}
