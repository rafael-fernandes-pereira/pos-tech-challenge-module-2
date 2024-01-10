package com.github.rafaelfernandes.parquimetro.dto;

import com.github.rafaelfernandes.parquimetro.controller.Cliente;
import com.github.rafaelfernandes.parquimetro.dados.GerarCadastro;
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



    }

}
