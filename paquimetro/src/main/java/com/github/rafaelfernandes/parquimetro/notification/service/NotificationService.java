package com.github.rafaelfernandes.parquimetro.notification.service;

import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingOpenedEntity;
import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingSendReceiptEntity;
import com.github.rafaelfernandes.parquimetro.parking.enums.ParkingType;
import com.github.rafaelfernandes.parquimetro.parking.repository.ParkingOpenedRepository;
import com.github.rafaelfernandes.parquimetro.parking.repository.ParkingSendReceiptRepository;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    ParkingSendReceiptRepository parkingSendReceiptRepository;

    @Autowired
    ParkingOpenedRepository parkingOpenedRepository;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");

    NumberFormat brasilFormat = NumberFormat.getNumberInstance(Locale.GERMANY);

    public void sendReceipt() throws Exception {

        List<ParkingSendReceiptEntity> mails = this.parkingSendReceiptRepository.findAll();

        String templateName = "receipt";

        for (ParkingSendReceiptEntity mail : mails) {
            MimeMessage message = mailSender.createMimeMessage();

            message.setFrom(new InternetAddress("rafaelfernandes@github.com"));
            message.setRecipients(MimeMessage.RecipientType.TO, mail.email());
            message.setSubject("Recibo Estacionamento");

            Context context = new Context();
            context.setVariable("nome", mail.name());
            context.setVariable("inicio", mail.receipt().start().format(dateTimeFormatter));
            context.setVariable("fim", mail.receipt().end().format(dateTimeFormatter));
            context.setVariable("valor", brasilFormat.format(mail.receipt().value().multiply(new BigDecimal("1.0"))));
            context.setVariable("multa", brasilFormat.format(mail.receipt().penalty().multiply(new BigDecimal("1.0"))));
            context.setVariable("total", brasilFormat.format(mail.receipt().final_value().multiply(new BigDecimal("1.0"))));

            String text = templateEngine.process(templateName, context);

            message.setContent(text, "text/html; charset=utf-8");

            mailSender.send(message);

        }

        parkingSendReceiptRepository.deleteAll();

    }

    public void sendTimeToCloseFix() throws Exception{

        String templateName = "fix";

        LocalDateTime endTime = LocalDateTime.now()
                .minusMinutes(45)
                .withSecond(0)
                .withNano(0);

        List<ParkingOpenedEntity> parkingOpenedEntities = parkingOpenedRepository.findByParkingOpened(endTime, ParkingType.FIX);

        for (ParkingOpenedEntity opened: parkingOpenedEntities){
            MimeMessage message = mailSender.createMimeMessage();

            message.setFrom(new InternetAddress("rafaelfernandes@github.com"));
            message.setRecipients(MimeMessage.RecipientType.TO, opened.contact().email());
            message.setSubject("Alerta! Estacionamento Expirando...");

            Context context = new Context();
            context.setVariable("nome", opened.name());

            String text = templateEngine.process(templateName, context);

            message.setContent(text, "text/html; charset=utf-8");

            mailSender.send(message);
        }

    }




}
