package com.github.rafaelfernandes.parquimetro.estacionamento.e2e;

import com.github.rafaelfernandes.parquimetro.cliente.controller.request.Cliente;
import com.github.rafaelfernandes.parquimetro.cliente.dto.ClienteDto;
import com.github.rafaelfernandes.parquimetro.cliente.entity.ClienteEntity;
import com.github.rafaelfernandes.parquimetro.cliente.entity.ContatoEntity;
import com.github.rafaelfernandes.parquimetro.cliente.enums.FormaPagamento;
import com.github.rafaelfernandes.parquimetro.cliente.repository.ClienteRepository;
import com.github.rafaelfernandes.parquimetro.cliente.service.FormaPagamentoService;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.request.Fixo;
import com.github.rafaelfernandes.parquimetro.estacionamento.controller.response.Estacionamento;
import com.github.rafaelfernandes.parquimetro.estacionamento.entity.EstacionamentoAbertoEntity;
import com.github.rafaelfernandes.parquimetro.estacionamento.enums.TipoPeriodo;
import com.github.rafaelfernandes.parquimetro.estacionamento.repository.EstacionamentoRepository;
import com.github.rafaelfernandes.parquimetro.estacionamento.service.EstacionamentoService;
import com.github.rafaelfernandes.parquimetro.util.ClienteCarro;
import com.github.rafaelfernandes.parquimetro.util.GerarCadastro;
import com.github.rafaelfernandes.parquimetro.util.MongoContainers;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
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
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class EstacionamentoControllerTest {

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
    private ClienteRepository clienteRepository;

    @Autowired
    private FormaPagamentoService formaPagamentoService;

    @Autowired
    private EstacionamentoService estacionamentoService;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @BeforeEach
    void setup(){
        clienteRepository.deleteAll();
        estacionamentoRepository.deleteAll();
    }

    @NotNull
    private ClienteCarro cadastrarNovoCliente() {
        Cliente cliente = GerarCadastro.cliente(Boolean.TRUE);
        ClienteEntity clienteEntity = ClienteDto.from(cliente, Boolean.TRUE);

        ClienteEntity clienteSalvoEntity = clienteRepository.save(clienteEntity);

        Cliente clienteSalvo = ClienteDto.from(clienteSalvoEntity);

        return new ClienteCarro(clienteSalvo, clienteSalvo.carros().get(0));
    }

    @Test
    void deveRetornarEstacionamento(){

        ClienteCarro clienteCarro = cadastrarNovoCliente();

        this.estacionamentoService.registrar(TipoPeriodo.FIXO, clienteCarro.cliente().id(), clienteCarro.carro(), 3);

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/estacionamento/"+ clienteCarro.cliente().id() + "/" + clienteCarro.carro(),
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String id = documentContext.read("$.estacionamentos[0].cliente_id");

        assertThat(id).isEqualTo(clienteCarro.cliente().id().toString());

        String carro = documentContext.read("$.estacionamentos[0].carro");

        assertThat(carro).isEqualTo(clienteCarro.carro());

        String formaPagamento = documentContext.read("$.estacionamentos[0].forma_pagamento");

        assertThat(formaPagamento).isEqualTo(clienteCarro.cliente().forma_pagamento().name());

        String tipoPeriodo = documentContext.read("$.estacionamentos[0].tipo_periodo");

        assertThat(tipoPeriodo).isEqualTo(TipoPeriodo.FIXO.name());

        Integer duracaoFixa = documentContext.read("$.estacionamentos[0].duracao_fixa");

        assertThat(duracaoFixa).isEqualTo(3);

        LocalDateTime inicio = LocalDateTime.parse(documentContext.read("$.estacionamentos[0].inicio"));

        assertThat(inicio).isNotNull();

        Integer httpStatusCode = documentContext.read("$.http_status_code");
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK.value());

        List<String> erros = documentContext.read("$.erros");
        assertThat(erros).isNotNull();

    }

    @Test
    void deveRetornarNotFoundQuandoProcurarEoClienteNaoExistir(){

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/estacionamento/"+ UUID.randomUUID() + "/AABBCCD",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        ArrayList<String> erros = documentContext.read("$.erros");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Registro não encontrado"))

        ;



    }

    @Test
    void deveRetornarNotFoundQuandoProcurarEoCarroNaoExistir(){

        ClienteCarro clienteCarro = cadastrarNovoCliente();

        ResponseEntity<String> response = this.restTemplate
                .getForEntity(
                        "/estacionamento/"+ clienteCarro.cliente().id() + "/AABBCCD",
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        ArrayList<String> erros = documentContext.read("$.erros");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Registro não encontrado"))

        ;



    }

    @Test
    void devCadastrarPeriodoFixo(){

        ClienteCarro clienteCarro = cadastrarNovoCliente();

        Fixo fixo = new Fixo(3);

        ResponseEntity<Void> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/" + clienteCarro.carro() + "/fixo",
                        fixo,
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
    void deveRetornarNotFoundQuandoClienteNaoExisteAoTentarRegistrarTempoFixo(){

        Fixo fixo = new Fixo(3);

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + UUID.randomUUID() + "/AABBCCD/fixo",
                        fixo,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(createResponse.getBody());

        ArrayList<String> erros = documentContext.read("$.erros");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Cliente não existe!"))

        ;

    }

    @Test
    void deveRetornarNotFoundQuandoPlacaNaoExisteAoTentarRegistrarTempoFixo(){

        ClienteCarro clienteCarro = cadastrarNovoCliente();

        Fixo fixo = new Fixo(3);

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/AABBCCD/fixo",
                        fixo,
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
    void deveRetonarDuplicidadeQUandoTentaRegistrarTempoFixoEUmaMesmaPlaca(){
        ClienteCarro clienteCarro = cadastrarNovoCliente();

        Fixo fixo = new Fixo(3);

        ResponseEntity<Void> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/" + clienteCarro.carro() + "/fixo",
                        fixo,
                        Void.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        ResponseEntity<String> createResponseDuplicate = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/" + clienteCarro.carro() + "/fixo",
                        fixo,
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
    void deveRetonarBadREquestQuandoEnviaDuracaoZeroOuMenorParaFixo(){
        ClienteCarro clienteCarro = cadastrarNovoCliente();

        Fixo fixo = new Fixo(0);

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/" + clienteCarro.carro() + "/fixo",
                        fixo,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


        DocumentContext documentContext = JsonPath.parse(createResponse.getBody());

        ArrayList<String> erros = documentContext.read("$.erros");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Tempo mínimo de 1 hora"))

        ;

        fixo = new Fixo(-1);

        createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/" + clienteCarro.carro() + "/fixo",
                        fixo,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


        documentContext = JsonPath.parse(createResponse.getBody());

        erros = documentContext.read("$.erros");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Tempo mínimo de 1 hora"))

        ;

    }

    @Test
    void deveRegistrarTempoPorHora(){

        ClienteCarro clienteCarro = cadastrarNovoCliente();

        this.formaPagamentoService.alterar(clienteCarro.cliente().id(), FormaPagamento.CARTAO_CREDITO.name());

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/" + clienteCarro.carro() + "/hora",
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
        ClienteCarro clienteCarro = cadastrarNovoCliente();

        this.formaPagamentoService.alterar(clienteCarro.cliente().id(), FormaPagamento.PIX.name());

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/" + clienteCarro.carro() + "/hora",
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

        Fixo fixo = new Fixo(3);

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + UUID.randomUUID() + "/AABBCCD/hora",
                        fixo,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext documentContext = JsonPath.parse(createResponse.getBody());

        ArrayList<String> erros = documentContext.read("$.erros");

        assertThat(erros)
                .anyMatch(erro -> erro.equalsIgnoreCase("Cliente não existe!"))

        ;

    }

    @Test
    void deveRetornarNotFoundQuandoPlacaNaoExisteAoTentarRegistrarTempoHora(){

        ClienteCarro clienteCarro = cadastrarNovoCliente();

        Fixo fixo = new Fixo(3);

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/AABBCCD/hora",
                        fixo,
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
        ClienteCarro clienteCarro = cadastrarNovoCliente();

        this.formaPagamentoService.alterar(clienteCarro.cliente().id(), FormaPagamento.CARTAO_CREDITO.name());

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/" + clienteCarro.carro() + "/hora",
                        null,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        ResponseEntity<String> createResponseDuplicate = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/" + clienteCarro.carro() + "/hora",
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
        ClienteCarro clienteCarro = cadastrarNovoCliente();

        this.formaPagamentoService.alterar(clienteCarro.cliente().id(), FormaPagamento.CARTAO_CREDITO.name());

        Fixo fixo = new Fixo(0);

        ResponseEntity<String> createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/" + clienteCarro.carro() + "/hora",
                        fixo,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        DocumentContext documentContext = JsonPath.parse(createResponse.getBody());

        Integer duracao = documentContext.read("$.estacionamentos[0].duracao_fixa");

        assertThat(duracao).isNull();

        this.estacionamentoRepository.deleteAll();

        fixo = new Fixo(-1);

        createResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/" + clienteCarro.carro() + "/hora",
                        fixo,
                        String.class
                );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        documentContext = JsonPath.parse(createResponse.getBody());

        duracao = documentContext.read("$.estacionamentos[0].duracao_fixa");

        assertThat(duracao).isNull();

    }


    @Test
    void deveFinalizarFixo2HorasSemMulta(){

        ClienteCarro clienteCarro = cadastrarNovoCliente();

        this.formaPagamentoService.alterar(clienteCarro.cliente().id(), FormaPagamento.CARTAO_CREDITO.name());

        EstacionamentoAbertoEntity estacionamentoAberto = new EstacionamentoAbertoEntity(
                UUID.randomUUID(),
                clienteCarro.cliente().id(),
                clienteCarro.carro(),
                clienteCarro.cliente().nome(),
                new ContatoEntity(
                        clienteCarro.cliente().contato().email(),
                        clienteCarro.cliente().contato().celular()
                ),
                clienteCarro.cliente().forma_pagamento(),
                TipoPeriodo.FIXO,
                2,
                LocalDateTime.now().minusHours(2L)
        );

        this.estacionamentoRepository.insert(estacionamentoAberto);

        ResponseEntity<String> finalizarResponse = this.restTemplate
                .postForEntity(
                        "/estacionamento/" + clienteCarro.cliente().id() + "/" + clienteCarro.carro() + "/finalizar",
                        null,
                        String.class
                );

        assertThat(finalizarResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(finalizarResponse.getBody());

        LocalDateTime inicio = LocalDateTime.parse(documentContext.read("$.recibo.inicio"));
        assertThat(inicio).isNotNull();

        LocalDateTime fim = LocalDateTime.parse(documentContext.read("$.recibo.fim"));
        assertThat(fim).isNotNull();

        Integer horasSolicitadas = documentContext.read("$.recibo.horas_solicitadas");
        assertThat(horasSolicitadas).isEqualTo(2);

        Double valor = documentContext.read("$.recibo.valor");
        assertThat(valor).isEqualTo(14.0);

        Integer tempoAMais = documentContext.read("$.recibo.tempo_a_mais");
        assertThat(tempoAMais).isEqualTo(0L);

        Double multa = documentContext.read("$.recibo.multa");
        assertThat(multa).isEqualTo(0);

        Double valorFinal = documentContext.read("$.recibo.valor_final");
        assertThat(valorFinal).isEqualTo(14.0);

        Integer httpStatusCode = documentContext.read("$.http_status_code");
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK.value());

        List<String> erros = documentContext.read("$.erros");
        assertThat(erros).isNull();

    }


}
