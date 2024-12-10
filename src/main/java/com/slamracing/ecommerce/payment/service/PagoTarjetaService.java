package com.slamracing.ecommerce.payment.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.common.PhoneRequest;
import com.mercadopago.client.payment.*;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.slamracing.ecommerce.exception.MercadoPagoException;
import com.slamracing.ecommerce.model.PagoEntity;
import com.slamracing.ecommerce.model.PedidoEntity;
import com.slamracing.ecommerce.model.enums.MetodoPago;
import com.slamracing.ecommerce.payment.dto.PagoTarjetaDTO;
import com.slamracing.ecommerce.repository.PagoRepository;
import com.slamracing.ecommerce.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PagoTarjetaService {
    @Value("${mercado_pago_token}")
    private String mercadoPagoAccessToken;

    @Autowired
    private final PagoRepository pagoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    public PagoTarjetaService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }


//    public Long processPayment() throws MPException, MPApiException {
//            Map<String, String> customHeaders = new HashMap<>();
//            customHeaders.put("x-idempotency-key", "fe86f5f6-4bba-4d0a-bbec-bfbd59e50d91");
//
//            MPRequestOptions requestOptions = MPRequestOptions.builder()
//                    .customHeaders(customHeaders)
//                    .build();
//
//            MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
//
//            PaymentClient client = new PaymentClient();
//
//            List<PaymentItemRequest> items = new ArrayList<>();
//
//            PaymentItemRequest item =
//                    PaymentItemRequest.builder()
//                            .id("PR0001")
//                            .title("Point Mini")
//                            .description("Producto Point para cobros con tarjetas mediante bluetooth")
//                            .pictureUrl(
//                                    "https://http2.mlstatic.com/resources/frontend/statics/growth-sellers-landings/device-mlb-point-i_medium@2x.png")
//                            .categoryId("electronics")
//                            .quantity(1)
//                            .unitPrice(new BigDecimal("58.8"))
//                            .build();
//
//            items.add(item);
//
//            PaymentCreateRequest createRequest =
//                    PaymentCreateRequest.builder()
//                            .additionalInfo(
//                                    PaymentAdditionalInfoRequest.builder()
//                                            .items(items)
//                                            .payer(
//                                                    PaymentAdditionalInfoPayerRequest.builder()
//                                                            .firstName("Test")
//                                                            .lastName("Test")
//                                                            .phone(
//                                                                    PhoneRequest.builder().areaCode("11").number("987654321").build())
//                                                            .build())
//                                            .shipments(
//                                                    PaymentShipmentsRequest.builder()
//                                                            .receiverAddress(
//                                                                    PaymentReceiverAddressRequest.builder()
//                                                                            .zipCode("12312-123")
//                                                                            .stateName("Rio de Janeiro")
//                                                                            .cityName("Buzios")
//                                                                            .streetName("Av das Nacoes Unidas")
//                                                                            .streetNumber("3003")
//                                                                            .build())
//                                                            .build())
//                                            .build())
//                            .description("Payment for product")
//                            .externalReference("MP0001")
//                            .installments(1)
//                            .order(PaymentOrderRequest.builder().type("mercadolibre").build())
//                            .payer(PaymentPayerRequest.builder().entityType("individual").type("customer").build())
//                            .paymentMethodId("visa")
//                            .transactionAmount(new BigDecimal("58.8"))
//                            .build();
//
//            Payment createdPayment = client.create(createRequest, requestOptions);
//
//            return createdPayment.getId();
//    }







    public Long processPayment(PagoTarjetaDTO pagoTarjetaDTO) {
        try {
            /*Map<String, String> customHeaders = new HashMap<>();
            customHeaders.put("x-idempotency-key", "c1974d31-bb88-4a32-ad45-7435d373e42b");

            MPRequestOptions requestOptions = MPRequestOptions.builder()
                    .customHeaders(customHeaders)
                    .build();*/

            // Imprimir el JSON recibido del frontend
            System.out.println("JSON recibido del frontend: " + pagoTarjetaDTO.toString());

            MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);

            PaymentClient paymentClient = new PaymentClient();

            PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
                    .additionalInfo((PaymentAdditionalInfoRequest) pagoTarjetaDTO.getAdditional_info())
                    .description(pagoTarjetaDTO.getProductDescription())
                    .installments(pagoTarjetaDTO.getInstallments())
                    .payer(PaymentPayerRequest.builder()
                            .email(pagoTarjetaDTO.getPayer().getEmail())
                            .identification(IdentificationRequest.builder()
                                    .type(pagoTarjetaDTO.getPayer().getIdentification().getType())
                                    .number(pagoTarjetaDTO.getPayer().getIdentification().getNumber())
                                    .build())
                            .build())
                    .paymentMethodId(pagoTarjetaDTO.getPayment_method_id())
                    .token(pagoTarjetaDTO.getToken())
                    .transactionAmount(pagoTarjetaDTO.getTransaction_amount())
                    .externalReference(pagoTarjetaDTO.getExternal_reference())
                    .statementDescriptor(pagoTarjetaDTO.getStatement_descriptor())
                    .description(pagoTarjetaDTO.getProductDescription())
                    .build();

            System.out.println("Solicitud de pago: " + paymentCreateRequest.getExternalReference());

            System.out.println("Creando Pago");

            //Payment createdPayment = paymentClient.create(paymentCreateRequest, requestOptions);
            Payment createdPayment = paymentClient.create(paymentCreateRequest);
            System.out.println("Pago creado: " + createdPayment.getResponse().getContent());

            PagoEntity pago = new PagoEntity();

            String externalReference = pagoTarjetaDTO.getExternal_reference();
            System.out.println("External reference: " + externalReference);


            Long idPedido = Long.parseLong(externalReference);
            System.out.println("ID del pedido: " + idPedido);
            PedidoEntity pedidoBd = pedidoRepository.findById(idPedido).orElse(null);
            System.out.println("Pedido encontrado: " + pedidoBd);


            pago.setPedido(pedidoBd);
            pago.setMetodoPago(MetodoPago.TARJETA_CREDITO);
            pago.setMonto_recibido(createdPayment.getTransactionAmount());
            pago.setComision_mp(createdPayment.getFeeDetails().get(0).getAmount());
            pago.setMontoNeto(createdPayment.getTransactionAmount().subtract(createdPayment.getFeeDetails().get(0).getAmount()));
            pago.setEstadoTransaccion(createdPayment.getStatus());

            // Obtener la zona horaria correspondiente a Perú
            ZoneId zonaPeru = ZoneId.of("America/Lima");

            // Obtener el offset de la zona horaria de Perú en la fecha y hora dada
            ZoneOffset offsetPeru = zonaPeru.getRules().getOffset(createdPayment.getDateCreated().toInstant());

            // Convertir el OffsetDateTime a ZonedDateTime con la zona horaria de Perú
            ZonedDateTime zonedDateTimePeru = createdPayment.getDateCreated().atZoneSameInstant(offsetPeru);

            // Formatear la fecha y hora en un formato más entendible
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String fechaHoraFormateada = zonedDateTimePeru.format(formatter);

            pago.setFechaPago(LocalDateTime.parse(fechaHoraFormateada));


            pagoRepository.save(pago);


            return createdPayment.getId();
        } catch (MPApiException ex) {
            ex.printStackTrace();
            System.out.printf(
                    "MercadoPago Error. Status: %s, Content: %s%n",
                    ex.getApiResponse().getStatusCode(), ex.getApiResponse().getContent());
        } catch (MPException exception) {
            exception.printStackTrace();
            throw new MercadoPagoException(exception.getMessage());
        }
        return null;
    }

    public String buscarPagoPorId(String id) {
        // Construir la URL de la API de Mercado Pago para obtener la información del pago
        String apiUrl = "https://api.mercadopago.com/v1/payments/" + id;

        // Configurar HttpHeaders con el token de acceso para autenticar la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(mercadoPagoAccessToken);

        // Configurar RestTemplate para hacer la solicitud HTTP con los encabezados configurados
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Hacer la solicitud HTTP a la API de Mercado Pago
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // La solicitud fue exitosa, extraer la información del cuerpo de la respuesta
            String paymentInfo = response.getBody();
            System.out.println("Información del pago: " + paymentInfo);

            // Retornar una respuesta HTTP 200 (OK) para indicar que la notificación fue recibida correctamente
            return paymentInfo;
        } else {
            // La solicitud no fue exitosa, manejar el error adecuadamente
            System.err.println("Error al obtener la información del pago. Código de estado: " + response.getStatusCodeValue());
            return "Error al obtener la información del pago";
        }

    }
}
