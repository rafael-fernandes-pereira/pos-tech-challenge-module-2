package com.github.rafaelfernandes.parquimetro.estacionamento.service;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Cliente;
import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageCliente;
import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.cliente.service.ClienteService;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.Estacionamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.MessageEstacionamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.MessageFinalizado;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.Recibo;
import com.github.rafaelfernandes.parquimetro.estacionamento.dto.MessageEstacionamentoDTO;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoAbertoEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoEncerradoEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import com.github.rafaelfernandes.parquimetro.estacionamento.repository.EstacionamentoAbertoRepository;
import com.github.rafaelfernandes.parquimetro.estacionamento.repository.EstacionamentoEncerradoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EstacionamentoService {

    @Autowired
    EstacionamentoAbertoRepository estacionamentoAbertoRepository;

    @Autowired
    EstacionamentoEncerradoRepository estacionamentoEncerradoRepository;

    @Autowired
    ClienteService clienteService;

    @Value("${estacionamento.valor.fixo}")
    Integer valorFixo;

    @Value("${estacionamento.valor.multa}")
    Integer valorMulta;

    public MessageEstacionamento registrar(TipoPeriodo tipoPeriodo, UUID clienteId, String carro, Integer duracao){

        if (tipoPeriodo.equals(TipoPeriodo.FIXO) && (duracao == null || duracao <= 0))
            return MessageEstacionamentoDTO.error(HttpStatus.BAD_REQUEST, "Tempo mínimo de 1 hora");

        MessageCliente messageCliente = this.clienteService.obterPorId(clienteId);

        if (!messageCliente.errors().isEmpty())
            return MessageEstacionamentoDTO.error(HttpStatus.valueOf(messageCliente.httpStatusCode()), messageCliente.errors());

        Cliente cliente = messageCliente.clientes().get(0);

        if (!cliente.carros().contains(carro))
            return MessageEstacionamentoDTO.error(HttpStatus.NOT_FOUND, "Carro não cadastrado para esse cliente");

        if (tipoPeriodo.equals(TipoPeriodo.HORA) && cliente.forma_pagamento().equals(FormaPagamento.PIX))
            return MessageEstacionamentoDTO.error(HttpStatus.BAD_REQUEST, "Forma de pagamento não permitido para o tipo de periodo escolhido!");

        try {

            EstacionamentoAbertoEntity estacionamentoAberto = EstacionamentoAbertoEntity.novo(cliente, carro, tipoPeriodo, duracao);

            EstacionamentoAbertoEntity salvo = this.estacionamentoAbertoRepository.insert(estacionamentoAberto);

            Estacionamento estacionamento = Estacionamento.fromEstacionamentoAberto(salvo);

            return MessageEstacionamentoDTO.success(HttpStatus.CREATED, estacionamento);

        } catch (DuplicateKeyException exception){
            return MessageEstacionamentoDTO.error(HttpStatus.CONFLICT, "Carro já está com tempo lançado!");
        }

    }

    public MessageEstacionamento obterAbertoPorCarro(UUID clienteId, String carro){
        Optional<EstacionamentoAbertoEntity> entity = Optional.ofNullable(this.estacionamentoAbertoRepository.findByClienteIdAndCarro(clienteId, carro));

        if (entity.isPresent()){
            Estacionamento estacionamento = Estacionamento.fromEstacionamentoAberto(entity.get());
            return MessageEstacionamentoDTO.success(HttpStatus.OK, estacionamento);
        }

        return MessageEstacionamentoDTO.error(HttpStatus.NOT_FOUND, "Registro não encontrado");

    }

    public MessageFinalizado finalizar(UUID clienteId, String carro){

        MessageCliente messageCliente = this.clienteService.obterPorId(clienteId);

        if (!messageCliente.errors().isEmpty())
            return MessageFinalizado.error(HttpStatus.valueOf(messageCliente.httpStatusCode()), messageCliente.errors());

        Cliente cliente = messageCliente.clientes().get(0);

        if (!cliente.carros().contains(carro))
            return MessageFinalizado.error(HttpStatus.NOT_FOUND, "Carro não cadastrado para esse cliente");

        MessageEstacionamento messageEstacionamento = this.obterAbertoPorCarro(clienteId, carro);

        if (messageEstacionamento.estacionamentos().isEmpty())
            return MessageFinalizado.error(HttpStatus.NOT_FOUND, "Registro de estacionamento não encontrado");

        Estacionamento estacionamento = messageEstacionamento.estacionamentos().get(0);

        LocalDateTime fim = LocalDateTime.now();

        BigDecimal valor = new BigDecimal(estacionamento.duracao_fixa())
                .multiply(new BigDecimal(this.valorFixo))
                .multiply(new BigDecimal("1.0"));

        Duration duration = Duration.between(estacionamento.inicio(), fim);

        Long tempoPedido = estacionamento.duracao_fixa() * 3600L;


        Long horasAMais = 0L;

        Long segundosAMais = 0L;

        if (duration.getSeconds() > tempoPedido){

            segundosAMais = duration.getSeconds() - tempoPedido;

            horasAMais = segundosAMais / 3600;

            Long segundosSobrando = segundosAMais % 3600;

            if (horasAMais < 1L) {
                horasAMais = 1L;
            } else if (segundosSobrando > 0){
                horasAMais++;
            }


        }

        BigDecimal multa = new BigDecimal("1.0")
                .multiply(new BigDecimal(horasAMais))
                .multiply(new BigDecimal(this.valorMulta));

        BigDecimal valorFinal = valor.add(multa);

        Recibo recibo = new Recibo(estacionamento.inicio(), fim, estacionamento.duracao_fixa(), valor, segundosAMais, multa, valorFinal);

        EstacionamentoEncerradoEntity estacionamentoEncerradoEntity = EstacionamentoEncerradoEntity.from(estacionamento, recibo);

        this.estacionamentoEncerradoRepository.insert(estacionamentoEncerradoEntity);

        this.estacionamentoAbertoRepository.deleteById(estacionamento.id());

        return new MessageFinalizado(recibo, HttpStatus.OK.value(), null);
    }

}
