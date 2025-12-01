package com.vesta.web.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Captura errores 404 (Página no encontrada)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(NoHandlerFoundException e, Model model) {
        return "error/404";
    }

    // Captura errores de la API (ej: 401 Credenciales malas)
    @ExceptionHandler(HttpClientErrorException.class)
    public String handleApiError(HttpClientErrorException e, Model model) {
        if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
            model.addAttribute("error", "Acceso denegado o sesión expirada.");
            return "login";
        }
        model.addAttribute("error", "Error de comunicación con Vesta API: " + e.getMessage());
        return "error/500";
    }

    // Captura cualquier otro error (500)
    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception e, Model model) {
        model.addAttribute("error", "Ocurrió un error inesperado: " + e.getMessage());
        return "error/500";
    }
}