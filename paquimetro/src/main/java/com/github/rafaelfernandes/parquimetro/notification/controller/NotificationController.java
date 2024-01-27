package com.github.rafaelfernandes.parquimetro.notification.controller;

import com.github.rafaelfernandes.parquimetro.customer.controller.response.CustomerError;
import com.github.rafaelfernandes.parquimetro.notification.service.NotificationService;
import com.github.rafaelfernandes.parquimetro.parking.controller.response.open.ParkingOpened;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@Tag(name = "Notification", description = "Notification Endpoint - Responsável pelo envio de notificações")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Enviar recibo")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "200")
    })
    @PostMapping("/receipt")
    ResponseEntity<Void> sendReceipt() throws Exception {

        this.notificationService.sendReceipt();

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();

    }

    @Operation(summary = "Enviar notificação de tempo fixo")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "200")
    })
    @PostMapping("/timeToClose/fix")
    ResponseEntity<Void> sendTimeToCloseFix() throws Exception{
        this.notificationService.sendTimeToCloseFix();

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(summary = "Enviar notificação de tempo fixo")
    @ApiResponses(value = {
            @ApiResponse(description = "Sucesso", responseCode = "200")
    })
    @PostMapping("/timeToClose/hour")
    ResponseEntity<Void> sendTimeToCloseHour() throws Exception{
        this.notificationService.sendTimeToCloseHour();

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();


    }

}
