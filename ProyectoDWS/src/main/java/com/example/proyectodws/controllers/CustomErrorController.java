package com.example.proyectodws.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorCode", "404");
                model.addAttribute("errorMessage", "Página no encontrada.");
                return "errorScreens/error404.html";
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorCode", "500");
                model.addAttribute("errorMessage", "Error interno del servidor.");
                return "errorScreens/error500.html";
            }
            else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                model.addAttribute("errorCode", "400");
                model.addAttribute("errorMessage", "Solicitud incorrecta.");
                return "errorScreens/error400.html";
            }
            else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                model.addAttribute("errorCode", "401");
                model.addAttribute("errorMessage", "No autorizado.");
                return "errorScreens/error401.html";
            }
            else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("errorCode", "403");
                model.addAttribute("errorMessage", "Acceso denegado.");
                return "errorScreens/error403.html";
            }
        }

        model.addAttribute("errorCode", "500");
        model.addAttribute("errorMessage", "Error interno del servidor.");
        return "errorScreens/error500.html"; // Otra página de error si es necesario
    }

}
