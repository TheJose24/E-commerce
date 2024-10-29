package com.slamracing.ecommerce.service;

import com.slamracing.ecommerce.config.EmailConfiguration;
import com.slamracing.ecommerce.dto.EmailRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class EmailServiceNewsletterImplement implements IEmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final EmailConfiguration emailConfiguration;
    private final SimpleMailMessage simpleMailMessage;

    @Override
    public void enviarCorreoAdministrador(EmailRequestDTO requestDTO, String asunto) throws MessagingException {
        String cuerpoHTML = procesarPlantilla("email/correo-admin-newsletter", requestDTO);
        enviarCorreoHTML(emailConfiguration.getEmailRecipient(), asunto, cuerpoHTML);
    }

    @Override
    public void enviarCorreoUsuario(EmailRequestDTO requestDTO, String asunto) throws MessagingException{
        log.info("Enviando correo de bienvenida a {}", requestDTO.getEmail());
        String cuerpoHTML = procesarPlantilla("email/correo-user-newsletter-bienvenida", requestDTO);
        enviarCorreoHTML(requestDTO.getEmail(), asunto, cuerpoHTML);
    }

    private void enviarCorreoHTML(String destinatario, String asunto, String cuerpoHTML) throws MessagingException {
        log.info("Enviando correo a {}", destinatario);
        MimeMessage mensaje = crearMimeMessage(destinatario, asunto, cuerpoHTML);
        javaMailSender.send(mensaje);
    }

    private MimeMessage crearMimeMessage(String destinatario, String asunto, String cuerpoHTML) throws MessagingException {
        MimeMessage mensaje = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(cuerpoHTML, true);
        helper.setFrom(Objects.requireNonNull(simpleMailMessage.getFrom()));
        return mensaje;
    }

    @Override
    public String procesarPlantilla(String nombrePlantilla, EmailRequestDTO datos) {
        Context contexto = new Context();
        contexto.setVariable("usuario", datos);
        return templateEngine.process(nombrePlantilla, contexto);
    }
}
