package com.github.rafaelfernandes.parquimetro.estacionamento.service;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.cliente.service.CustomerService;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.ParkingOpened;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.MessageAberto;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.EstacionamentoEncerrado;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.MessageEncerrado;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.Recibo;
import com.github.rafaelfernandes.parquimetro.estacionamento.dto.MessageEstacionamentoDTO;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.ParkingOpenedEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoEncerradoEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoEnvioRecibo;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.ParkingType;
import com.github.rafaelfernandes.parquimetro.estacionamento.exception.ParkingOpenedException;
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
    CustomerService customerService;

    @Value("${estacionamento.valor.fixo}")
    Double valorFixo;

    @Value("${estacionamento.valor.multa}")
    Double valorMulta;

    @Value("${estacionamento.valor.hora.primeira}")
    Double valorPrimeraHora;

    @Value("${estacionamento.valor.hora.restante}")
    Double valorRestanteHora;

    public MessageAberto registrar(ParkingType parkingType, UUID clienteId, String carro, Integer duracao){

        if (parkingType.equals(ParkingType.FIXO) && (duracao == null || duracao <= 0))
            return MessageEstacionamentoDTO.error(HttpStatus.BAD_REQUEST, "Tempo mínimo de 1 hora");

        Customer customer = this.customerService.findBydId(clienteId);

        if (!customer.cars().contains(carro))
            return MessageEstacionamentoDTO.error(HttpStatus.NOT_FOUND, "Carro não cadastrado para esse cliente");

        if (parkingType.equals(ParkingType.HORA) && customer.payment_method().equals(PaymentMethod.PIX))
            return MessageEstacionamentoDTO.error(HttpStatus.BAD_REQUEST, "Forma de pagamento não permitido para o tipo de periodo escolhido!");

        try {

            ParkingOpenedEntity estacionamentoAberto = ParkingOpenedEntity.novo(customer, carro, parkingType, duracao);

            ParkingOpenedEntity salvo = this.estacionamentoAbertoRepository.insert(estacionamentoAberto);

            ParkingOpened estacionamento = ParkingOpened.fromEstacionamentoAberto(salvo);

            return MessageEstacionamentoDTO.success(HttpStatus.CREATED, estacionamento);

        } catch (DuplicateKeyException exception){
            return MessageEstacionamentoDTO.error(HttpStatus.CONFLICT, "Carro já está com tempo lançado!");
        }

    }

    public ParkingOpened getOpenedByCustomerIdAndCar(UUID customerId, String car){
        Optional<ParkingOpenedEntity> entity = Optional.ofNullable(this.estacionamentoAbertoRepository.findByClienteIdAndCarro(customerId, car));

        return entity
                .map(ParkingOpened::fromEstacionamentoAberto)
                .orElseThrow(ParkingOpenedException::new);

    }

    public MessageEncerrado finalizar(UUID clienteId, String carro){

        ParkingOpened parkingOpened = this.getOpenedByCustomerIdAndCar(clienteId, carro);

        LocalDateTime fim = LocalDateTime.now();

        BigDecimal valor, multa= new BigDecimal("0.0"), valorFinal;
        Long segundosAMais = 0L;

        Duration duration = Duration.between(parkingOpened.start(), fim);

        if (ParkingType.FIXO.equals(parkingOpened.parking_type())){

            valor = new BigDecimal(parkingOpened.duration())
                    .multiply(new BigDecimal(this.valorFixo))
                    .multiply(new BigDecimal("1.0"));


            Long tempoPedido = parkingOpened.duration() * 3600L;

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


        Recibo recibo = new Recibo(parkingOpened.start(), fim, parkingOpened.duration(), valor, segundosAMais, multa, valorFinal);

        EstacionamentoEncerradoEntity estacionamentoEncerradoEntity = EstacionamentoEncerradoEntity.from(parkingOpened, recibo);

        this.estacionamentoEncerradoRepository.insert(estacionamentoEncerradoEntity);

        this.estacionamentoAbertoRepository.deleteById(parkingOpened.id());

        EstacionamentoEnvioRecibo envioRecibo = new EstacionamentoEnvioRecibo(
                estacionamentoEncerradoEntity.id(),
                estacionamentoEncerradoEntity.nome(),
                estacionamentoEncerradoEntity.contato().email(),
                estacionamentoEncerradoEntity.contato().celphone(),
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
