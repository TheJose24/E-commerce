package com.slamracing.ecommerce.service;

import com.slamracing.ecommerce.dto.EmailRequestDTO;
import jakarta.mail.MessagingException;

public interface IEmailService {

    void enviarCorreoAdministrador(EmailRequestDTO requestDTO, String asunto) throws MessagingException;

    void enviarCorreoUsuario(EmailRequestDTO requestDTO, String asunto) throws MessagingException;

    String procesarPlantilla(String nombrePlantilla, EmailRequestDTO datos);
}
