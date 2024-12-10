package com.slamracing.ecommerce.payment.service;

import com.mercadopago.client.common.AddressRequest;
import com.mercadopago.client.common.PhoneRequest;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.slamracing.ecommerce.payment.dto.ItemDTO;
import com.slamracing.ecommerce.payment.dto.PreferenciaRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mercadopago.MercadoPagoConfig;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PreferenciaService {

    @Value("${mercado_pago_token}")
    private String mercadoPagoAccessToken;

    public String createPreference(PreferenciaRequestDTO preferenciaRequestDTO) {
        try {

            MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
            List<PreferenceItemRequest> preferenceItems = new ArrayList<>();
            for (ItemDTO item : preferenciaRequestDTO.getItems()) {
                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .pictureUrl(item.getPicture_url())
                        .categoryId(item.getCategory_id())
                        .quantity(item.getQuantity())
                        .currencyId(item.getCurrency_id())
                        .unitPrice(item.getUnit_price())
                        .build();
                preferenceItems.add(itemRequest);
            }

            /*PreferencePayerRequest payerRequest = PreferencePayerRequest.builder()
                    .name(preferenciaRequestDTO.getNombre())
                    .surname(preferenciaRequestDTO.getApellido())
                    .email(preferenciaRequestDTO.getEmail())
                    .phone(PhoneRequest.builder()
                            .areaCode("51")
                            .number(preferenciaRequestDTO.getTelefono())
                            .build())
                    .address(AddressRequest.builder()
                            .streetName(preferenciaRequestDTO.getDireccion())
                            .build())
                    .build();*/


            PreferenceBackUrlsRequest backUrlsRequest = PreferenceBackUrlsRequest.builder()
                    .success("https://slamracing.devbyjose.studio/productos")
                    .failure("https://www.linkedin.com/in/jose-sanchez-eisi/")
                    .pending("https://www.linkedin.com/in/jose-sanchez-eisi/")
                    .build();

            System.out.println("External Reference: " + preferenciaRequestDTO.getExternal_reference());

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(preferenceItems)
                    //.payer(payerRequest)
                    .externalReference(preferenciaRequestDTO.getExternal_reference())
                    .purpose("wallet_purchase")
                    .backUrls(backUrlsRequest)
                    .autoReturn("approved")
                    .statementDescriptor("Slam Racing Payment")
                    .notificationUrl("https://slamracing.devbyjose.studio/v1/mercadoPago/notificacion?source_news=webhooks")
                    .build();

            PreferenceClient preferenceClient = new PreferenceClient();

            Preference preference = preferenceClient.create(preferenceRequest);

            log.info("Preferencia creada: {}",preference.getResponse().getContent());

            return preference.getId();

        } catch (MPException | MPApiException e) {
            return e.toString();
        }
    }

}
