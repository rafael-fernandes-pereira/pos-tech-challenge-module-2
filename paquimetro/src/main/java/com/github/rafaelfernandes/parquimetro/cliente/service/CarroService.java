package com.github.rafaelfernandes.parquimetro.cliente.service;

import com.github.rafaelfernandes.parquimetro.cliente.dto.MessageDTO;
import com.github.rafaelfernandes.parquimetro.cliente.controller.response.MessageCarros;
import com.github.rafaelfernandes.parquimetro.cliente.entity.CustomerEntity;
import com.github.rafaelfernandes.parquimetro.cliente.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CarroService {

    @Autowired private ClienteRepository repository;

    public MessageCarros incluir(UUID requestId, List<String> carros){

        List<String> erros = new ArrayList<>();

        if (carros == null || carros.isEmpty() ){
            return MessageDTO.carrosError(HttpStatus.BAD_REQUEST, erros);
        }

        Optional<CustomerEntity> cliente = this.repository.findById(requestId);

        if (cliente.isEmpty()) {
            return MessageDTO.carrosError(HttpStatus.NOT_FOUND, erros);
        }

        List<String> carrosSalvos = cliente.get().cars();

        Set<String> novosCarros = new TreeSet<>();

        novosCarros.addAll(carrosSalvos.stream().toList());
        novosCarros.addAll(carros.stream().toList());

        CustomerEntity customerEntity = new CustomerEntity(
                requestId,
                cliente.get().name(),
                cliente.get().document(),
                cliente.get().address(),
                cliente.get().payment_method(),
                cliente.get().contact(),
                novosCarros.stream().toList()
        );

        this.repository.save(customerEntity);

        return MessageDTO.carrosSuccess(HttpStatus.NO_CONTENT, null);

    }

    public MessageCarros obter(UUID requestId){

        Optional<CustomerEntity> cliente = this.repository.findById(requestId);

        return cliente
                .map(clienteEntity -> MessageDTO.carrosSuccess(HttpStatus.OK, clienteEntity.cars()))
                .orElseGet(() -> MessageDTO.carrosError(HttpStatus.NOT_FOUND, null));


    }

    public MessageCarros deletar(UUID requestId, String carro) {

        Optional<CustomerEntity> cliente = this.repository.findById(requestId);

        if (cliente.isEmpty()) {
            return MessageDTO.carrosError(HttpStatus.NOT_FOUND, null);
        }

        List<String> carrosSalvos = cliente.get().cars();

        if (!carrosSalvos.contains(carro)){
            return MessageDTO.carrosError(HttpStatus.NOT_FOUND, null);
        }

        List<String> novosCarros = new ArrayList<>(carrosSalvos);
        novosCarros.remove(carro);

        CustomerEntity customerEntity = new CustomerEntity(
                requestId,
                cliente.get().name(),
                cliente.get().document(),
                cliente.get().address(),
                cliente.get().payment_method(),
                cliente.get().contact(),
                novosCarros.stream().toList()
        );

        this.repository.save(customerEntity);

        return MessageDTO.carrosSuccess(HttpStatus.NO_CONTENT, null);
    }
}
