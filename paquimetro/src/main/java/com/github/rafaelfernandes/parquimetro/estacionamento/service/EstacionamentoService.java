package com.github.rafaelfernandes.parquimetro.estacionamento.service;

import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageCliente;
import com.github.rafaelfernandes.parquimetro.cliente.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.cliente.service.ClienteService;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.MessageEstacionamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import com.github.rafaelfernandes.parquimetro.estacionamento.repository.EstacionamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EstacionamentoService {

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private ClienteService clienteService;

    public MessageEstacionamento registrar(TipoPeriodo tipoPeriodo, UUID clienteId, String carro, Integer duracao){

        MessageCliente cliente = this.clienteService.obterPorId(clienteId);

        if (cliente.clientes().isEmpty()){

        }

    }

}
