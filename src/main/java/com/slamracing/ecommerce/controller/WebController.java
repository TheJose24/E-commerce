package com.slamracing.ecommerce.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Controller
public class WebController {

    @GetMapping("/cambiar_idioma")
    public String changeLanguage(@RequestParam("idioma") String lang, HttpSession session) {
        Locale newLocale;

        // Usar el Locale específico basado en el idioma
        if ("en".equalsIgnoreCase(lang)) {
            newLocale = Locale.US; // Inglés de Estados Unidos
        } else if ("es".equalsIgnoreCase(lang)) {
            newLocale = new Locale.Builder()
                    .setLanguage("ES") // Español de Perú
                    .setRegion("PE")
                    .build();
        } else {
            newLocale = Locale.getDefault(); // Otras opciones, por defecto
        }

        // Establecer el Locale en la sesión
        session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, newLocale);
        return "redirect:/";
    }

    @GetMapping("/")
    public String home(Model model, Locale locale) {
        // Mostrar el idioma actual en mayúsculas
        model.addAttribute("idiomaActual", locale.getLanguage().toUpperCase());
        return "user/index";
    }




}
