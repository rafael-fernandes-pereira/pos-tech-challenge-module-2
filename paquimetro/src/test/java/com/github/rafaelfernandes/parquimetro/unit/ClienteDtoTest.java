package com.github.rafaelfernandes.parquimetro.unit;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.dados.GerarCadastro;
import com.github.rafaelfernandes.parquimetro.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.entity.ClienteEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClienteDtoTest {

    @Test
    void retornaClienteNovoEntityComSucesso(){

        Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);

        ClienteEntity entity = ClienteDto.from(cliente, Boolean.TRUE);

        assertThat(cliente.nome()).isEqualTo(entity.nome());
        assertThat(cliente.documento()).isEqualTo(entity.documento());

        assertThat(cliente.endereco().logradouro()).isEqualTo(entity.endereco().logradouro());
        assertThat(cliente.endereco().numero()).isEqualTo(entity.endereco().numero());
        assertThat(cliente.endereco().complemento()).isEqualTo(entity.endereco().complemento());
        assertThat(cliente.endereco().bairro()).isEqualTo(entity.endereco().bairro());
        assertThat(cliente.endereco().cidade()).isEqualTo(entity.endereco().cidade());
        assertThat(cliente.endereco().estado()).isEqualTo(entity.endereco().estado());

        assertThat(cliente.forma_pagamento()).isEqualTo(entity.forma_pagamento());





    }

}
