package com.github.rafaelfernandes.parquimetro.parking.service;

import com.github.rafaelfernandes.parquimetro.customer.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.customer.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.customer.exception.CarNotFoundException;
import com.github.rafaelfernandes.parquimetro.customer.service.CustomerService;
import com.github.rafaelfernandes.parquimetro.parking.controller.response.open.ParkingOpened;
import com.github.rafaelfernandes.parquimetro.parking.controller.response.close.ParkingFinished;
import com.github.rafaelfernandes.parquimetro.parking.controller.response.close.Receipt;
import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingOpenedEntity;
import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingFinishedEntity;
import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingSendReceiptEntity;
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
    Double firstHourValue;

    @Value("${parking.hour.rest}")
    Double restHourValue;

    public void register(ParkingType parkingType, UUID customerId, String car, Long duration){

        if (parkingType.equals(ParkingType.FIX) && (duration == null || duration <= 0))
            throw new ParkingMinimumDuration1HourException();

        Customer customer = this.customerService.findBydId(customerId);

        if (!customer.cars().contains(car)) throw new CarNotFoundException();

        if (parkingType.equals(ParkingType.HOUR) && customer.payment_method().equals(PaymentMethod.PIX))
            throw new ParkingRegisterHourTypeAndPaymentMethodPixBadRequestException();

        try {

            ParkingOpenedEntity parkingOpenedEntity = ParkingOpenedEntity.create(customer, car, parkingType, duration);

            ParkingOpenedEntity saved = this.parkingOpenedRepository.insert(parkingOpenedEntity);

            ParkingOpened.fromOpenedParking(saved);

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
        Duration expectedDuration = Duration.between(parkingOpened.start(), parkingOpened.expected_end_time());

        if (ParkingType.FIX.equals(parkingOpened.parking_type())){

            value = new BigDecimal(expectedDuration.toHours())
                    .multiply(BigDecimal.valueOf(this.fixValue))
                    .multiply(new BigDecimal("1.0"));


            long hourPlus = 0L;

            if (duration.getSeconds() > expectedDuration.getSeconds()){

                secondsPlus = duration.getSeconds() - expectedDuration.getSeconds();

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
                    .multiply(BigDecimal.valueOf(this.restHourValue))
                    .multiply(new BigDecimal("1.0"))
                    .add(BigDecimal.valueOf(this.firstHourValue));

            valueFinal = value;
        }


        Receipt receipt = new Receipt(parkingOpened.start(), end, value, penalty, valueFinal);

        ParkingFinishedEntity parkingFinishedEntity = ParkingFinishedEntity.from(parkingOpened, receipt);

        this.parkingFinishedRepository.insert(parkingFinishedEntity);

        this.parkingOpenedRepository.deleteById(parkingOpened.id());

        ParkingSendReceiptEntity parkingSendReceiptEntity = new ParkingSendReceiptEntity(
                parkingFinishedEntity.id(),
                parkingFinishedEntity.name(),
                parkingFinishedEntity.contact().email(),
                parkingFinishedEntity.contact().celphone(),
                receipt
        );

        this.parkingSendReceiptRepository.insert(parkingSendReceiptEntity);

        return ParkingFinished.fromEstacionamentoEncerradoEntity(parkingFinishedEntity);

    }

    public ParkingFinished getFinished(UUID parkingFinishedId){
        Optional<ParkingFinishedEntity> entity = this.parkingFinishedRepository.findById(parkingFinishedId);

        return entity
                .map(ParkingFinished::fromEstacionamentoEncerradoEntity)
                .orElseThrow(ParkingOpenNotFound::new)
                ;

    }

    public void updateStartEnd(UUID customerId, String car, Long starMinusHours, Long expectedHoursPlus){


        ParkingOpened parkingOpened = this.getOpenedByCustomerIdAndCar(customerId, car);



        ParkingOpenedEntity updateParking = ParkingOpenedEntity.updateStartEnd(parkingOpened, starMinusHours, expectedHoursPlus);

        this.parkingOpenedRepository.save(updateParking);

    }

}
