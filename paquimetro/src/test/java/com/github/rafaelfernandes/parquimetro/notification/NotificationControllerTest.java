package com.github.rafaelfernandes.parquimetro.notification;

import com.github.rafaelfernandes.parquimetro.customer.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.customer.entity.ContactEntity;
import com.github.rafaelfernandes.parquimetro.customer.entity.CustomerEntity;
import com.github.rafaelfernandes.parquimetro.customer.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.customer.repository.CustomerRepository;
import com.github.rafaelfernandes.parquimetro.customer.service.PaymentMethodService;
import com.github.rafaelfernandes.parquimetro.parking.entity.ParkingOpenedEntity;
import com.github.rafaelfernandes.parquimetro.parking.enums.ParkingType;
import com.github.rafaelfernandes.parquimetro.parking.repository.ParkingOpenedRepository;
import com.github.rafaelfernandes.parquimetro.parking.service.ParkingService;
import com.github.rafaelfernandes.parquimetro.util.CustomerCar;
import com.github.rafaelfernandes.parquimetro.util.GenerateData;
import com.github.rafaelfernandes.parquimetro.util.MongoContainers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers

public class NotificationControllerTest {

    @Container //
    private static MongoDBContainer mongoDBContainer = MongoContainers.getDefaultContainer();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.auto-index-creation", MongoContainers::getTrue);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ParkingOpenedRepository parkingOpenedRepository;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private ParkingService parkingService;


    @BeforeEach
    void setup(){
        customerRepository.deleteAll();
        parkingOpenedRepository.deleteAll();
    }


    @NotNull
    private CustomerCar createNewCustomer(Boolean createAndFinishParkiing) {
        Customer customer = GenerateData.customer(Boolean.TRUE);
        CustomerEntity customerEntity = CustomerEntity.from(customer, Boolean.TRUE);

        CustomerEntity clienteSalvoEntity = customerRepository.save(customerEntity);

        paymentMethodService.change(clienteSalvoEntity.id(), PaymentMethod.CREDIT_CARD.name());

        if (createAndFinishParkiing){

            ParkingOpenedEntity parkingOpenedEntity = new ParkingOpenedEntity(
                    UUID.randomUUID(),
                    clienteSalvoEntity.id(),
                    customer.cars().get(0),
                    customer.name(),
                    new ContactEntity(
                            customer.contact().email(),
                            customer.contact().cellphone()
                    ),
                    customer.payment_method(),
                    ParkingType.HOUR,
                    null,
                    LocalDateTime.now().minusHours(3L).minusMinutes(30L)
            );

            this.parkingOpenedRepository.insert(parkingOpenedEntity);

            this.parkingService.finish(clienteSalvoEntity.id(), customer.cars().get(0));

        }

        Customer customerSalvo = Customer.from(clienteSalvoEntity);

        return new CustomerCar(customerSalvo, customerSalvo.cars().get(0));
    }

    @Test
    @DisplayName("POST /notification/receipt -> Should Return Success When Send One Receipt Email")
    void shouldReturnSuccessWhenSendOneReceiptEmail(){

        createNewCustomer(Boolean.TRUE);


        ResponseEntity<String> response = this.restTemplate
                .postForEntity(
                        "/notification/receipt",
                        null,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);



    }


}
