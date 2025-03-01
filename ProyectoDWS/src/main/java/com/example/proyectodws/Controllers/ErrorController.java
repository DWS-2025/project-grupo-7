package com.example.proyectodws.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ErrorController {

    // Manejo de error 404 (Página no encontrada)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(Exception ex, Model model) {
        model.addAttribute("error", "404 - Página no encontrada");
        model.addAttribute("message", "La página que buscas no existe.");
        return "errorScreens/Error404"; // Esto debe corresponder con la plantilla en templates/error/404.html
    }

    // Manejo de error 400 (Solicitud incorrecta)
    @ExceptionHandler(ResponseStatusException.class)
    public String handleBadRequest(ResponseStatusException ex, Model model) {
        if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
            model.addAttribute("error", "400 - Solicitud incorrecta");
            model.addAttribute("message", "Hubo un problema con la solicitud.");
            return "errorScreens/Error400";
        }

        if (ex.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            model.addAttribute("error", "500 - Error interno del servidor");
            model.addAttribute("message", "Ocurrió un problema inesperado.");
            return "errorScreens/Error500";
        }
        return "error/default";
    }

    // Manejo de error 300 (Redirección)
    @ExceptionHandler(IllegalStateException.class)
    public String handleRedirectError(Model model) {
        model.addAttribute("error", "300 - Redirección");
        model.addAttribute("message", "La página ha sido movida a otro lugar.");
        return "errorScreens/Error300";
    }

    // Manejo de error 500 (Error del servidor)
    @ExceptionHandler(Exception.class)
    public String handleInternalServerError(Exception ex, Model model) {
        model.addAttribute("error", "500 - Error del servidor");
        model.addAttribute("message", "Ocurrió un error inesperado.");
        return "errorScreens/Error500";
    }
}

