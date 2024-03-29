package com.github.rafaelfernandes.parquimetro.parking.controller;

import com.github.rafaelfernandes.parquimetro.customer.controller.response.CustomerError;
import com.github.rafaelfernandes.parquimetro.customer.exception.*;
import com.github.rafaelfernandes.parquimetro.parking.exception.ParkingDuplicateException;
import com.github.rafaelfernandes.parquimetro.parking.exception.ParkingMinimumDuration1HourException;
import com.github.rafaelfernandes.parquimetro.parking.exception.ParkingOpenedException;
import com.github.rafaelfernandes.parquimetro.parking.exception.ParkingRegisterHourTypeAndPaymentMethodPixBadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "com.github.rafaelfernandes.parquimetro.parking.controller")
public class ParkingExceptionHandler {

    @ExceptionHandler({ParkingOpenedException.class})
    public ResponseEntity<CustomerError> parkingOpenedNotFound(ParkingOpenedException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new CustomerError(exception.getErrors()));
    }

    @ExceptionHandler({ParkingMinimumDuration1HourException.class})
    public ResponseEntity<CustomerError> parkingMinimumDuration1Hour(ParkingMinimumDuration1HourException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CustomerError(exception.getErrors()));
    }

    @ExceptionHandler({CustomerNotFoundException.class})
    public ResponseEntity<CustomerError> customerNotFound(CustomerNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new CustomerError(exception.getErrors()));
    }

    @ExceptionHandler({ParkingDuplicateException.class})
    public ResponseEntity<CustomerError> parkingDuplicate(ParkingDuplicateException exception){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new CustomerError(exception.getErrors()));
    }

    @ExceptionHandler({ParkingRegisterHourTypeAndPaymentMethodPixBadRequestException.class})
    public ResponseEntity<CustomerError> parkingRegisterHourTypeAndPaymentMethodPix(ParkingRegisterHourTypeAndPaymentMethodPixBadRequestException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CustomerError(exception.getErrors()));
    }

    @ExceptionHandler({CarNotFoundException.class})
    public ResponseEntity<CustomerError> customerCarNotFound(CarNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new CustomerError(exception.getErrors()));
    }


    @ExceptionHandler({CarEmptyAfterDeleteException.class})
    public ResponseEntity<CustomerError> carEmptyAfterDelete(CarEmptyAfterDeleteException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CustomerError(exception.getErrors()));
    }

    @ExceptionHandler({PaymentMethodNull.class})
    public ResponseEntity<CustomerError> paymentMethodNull(PaymentMethodNull exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CustomerError(exception.getErrors()));
    }



}
