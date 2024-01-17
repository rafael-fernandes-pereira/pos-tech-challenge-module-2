package com.github.rafaelfernandes.parquimetro.estacionamento.controller;

import com.github.rafaelfernandes.parquimetro.estacionamento.controller.request.Fixo;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.MessageAberto;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.MessageEncerrado;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import com.github.rafaelfernandes.parquimetro.estacionamento.service.EstacionamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/estacionamento")
public class EstacionamentoController {

    @Autowired private EstacionamentoService estacionamentoService;

    @GetMapping("/{requestId}/{carro}/aberto")
    ResponseEntity<MessageAberto> obterDadosEmAberto(@PathVariable UUID requestId, @PathVariable String carro){

        MessageAberto estacionamento = this.estacionamentoService.obterAbertoPorCarro(requestId, carro);

        return ResponseEntity
                .status(estacionamento.http_status_code())
                .body(estacionamento);

    }

    @PostMapping("/{requestId}/{carro}/fixo")
    ResponseEntity<MessageAberto> registrarFixo(@PathVariable UUID requestId,
                                                @PathVariable String carro,
                                                @RequestBody Fixo fixo,
                                                UriComponentsBuilder uriComponentsBuilder){

        MessageAberto messageAberto = this.estacionamentoService.registrar(TipoPeriodo.FIXO, requestId, carro, fixo.duracao_fixa());

        URI location = uriComponentsBuilder
                .path("estacionamento/{requestId}/{carro}/aberto")
                .buildAndExpand(requestId, carro)
                .toUri();

        return ResponseEntity
                .status(messageAberto.http_status_code())
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .body(messageAberto);
    }

    @PostMapping("/{requestId}/{carro}/hora")
    ResponseEntity<MessageAberto> registrarHora(@PathVariable UUID requestId, @PathVariable String carro, UriComponentsBuilder uriComponentsBuilder){

        MessageAberto messageAberto = this.estacionamentoService.registrar(TipoPeriodo.HORA, requestId, carro, null);

        URI location = uriComponentsBuilder
                .path("estacionamento/{requestId}/{carro}/aberto")
                .buildAndExpand(requestId, carro)
                .toUri();

        return ResponseEntity
                .status(messageAberto.http_status_code())
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .body(messageAberto);
    }

    @PostMapping("/{requestId}/{carro}/finalizar")
    ResponseEntity<MessageEncerrado> finalizar(@PathVariable UUID requestId, @PathVariable String carro, UriComponentsBuilder uriComponentsBuilder){

        MessageEncerrado messageEncerrado = this.estacionamentoService.finalizar(requestId, carro);

        UUID encerradoId = messageEncerrado.estacionamentos().get(0).id();

        URI location = uriComponentsBuilder
                .path("estacionamento/{requestId}/encerrado")
                .buildAndExpand(encerradoId)
                .toUri();

        return ResponseEntity
                .status(messageEncerrado.http_status_code())
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .body(messageEncerrado);


    }

    @GetMapping("/{requestId}/encerrado")
    ResponseEntity<MessageEncerrado> obterDadosEncerrado(@PathVariable UUID requestId){

        MessageEncerrado estacionamento = this.estacionamentoService.obterEncerrado(requestId);

        return ResponseEntity
                .status(estacionamento.http_status_code())
                .body(estacionamento);

    }





}
