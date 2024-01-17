package com.github.rafaelfernandes.parquimetro.estacionamento.entity;

import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.Recibo;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("estacionamento_envio_recibo")
public record EstacionamentoEnvioRecibo(
        UUID estacionamentoEncerradoId,
        String nome,
        String email,
        String telefone,
        Recibo recibo
) {
}
