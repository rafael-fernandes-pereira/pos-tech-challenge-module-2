package com.github.rafaelfernandes.parquimetro.parking.service;

import com.github.rafaelfernandes.parquimetro.customer.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.customer.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.customer.exception.CarNotFoundException;
import com.github.rafaelfernandes.parquimetro.customer.service.CustomerService;
import com.github.rafaelfernandes.parquimetro.parking.controller.response.aberto.ParkingOpened;
import com.github.rafaelfernandes.parquimetro.parking.controller.response.encerrado.ParkingFinished;
import com.github.rafaelfernandes.parquimetro.parking.controller.response.encerrado.Receipt;
import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingOpenedEntity;
import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingFinishedEntity;
import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingOpenedReceiptEntity;
import com.github.rafaelfernandes.parquimetro.parking.enums.ParkingType;
import com.github.rafaelfernandes.parquimetro.parking.exception.*;
import com.github.rafaelfernandes.parquimetro.parking.repository.ParkingOpenedRepository;
import com.github.rafaelfernandes.parquimetro.parking.repository.ParkingFinishedRepository;
import com.github.rafaelfernandes.parquimetro.parking.repository.ParkingSendReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
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
    ParkingFinishedRepository parkingFinishedRepository;

    @Autowired
    ParkingSendReceiptRepository parkingSendReceiptRepository;

    @Autowired
    CustomerService customerService;

    @Value("${parking.fix}")
    Double fixValue;

    @Value("${parking.penalty}")
    Double penaltyValue;

    @Value("${parking.hour.first}")
    Double FirstHourValue;

    @Value("${parking.hour.rest}")
    Double RestHourValue;

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

    public ParkingFinished finish(UUID clienteId, String carro){

        ParkingOpened parkingOpened = this.getOpenedByCustomerIdAndCar(clienteId, carro);

        LocalDateTime end = LocalDateTime.now();

        BigDecimal value, penalty= new BigDecimal("0.0"), valueFinal;
        long secondsPlus = 0L;

        Duration duration = Duration.between(parkingOpened.start(), end);

        if (ParkingType.FIX.equals(parkingOpened.parking_type())){

            value = new BigDecimal(parkingOpened.duration())
                    .multiply(BigDecimal.valueOf(this.fixValue))
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
                    .multiply(BigDecimal.valueOf(this.penaltyValue));

            valueFinal = value.add(penalty);
        } else {

            long hours = duration.toHours();

            long hourPlus =  duration.getSeconds() > (hours * 3600) ? 1L : 0L;

            long hourFinal = hours + hourPlus - 1L;

            value = new BigDecimal(hourFinal)
                    .multiply(BigDecimal.valueOf(this.RestHourValue))
                    .multiply(new BigDecimal("1.0"))
                    .add(BigDecimal.valueOf(this.FirstHourValue));

            valueFinal = value;
        }


        Receipt receipt = new Receipt(parkingOpened.start(), end, parkingOpened.duration(), value, secondsPlus, penalty, valueFinal);

        ParkingFinishedEntity parkingFinishedEntity = ParkingFinishedEntity.from(parkingOpened, receipt);

        this.parkingFinishedRepository.insert(parkingFinishedEntity);

        this.parkingOpenedRepository.deleteById(parkingOpened.id());

        ParkingOpenedReceiptEntity parkingOpenedReceiptEntity = new ParkingOpenedReceiptEntity(
                parkingFinishedEntity.id(),
                parkingFinishedEntity.name(),
                parkingFinishedEntity.contact().email(),
                parkingFinishedEntity.contact().celphone(),
                receipt
        );

        this.parkingSendReceiptRepository.insert(parkingOpenedReceiptEntity);

        return ParkingFinished.fromEstacionamentoEncerradoEntity(parkingFinishedEntity);

    }

    public ParkingFinished getFinished(UUID parkingFinishedId){
        Optional<ParkingFinishedEntity> entity = this.parkingFinishedRepository.findById(parkingFinishedId);

        return entity
                .map(ParkingFinished::fromEstacionamentoEncerradoEntity)
                .orElseThrow(ParkingOpenNotFound::new)
                ;

    }

}
