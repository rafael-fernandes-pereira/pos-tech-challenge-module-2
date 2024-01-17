package com.github.rafaelfernandes.parquimetro.estacionamento.service;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Cliente;
import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageCliente;
import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.cliente.service.ClienteService;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.EstacionamentoAberto;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.MessageAberto;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.EstacionamentoEncerrado;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.MessageEncerrado;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.Recibo;
import com.github.rafaelfernandes.parquimetro.estacionamento.dto.MessageEstacionamentoDTO;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoAbertoEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoEncerradoEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoEnvioRecibo;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import com.github.rafaelfernandes.parquimetro.estacionamento.repository.EstacionamentoAbertoRepository;
import com.github.rafaelfernandes.parquimetro.estacionamento.repository.EstacionamentoEncerradoRepository;
import com.github.rafaelfernandes.parquimetro.estacionamento.repository.EstacionamentoEnvioReciboRepository;
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
    EstacionamentoEnvioReciboRepository estacionamentoEnvioReciboRepository;

    @Autowired
    ClienteService clienteService;

    @Value("${estacionamento.valor.fixo}")
    Double valorFixo;

    @Value("${estacionamento.valor.multa}")
    Double valorMulta;

    @Value("${estacionamento.valor.hora.primeira}")
    Double valorPrimeraHora;

    @Value("${estacionamento.valor.hora.restante}")
    Double valorRestanteHora;

    public MessageAberto registrar(TipoPeriodo tipoPeriodo, UUID clienteId, String carro, Integer duracao){

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

            EstacionamentoAberto estacionamento = EstacionamentoAberto.fromEstacionamentoAberto(salvo);

            return MessageEstacionamentoDTO.success(HttpStatus.CREATED, estacionamento);

        } catch (DuplicateKeyException exception){
            return MessageEstacionamentoDTO.error(HttpStatus.CONFLICT, "Carro já está com tempo lançado!");
        }

    }

    public MessageAberto obterAbertoPorCarro(UUID clienteId, String carro){
        Optional<EstacionamentoAbertoEntity> entity = Optional.ofNullable(this.estacionamentoAbertoRepository.findByClienteIdAndCarro(clienteId, carro));

        if (entity.isPresent()){
            EstacionamentoAberto estacionamentoAberto = EstacionamentoAberto.fromEstacionamentoAberto(entity.get());
            return MessageEstacionamentoDTO.success(HttpStatus.OK, estacionamentoAberto);
        }

        return MessageEstacionamentoDTO.error(HttpStatus.NOT_FOUND, "Registro não encontrado");

    }

    public MessageEncerrado finalizar(UUID clienteId, String carro){

        MessageCliente messageCliente = this.clienteService.obterPorId(clienteId);

        if (!messageCliente.errors().isEmpty())
            return MessageEncerrado.error(HttpStatus.valueOf(messageCliente.httpStatusCode()), messageCliente.errors());

        Cliente cliente = messageCliente.clientes().get(0);

        if (!cliente.carros().contains(carro))
            return MessageEncerrado.error(HttpStatus.NOT_FOUND, "Carro não cadastrado para esse cliente");

        MessageAberto messageAberto = this.obterAbertoPorCarro(clienteId, carro);

        if (messageAberto.estacionamentos() == null || messageAberto.estacionamentos().isEmpty())
            return MessageEncerrado.error(HttpStatus.NOT_FOUND, "Registro de estacionamento não encontrado");

        EstacionamentoAberto estacionamentoAberto = messageAberto.estacionamentos().get(0);

        LocalDateTime fim = LocalDateTime.now();

        BigDecimal valor, multa= new BigDecimal("0.0"), valorFinal;
        Long segundosAMais = 0L;

        Duration duration = Duration.between(estacionamentoAberto.inicio(), fim);

        if (TipoPeriodo.FIXO.equals(estacionamentoAberto.tipo_periodo())){

            valor = new BigDecimal(estacionamentoAberto.duracao_fixa())
                    .multiply(new BigDecimal(this.valorFixo))
                    .multiply(new BigDecimal("1.0"));


            Long tempoPedido = estacionamentoAberto.duracao_fixa() * 3600L;

            Long horasAMais = 0L;

            segundosAMais = 0L;

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

            multa = new BigDecimal("1.0")
                    .multiply(new BigDecimal(horasAMais))
                    .multiply(new BigDecimal(this.valorMulta));

            valorFinal = valor.add(multa);
        } else {

            Long horas = duration.toHours();

            Long horaAMais =  duration.getSeconds() > (horas * 3600) ? 1L : 0L;

            Long horasFinal = horas + horaAMais - 1L;

            valor = new BigDecimal(horasFinal)
                    .multiply(new BigDecimal(this.valorRestanteHora))
                    .multiply(new BigDecimal("1.0"))
                    .add(new BigDecimal(this.valorPrimeraHora));

            valorFinal = valor;
        }


        Recibo recibo = new Recibo(estacionamentoAberto.inicio(), fim, estacionamentoAberto.duracao_fixa(), valor, segundosAMais, multa, valorFinal);

        EstacionamentoEncerradoEntity estacionamentoEncerradoEntity = EstacionamentoEncerradoEntity.from(estacionamentoAberto, recibo);

        this.estacionamentoEncerradoRepository.insert(estacionamentoEncerradoEntity);

        this.estacionamentoAbertoRepository.deleteById(estacionamentoAberto.id());

        EstacionamentoEnvioRecibo envioRecibo = new EstacionamentoEnvioRecibo(
                estacionamentoEncerradoEntity.id(),
                estacionamentoEncerradoEntity.nome(),
                estacionamentoEncerradoEntity.contato().email(),
                estacionamentoEncerradoEntity.contato().telefone(),
                recibo
        );

        this.estacionamentoEnvioReciboRepository.insert(envioRecibo);

        EstacionamentoEncerrado estacionamentoEncerrado = EstacionamentoEncerrado.fromEstacionamentoEncerradoEntity(estacionamentoEncerradoEntity);

        return MessageEncerrado.success(HttpStatus.OK, estacionamentoEncerrado);
    }

    public MessageEncerrado obterEncerrado(UUID estacionamentoEncerradoId){
        Optional<EstacionamentoEncerradoEntity> entity = this.estacionamentoEncerradoRepository.findById(estacionamentoEncerradoId);

        if (entity.isPresent()){
            EstacionamentoEncerrado estacionamentoAberto = EstacionamentoEncerrado.fromEstacionamentoEncerradoEntity(entity.get());
            return MessageEncerrado.success(HttpStatus.OK, estacionamentoAberto);
        }

        return MessageEncerrado.error(HttpStatus.NOT_FOUND, "Registro não encontrado");

    }

}
