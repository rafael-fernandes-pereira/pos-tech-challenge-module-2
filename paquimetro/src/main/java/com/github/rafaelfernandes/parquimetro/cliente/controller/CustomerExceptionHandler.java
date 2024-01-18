package com.github.rafaelfernandes.parquimetro.cliente.controller;

import com.github.rafaelfernandes.parquimetro.cliente.controller.response.CustomerError;
import com.github.rafaelfernandes.parquimetro.cliente.exception.CustomerDuplicateException;
import com.github.rafaelfernandes.parquimetro.cliente.exception.CustomerNotFoundException;
import com.github.rafaelfernandes.parquimetro.cliente.exception.CustomerValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomerExceptionHandler {

    @ExceptionHandler({CustomerNotFoundException.class})
    public ResponseEntity<CustomerError> customerNotFound(CustomerNotFoundException exception){
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



}
