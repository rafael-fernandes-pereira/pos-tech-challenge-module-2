package com.github.rafaelfernandes.parquimetro.enums;

import lombok.Getter;

@Getter
public enum FormaPagamento {

    PIX ("Pix"),
    CARTAO_CREDITO("Cartão de crédito"),
    CARTAO_DEBITO("Cartão de débito");

    private final String descricao;

    FormaPagamento(String descricao){

        this.descricao = descricao;
    }

}