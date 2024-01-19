package com.github.rafaelfernandes.parquimetro.estacionamento.e2e;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Customer;
import com.github.rafaelfernandes.parquimetro.cliente.entity.CustomerEntity;
import com.github.rafaelfernandes.parquimetro.cliente.entity.ContactEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.PaymentMethod;
import com.github.rafaelfernandes.parquimetro.cliente.repository.CustomerRepository;
import com.github.rafaelfernandes.parquimetro.cliente.service.PaymentMethodService;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.request.FixTime;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.ParkingOpenedEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.ParkingType;
import com.github.rafaelfernandes.parquimetro.estacionamento.repository.EstacionamentoAbertoRepository;
import com.github.rafaelfernandes.parquimetro.estacionamento.service.ParkingService;
import com.github.rafaelfernandes.parquimetro.util.CustomerCar;
import com.github.rafaelfernandes.parquimetro.util.GenerateData;
import com.github.rafaelfernandes.parquimetro.util.MongoContainers;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
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

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ParkingControllerTest {

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
    private PaymentMethodService paymentMethodService;

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private EstacionamentoAbertoRepository estacionamentoAbertoRepository;

    @BeforeEach
    void setup(){
        customerRepository.deleteAll();
        estacionamentoAbertoRepository.deleteAll();
    }

    @NotNull
    private CustomerCar createNewCustomer() {
        Customer customer = GenerateData.customer(Boolean.TRUE);
        CustomerEntity customerEntity = CustomerEntity.from(customer, Boolean.TRUE);

        CustomerEntity clienteSalvoEntity = customerRepository.save(customerEntity);

        Customer customerSalvo = Customer.from(clienteSalvoEntity);

        return new CustomerCar(customerSalvo, customerSalvo.cars().get(0));
    }

    @Test
    @DisplayName("GET /parking/customerId/car -> Should Return Parking Opened")
    void shouldReturnParkingOpened(){

        CustomerCar customerCar = createNewCustomer();

        this.parkingService.registrar(ParkingType.FIX, customerCar.customer().id(), customerCar.carro(), 3);

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/parking/"+ customerCar.customer().id() + "/" + customerCar.carro() + "/opened",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.customer_id");

        assertThat(id).isEqualTo(customerCar.customer().id().toString());

        String car = documentContext.read("$.car");

        assertThat(car).isEqualTo(customerCar.carro());

        String paymentMethod = documentContext.read("$.payment_method");

        assertThat(paymentMethod).isEqualTo(customerCar.customer().payment_method().name());

        String parkingType = documentContext.read("$.parking_type");

        assertThat(parkingType).isEqualTo(ParkingType.FIX.name());

        Integer duration = documentContext.read("$.duration");

        assertThat(duration).isEqualTo(3);

        LocalDateTime start = LocalDateTime.parse(documentContext.read("$.start"));

        assertThat(start).isNotNull();

    }

    @Test
    @DisplayName("GET /parking/customerId/car -> Should Return Not Found When Customer Not Exists")
    void shouldReturnNotFoundWhenCustomerNotExists(){

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/parking/"+ UUID.randomUUID() + "/AABBCCD" + "/opened",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        ArrayList<String> erros = documentContext.read("$.errors");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Registro não encontrado"))

        ;
    }

    @Test
    @DisplayName("GET /parking/customerId/car -> Should Return Not Found When Car Not Exists In Customer")
    void shouldReturnNotFoundWhenCarNotExistsInCustomer(){

        CustomerCar customerCar = createNewCustomer();

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/parking/"+ customerCar.customer().id() + "/AABBCCD" + "/opened",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        ArrayList<String> erros = documentContext.read("$.errors");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Registro não encontrado"))

        ;



    }

    @Test
    void devCadastrarPeriodoFixo(){

        CustomerCar customerCar = createNewCustomer();

        FixTime fixTime = new FixTime(3);

        ResponseEntity<Void> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/" + customerCar.carro() + "/fixo",
                        fixTime,
                        Void.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI location = createResponse.getHeaders().getLocation();

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        location,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    @DisplayName("POST -> /parking/customerId/car/fix -> Should return customer not found when register park fix")
    void shouldReturnCustomerNotFoundWhenRegisterParkFix(){

        FixTime fixTime = new FixTime(3);

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/parking/" + UUID.randomUUID() + "/AABBCCD/fix",
                        fixTime,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(createResponse.getBody());

        ArrayList<String> erros = documentContext.read("$.errors");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Cliente não existe!"))

        ;

    }

    @Test
    @DisplayName("POST -> /parking/customerId/car/fix -> Should Return Not Found When Car Not Exists In Customer On Register Fix Time")
    void shouldReturnNotFoundWhenCarNotExistsInCustomerOnRegisterFixTime(){

        CustomerCar customerCar = createNewCustomer();

        FixTime fixTime = new FixTime(3);

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/parking/" + customerCar.customer().id() + "/AABBCCD/fix",
                        fixTime,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(createResponse.getBody());

        ArrayList<String> erros = documentContext.read("$.errors");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Carro não existe para esse cliente"))

        ;

    }

    @Test
    void deveRetonarDuplicidadeQUandoTentaRegistrarTempoFixoEUmaMesmaPlaca(){
        CustomerCar customerCar = createNewCustomer();

        FixTime fixTime = new FixTime(3);

        ResponseEntity<Void> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/" + customerCar.carro() + "/fixo",
                        fixTime,
                        Void.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        ResponseEntity<String> createResponseDuplicate = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/" + customerCar.carro() + "/fixo",
                        fixTime,
                        String.class
                );

        assertThat(createResponseDuplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        DocumentContext documentContext = JsonPath.parse(createResponseDuplicate.getBody());

        ArrayList<String> erros = documentContext.read("$.erros");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Carro já está com tempo lançado!"))

        ;

    }

    @Test
    @DisplayName("POST -> /parking/customerId/car/fix -> Should Return Bad Request When Duration Is Less Or Equals Zero To Fix Time")
    void shouldReturnBadRequestWhenDurationIsLessOrEqualsZeroToFixTime(){
        CustomerCar customerCar = createNewCustomer();

        FixTime fixTime = new FixTime(0);

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/parking/" + customerCar.customer().id() + "/" + customerCar.carro() + "/fix",
                        fixTime,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


        DocumentContext documentContext = JsonPath.parse(createResponse.getBody());

        ArrayList<String> erros = documentContext.read("$.errors");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Tempo mínimo de 1 hora"))

        ;

        fixTime = new FixTime(-1);

        createResponse = this.restTemplate
                .postForEntity(
                        "/parking/" + customerCar.customer().id() + "/" + customerCar.carro() + "/fix",
                        fixTime,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


        documentContext = JsonPath.parse(createResponse.getBody());

        erros = documentContext.read("$.errors");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Tempo mínimo de 1 hora"))

        ;

    }

    @Test
    void deveRegistrarTempoPorHora(){

        CustomerCar customerCar = createNewCustomer();

        this.paymentMethodService.change(customerCar.customer().id(), PaymentMethod.CARTAO_CREDITO.name());

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/" + customerCar.carro() + "/hora",
                        null,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI location = createResponse.getHeaders().getLocation();

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        location,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void deveRetornarBadRequestQuandoTipoPeriodoIgualHoraEPgamentoPix(){
        CustomerCar customerCar = createNewCustomer();

        this.paymentMethodService.change(customerCar.customer().id(), PaymentMethod.PIX.name());

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/" + customerCar.carro() + "/hora",
                        null,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


        DocumentContext documentContext = JsonPath.parse(createResponse.getBody());

        ArrayList<String> erros = documentContext.read("$.erros");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Forma de pagamento não permitido para o tipo de periodo escolhido!"))

        ;
    }

    @Test
    void deveRetornarNotFoundQuandoClienteNaoExisteAoTentarRegistrarTempoHora(){

        FixTime fixTime = new FixTime(3);

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + UUID.randomUUID() + "/AABBCCD/hora",
                        fixTime,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(createResponse.getBody());

        ArrayList<String> erros = documentContext.read("$.errors");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Cliente não existe!"))

        ;

    }

    @Test
    void deveRetornarNotFoundQuandoPlacaNaoExisteAoTentarRegistrarTempoHora(){

        CustomerCar customerCar = createNewCustomer();

        FixTime fixTime = new FixTime(3);

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/AABBCCD/hora",
                        fixTime,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(createResponse.getBody());

        ArrayList<String> erros = documentContext.read("$.erros");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Carro não cadastrado para esse cliente"))

        ;

    }

    @Test
    void deveRetonarDuplicidadeQUandoTentaRegistrarTempoHoraEUmaMesmaPlaca(){
        CustomerCar customerCar = createNewCustomer();

        this.paymentMethodService.change(customerCar.customer().id(), PaymentMethod.CARTAO_CREDITO.name());

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/" + customerCar.carro() + "/hora",
                        null,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        ResponseEntity<String> createResponseDuplicate = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/" + customerCar.carro() + "/hora",
                        null,
                        String.class
                );

        assertThat(createResponseDuplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        DocumentContext documentContext = JsonPath.parse(createResponseDuplicate.getBody());

        ArrayList<String> erros = documentContext.read("$.erros");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Carro já está com tempo lançado!"))

        ;

    }

    @Test
    void deveRetonarDuracaoNulaQuandoRegistraHora(){
        CustomerCar customerCar = createNewCustomer();

        this.paymentMethodService.change(customerCar.customer().id(), PaymentMethod.CARTAO_CREDITO.name());

        FixTime fixTime = new FixTime(0);

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/" + customerCar.carro() + "/hora",
                        fixTime,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        DocumentContext documentContext = JsonPath.parse(createResponse.getBody());

        Integer duracao = documentContext.read("$.estacionamentos[0].duration");

        assertThat(duracao).isNull();

        this.estacionamentoAbertoRepository.deleteAll();

        fixTime = new FixTime(-1);

        createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/" + customerCar.carro() + "/hora",
                        fixTime,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        documentContext = JsonPath.parse(createResponse.getBody());

        duracao = documentContext.read("$.estacionamentos[0].duration");

        assertThat(duracao).isNull();

    }


    @Test
    void deveFinalizarFixo2HorasSemMulta(){

        CustomerCar customerCar = createNewCustomer();

        this.paymentMethodService.change(customerCar.customer().id(), PaymentMethod.CARTAO_CREDITO.name());

        ParkingOpenedEntity estacionamentoAberto = new ParkingOpenedEntity(
                UUID.randomUUID(),
                customerCar.customer().id(),
                customerCar.carro(),
                customerCar.customer().name(),
                new ContactEntity(
                        customerCar.customer().contact().email(),
                        customerCar.customer().contact().cellphone()
                ),
                customerCar.customer().payment_method(),
                ParkingType.FIX,
                2,
                LocalDateTime.now().minusHours(2L)
        );

        this.estacionamentoAbertoRepository.insert(estacionamentoAberto);

        ResponseEntity<String> finalizarResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/" + customerCar.carro() + "/finalizar",
                        null,
                        String.class
                );

        assertThat(finalizarResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(finalizarResponse.getBody());

        LocalDateTime inicio = LocalDateTime.parse(documentContext.read("$.estacionamentos[0].recibo.start"));
        assertThat(inicio).isNotNull();

        LocalDateTime fim = LocalDateTime.parse(documentContext.read("$.estacionamentos[0].recibo.fim"));
        assertThat(fim).isNotNull();

        Integer horasSolicitadas = documentContext.read("$.estacionamentos[0].recibo.horas_solicitadas");
        assertThat(horasSolicitadas).isEqualTo(2);

        Double valor = documentContext.read("$.estacionamentos[0].recibo.valor");
        assertThat(valor).isEqualTo(14.0);

        Integer tempoAMais = documentContext.read("$.estacionamentos[0].recibo.tempo_a_mais");
        assertThat(tempoAMais).isEqualTo(0L);

        Double multa = documentContext.read("$.estacionamentos[0].recibo.multa");
        assertThat(multa).isEqualTo(0);

        Double valorFinal = documentContext.read("$.estacionamentos[0].recibo.valor_final");
        assertThat(valorFinal).isEqualTo(14.0);

        Integer httpStatusCode = documentContext.read("$.http_status_code");
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK.value());

        List<String> erros = documentContext.read("$.erros");
        assertThat(erros).isEmpty();

        URI location = finalizarResponse.getHeaders().getLocation();

        ResponseEntity<String> responseFinalizado = this.restTemplate
                .getForEntity(
                        location,
                        String.class
                );

        assertThat(responseFinalizado.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/estacionamento/"+ customerCar.customer().id() + "/" + customerCar.carro(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);


    }

    @Test
    void deveFinalizar2HorasComMulta(){
        CustomerCar customerCar = createNewCustomer();

        this.paymentMethodService.change(customerCar.customer().id(), PaymentMethod.CARTAO_CREDITO.name());

        ParkingOpenedEntity estacionamentoAberto = new ParkingOpenedEntity(
                UUID.randomUUID(),
                customerCar.customer().id(),
                customerCar.carro(),
                customerCar.customer().name(),
                new ContactEntity(
                        customerCar.customer().contact().email(),
                        customerCar.customer().contact().cellphone()
                ),
                customerCar.customer().payment_method(),
                ParkingType.FIX,
                2,
                LocalDateTime.now().minusHours(3L).minusMinutes(30L)
        );

        this.estacionamentoAbertoRepository.insert(estacionamentoAberto);

        ResponseEntity<String> finalizarResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/" + customerCar.carro() + "/finalizar",
                        null,
                        String.class
                );

        assertThat(finalizarResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(finalizarResponse.getBody());

        LocalDateTime inicio = LocalDateTime.parse(documentContext.read("$.estacionamentos[0].recibo.start"));
        assertThat(inicio).isNotNull();

        LocalDateTime fim = LocalDateTime.parse(documentContext.read("$.estacionamentos[0].recibo.fim"));
        assertThat(fim).isNotNull();

        Integer horasSolicitadas = documentContext.read("$.estacionamentos[0].recibo.horas_solicitadas");
        assertThat(horasSolicitadas).isEqualTo(2);

        Double valor = documentContext.read("$.estacionamentos[0].recibo.valor");
        assertThat(valor).isEqualTo(14.0);

        Integer tempoAMais = documentContext.read("$.estacionamentos[0].recibo.tempo_a_mais");
        assertThat(tempoAMais).isEqualTo(5400L);

        Double multa = documentContext.read("$.estacionamentos[0].recibo.multa");
        assertThat(multa).isEqualTo(20);

        Double valorFinal = documentContext.read("$.estacionamentos[0].recibo.valor_final");
        assertThat(valorFinal).isEqualTo(34.0);

        Integer httpStatusCode = documentContext.read("$.http_status_code");
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK.value());

        List<String> erros = documentContext.read("$.erros");
        assertThat(erros).isEmpty();

        URI location = finalizarResponse.getHeaders().getLocation();

        ResponseEntity<String> responseFinalizado = this.restTemplate
                .getForEntity(
                        location,
                        String.class
                );

        assertThat(responseFinalizado.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/estacionamento/"+ customerCar.customer().id() + "/" + customerCar.carro(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deveFinalizarComHoraVariavel(){

        CustomerCar customerCar = createNewCustomer();

        this.paymentMethodService.change(customerCar.customer().id(), PaymentMethod.CARTAO_CREDITO.name());

        ParkingOpenedEntity estacionamentoAberto = new ParkingOpenedEntity(
                UUID.randomUUID(),
                customerCar.customer().id(),
                customerCar.carro(),
                customerCar.customer().name(),
                new ContactEntity(
                        customerCar.customer().contact().email(),
                        customerCar.customer().contact().cellphone()
                ),
                customerCar.customer().payment_method(),
                ParkingType.HOUR,
                null,
                LocalDateTime.now().minusHours(3L).minusMinutes(30L)
        );

        this.estacionamentoAbertoRepository.insert(estacionamentoAberto);

        ResponseEntity<String> finalizarResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/" + customerCar.carro() + "/finalizar",
                        null,
                        String.class
                );

        assertThat(finalizarResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(finalizarResponse.getBody());

        LocalDateTime inicio = LocalDateTime.parse(documentContext.read("$.estacionamentos[0].recibo.start"));
        assertThat(inicio).isNotNull();

        LocalDateTime fim = LocalDateTime.parse(documentContext.read("$.estacionamentos[0].recibo.fim"));
        assertThat(fim).isNotNull();

        Integer horasSolicitadas = documentContext.read("$.estacionamentos[0].recibo.horas_solicitadas");
        assertThat(horasSolicitadas).isNull();

        Double valor = documentContext.read("$.estacionamentos[0].recibo.valor");
        assertThat(valor).isEqualTo(25.0);

        Integer tempoAMais = documentContext.read("$.estacionamentos[0].recibo.tempo_a_mais");
        assertThat(tempoAMais).isEqualTo(0L);

        Double multa = documentContext.read("$.estacionamentos[0].recibo.multa");
        assertThat(multa).isEqualTo(0.0);

        Double valorFinal = documentContext.read("$.estacionamentos[0].recibo.valor_final");
        assertThat(valorFinal).isEqualTo(25.0);

        Integer httpStatusCode = documentContext.read("$.http_status_code");
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK.value());

        List<String> erros = documentContext.read("$.erros");
        assertThat(erros).isEmpty();

        URI location = finalizarResponse.getHeaders().getLocation();

        ResponseEntity<String> responseFinalizado = this.restTemplate
                .getForEntity(
                        location,
                        String.class
                );

        assertThat(responseFinalizado.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/estacionamento/"+ customerCar.customer().id() + "/" + customerCar.carro(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void deveRetornarNotFoundQuandoNaoExisteEstacionamentoAberto(){

        CustomerCar customerCar = createNewCustomer();

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + customerCar.customer().id() + "/" + customerCar.carro() +"/finalizar",
                        null,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(createResponse.getBody());

        ArrayList<String> erros = documentContext.read("$.erros");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Registro de estacionamento não encontrado"))
        ;

    }
}
