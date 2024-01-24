package com.github.rafaelfernandes.parquimetro.notification.controller;

import com.github.rafaelfernandes.parquimetro.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/receipt")
    ResponseEntity<Void> sendReceipt() throws Exception {

        this.notificationService.sendReceipt();

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();

    }

    @PostMapping("/timeToClose/fix")
    ResponseEntity<Void> sendTimeToCloseFix() throws Exception{
        this.notificationService.sendTimeToCloseFix();

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();


    }

}
