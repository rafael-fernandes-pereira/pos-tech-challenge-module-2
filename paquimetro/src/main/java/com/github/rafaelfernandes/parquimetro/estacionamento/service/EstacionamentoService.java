package com.github.rafaelfernandes.parquimetro.estacionamento.service;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Cliente;
import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageCliente;
import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.cliente.service.ClienteService;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.Estacionamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.MessageEstacionamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.dto.MessageEstacionamentoDTO;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoAbertoEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import com.github.rafaelfernandes.parquimetro.estacionamento.repository.EstacionamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class EstacionamentoService {

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private ClienteService clienteService;

    public MessageEstacionamento registrar(TipoPeriodo tipoPeriodo, UUID clienteId, String carro, Integer duracao){

        if (tipoPeriodo.equals(TipoPeriodo.FIXO) && (duracao == null || duracao <= 0))
            return MessageEstacionamentoDTO.error(HttpStatus.BAD_REQUEST, "Tempo mínimo de 1 hora");

        MessageCliente messageCliente = this.clienteService.obterPorId(clienteId);

        if (messageCliente.clientes().isEmpty())
            return MessageEstacionamentoDTO.error(HttpStatus.NOT_FOUND, "Cliente não existe");

        Cliente cliente = messageCliente.clientes().get(0);

        if (!cliente.carros().contains(carro))
            return MessageEstacionamentoDTO.error(HttpStatus.NOT_FOUND, "Carro não cadastrado para esse cliente");

        if (tipoPeriodo.equals(TipoPeriodo.HORA) && cliente.forma_pagamento().equals(FormaPagamento.PIX))
            return MessageEstacionamentoDTO.error(HttpStatus.BAD_REQUEST, "Forma de pagamento não permitido para o tipo de periodo escolhido!");

        try {

            EstacionamentoAbertoEntity estacionamentoAberto = EstacionamentoAbertoEntity.novo(cliente, carro, tipoPeriodo, duracao);

            EstacionamentoAbertoEntity salvo = this.estacionamentoRepository.insert(estacionamentoAberto);

            Estacionamento estacionamento = Estacionamento.fromEstacionamentoAberto(salvo);

            return MessageEstacionamentoDTO.success(HttpStatus.CREATED, estacionamento);

        } catch (DuplicateKeyException exception){
            return MessageEstacionamentoDTO.error(HttpStatus.CONFLICT, "Carro já está com tempo lançado!");
        }

    }

    public MessageEstacionamento obterAbertoPorCarro(UUID clienteId, String carro){
        Optional<EstacionamentoAbertoEntity> entity = Optional.ofNullable(this.estacionamentoRepository.findByClienteIdAndCarro(clienteId, carro));

        if (entity.isPresent()){
            Estacionamento estacionamento = Estacionamento.fromEstacionamentoAberto(entity.get());
            return MessageEstacionamentoDTO.success(HttpStatus.OK, estacionamento);
        }

        return MessageEstacionamentoDTO.error(HttpStatus.NOT_FOUND, "Registro não encontrado");

    }

}
