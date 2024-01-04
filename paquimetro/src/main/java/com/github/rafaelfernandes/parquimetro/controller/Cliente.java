package com.github.rafaelfernandes.parquimetro.controller;

import java.util.List;
import java.util.UUID;

public record Cliente(

        UUID id,
        String nome,
        Long documento,
        Endereco endereco,
        FormaPagamento forma_pagamento,
        Contato contato,
        List<String> carros
) {
}
