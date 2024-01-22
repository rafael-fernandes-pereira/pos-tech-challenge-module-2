package com.github.rafaelfernandes.parquimetro.estacionamento.service;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.cliente.exception.CarNotFoundException;
import com.github.rafaelfernandes.parquimetro.cliente.service.CustomerService;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.aberto.ParkingOpened;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.EstacionamentoEncerrado;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.MessageEncerrado;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.encerrado.Receipt;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.ParkingOpenedEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.ParkingEndedRepository;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.ParkingOpenedReceiptEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.ParkingType;
import com.github.rafaelfernandes.parquimetro.estacionamento.exception.ParkingDuplicateException;
import com.github.rafaelfernandes.parquimetro.estacionamento.exception.ParkingMinimumDuration1HourException;
import com.github.rafaelfernandes.parquimetro.estacionamento.exception.ParkingOpenedException;
import com.github.rafaelfernandes.parquimetro.estacionamento.exception.ParkingRegisterHourTypeAndPaymentMethodPixBadRequestException;
import com.github.rafaelfernandes.parquimetro.estacionamento.repository.ParkingOpenedRepository;
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
public class ParkingService {

    @Autowired
    ParkingOpenedRepository parkingOpenedRepository;

    @Autowired
    EstacionamentoEncerradoRepository estacionamentoEncerradoRepository;

    @Autowired
    EstacionamentoEnvioReciboRepository estacionamentoEnvioReciboRepository;

    @Autowired
    CustomerService customerService;

    @Value("${estacionamento.value.fixo}")
    Double valorFixo;

    @Value("${estacionamento.value.penalty}")
    Double valorMulta;

    @Value("${estacionamento.value.hora.primeira}")
    Double valorPrimeraHora;

    @Value("${estacionamento.value.hora.restante}")
    Double valorRestanteHora;

    public ParkingOpened register(ParkingType parkingType, UUID customerId, String car, Integer duration){

        if (parkingType.equals(ParkingType.FIX) && (duration == null || duration <= 0))
            throw new ParkingMinimumDuration1HourException();

        Customer customer = this.customerService.findBydId(customerId);

        if (!customer.cars().contains(car)) throw new CarNotFoundException();

        if (parkingType.equals(ParkingType.HOUR) && customer.payment_method().equals(PaymentMethod.PIX))
            throw new ParkingRegisterHourTypeAndPaymentMethodPixBadRequestException();

        try {

            ParkingOpenedEntity parkingOpenedEntity = ParkingOpenedEntity.create(customer, car, parkingType, duration);

            ParkingOpenedEntity saved = this.parkingOpenedRepository.insert(parkingOpenedEntity);

            return ParkingOpened.fromOpenedParking(saved);

        } catch (DuplicateKeyException exception){
            throw new ParkingDuplicateException();
        }

    }

    public ParkingOpened getOpenedByCustomerIdAndCar(UUID customerId, String car){
        Optional<ParkingOpenedEntity> entity = Optional.ofNullable(this.parkingOpenedRepository.findByCustomerIdAndCar(customerId, car));

        return entity
                .map(ParkingOpened::fromOpenedParking)
                .orElseThrow(ParkingOpenedException::new);

    }

    public MessageEncerrado finish(UUID clienteId, String carro){

        ParkingOpened parkingOpened = this.getOpenedByCustomerIdAndCar(clienteId, carro);

        LocalDateTime end = LocalDateTime.now();

        BigDecimal value, penalty= new BigDecimal("0.0"), valueFinal;
        long secondsPlus = 0L;

        Duration duration = Duration.between(parkingOpened.start(), end);

        if (ParkingType.FIX.equals(parkingOpened.parking_type())){

            value = new BigDecimal(parkingOpened.duration())
                    .multiply(BigDecimal.valueOf(this.valorFixo))
                    .multiply(new BigDecimal("1.0"));


            long durationHour = parkingOpened.duration() * 3600L;

            long hourPlus = 0L;

            if (duration.getSeconds() > durationHour){

                secondsPlus = duration.getSeconds() - durationHour;

                hourPlus = secondsPlus / 3600;

                long secondsMore = secondsPlus % 3600;

                if (hourPlus < 1L) {
                    hourPlus = 1L;
                } else if (secondsMore > 0){
                    hourPlus++;
                }


            }

            penalty = new BigDecimal("1.0")
                    .multiply(new BigDecimal(hourPlus))
                    .multiply(BigDecimal.valueOf(this.valorMulta));

            valueFinal = value.add(penalty);
        } else {

            long hours = duration.toHours();

            long hourPlus =  duration.getSeconds() > (hours * 3600) ? 1L : 0L;

            long hourFinal = hours + hourPlus - 1L;

            value = new BigDecimal(hourFinal)
                    .multiply(BigDecimal.valueOf(this.valorRestanteHora))
                    .multiply(new BigDecimal("1.0"))
                    .add(BigDecimal.valueOf(this.valorPrimeraHora));

            valueFinal = value;
        }


        Receipt receipt = new Receipt(parkingOpened.start(), end, parkingOpened.duration(), value, secondsPlus, penalty, valueFinal);

        ParkingEndedRepository parkingEndedRepository = ParkingEndedRepository.from(parkingOpened, receipt);

        this.estacionamentoEncerradoRepository.insert(parkingEndedRepository);

        this.parkingOpenedRepository.deleteById(parkingOpened.id());

        ParkingOpenedReceiptEntity parkingOpenedReceiptEntity = new ParkingOpenedReceiptEntity(
                parkingEndedRepository.id(),
                parkingEndedRepository.nome(),
                parkingEndedRepository.contato().email(),
                parkingEndedRepository.contato().celphone(),
                receipt
        );

        this.estacionamentoEnvioReciboRepository.insert(parkingOpenedReceiptEntity);

        EstacionamentoEncerrado estacionamentoEncerrado = EstacionamentoEncerrado.fromEstacionamentoEncerradoEntity(parkingEndedRepository);

        return MessageEncerrado.success(HttpStatus.OK, estacionamentoEncerrado);
    }

    public MessageEncerrado obterEncerrado(UUID estacionamentoEncerradoId){
        Optional<ParkingEndedRepository> entity = this.estacionamentoEncerradoRepository.findById(estacionamentoEncerradoId);

        if (entity.isPresent()){
            EstacionamentoEncerrado estacionamentoAberto = EstacionamentoEncerrado.fromEstacionamentoEncerradoEntity(entity.get());
            return MessageEncerrado.success(HttpStatus.OK, estacionamentoAberto);
        }

        return MessageEncerrado.error(HttpStatus.NOT_FOUND, "Registro não encontrado");

    }

}
