/*
package com.example.proyectodws.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ErrorController {

    // Manejador de error 404 - Página no encontrada
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Model model) {
        model.addAttribute("error", "404 - Página no encontrada");
        model.addAttribute("message", "La página que buscas no existe.");
        return "errorScreens/Error404.html.html"; // Asegúrate de que este archivo existe
    }

    // Manejador de error 400 - Solicitud incorrecta
    @ExceptionHandler(ResponseStatusException.class)
    public String handleBadRequest(ResponseStatusException ex, Model model) {
        if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
            model.addAttribute("error", "400 - Solicitud incorrecta");
            model.addAttribute("message", "Hubo un problema con la solicitud.");
            return "errorScreens/Error400.html.html";
        }
        return "errorScreens/Error500.html.html";
    }

    // Manejador de error 500 - Error interno del servidor
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalServerError(Exception ex, Model model) {
        model.addAttribute("error", "500 - Error del servidor");
        model.addAttribute("message", "Ocurrió un error inesperado.");
        return "errorScreens/Error500.html";
    }
}
*/
