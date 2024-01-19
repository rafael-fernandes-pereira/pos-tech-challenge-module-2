package com.github.rafaelfernandes.parquimetro.estacionamento.controller;

import com.github.rafaelfernandes.parquimetro.cliente.controller.response.CustomerError;
import com.github.rafaelfernandes.parquimetro.cliente.exception.*;
import com.github.rafaelfernandes.parquimetro.estacionamento.exception.ParkingOpenedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "com.github.rafaelfernandes.parquimetro.estacionamento.controller")
public class ParkingExceptionHandler {

    @ExceptionHandler({ParkingOpenedException.class})
    public ResponseEntity<CustomerError> parkingOpenedNotFound(ParkingOpenedException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new CustomerError(exception.getErrors()));
    }

    @ExceptionHandler({CustomerValidationException.class})
    public ResponseEntity<CustomerError> customerErrorValidation(CustomerValidationException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CustomerError(exception.getErrors()));
    }

    @ExceptionHandler({CustomerDuplicateException.class})
    public ResponseEntity<CustomerError> customerDuplicate(CustomerDuplicateException exception){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new CustomerError(exception.getErrors()));
    }

    @ExceptionHandler({CarEmptyException.class})
    public ResponseEntity<CustomerError> carEmpty(CarEmptyException exception){
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
