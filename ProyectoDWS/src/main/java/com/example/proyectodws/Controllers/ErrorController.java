package com.example.proyectodws.Controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@ControllerAdvice
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class})
    public ModelAndView handleBadRequest(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
        return new ModelAndView("Error400");
    }

    @ExceptionHandler(SecurityException.class)
    public ModelAndView handleUnauthorized(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        return new ModelAndView("Error401");
    }
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public ModelAndView handleNotFound(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
        return new ModelAndView("Error404");
    }

    @ExceptionHandler(IOException.class)
    public ModelAndView handleMovedPermanently(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY); // 301
        return new ModelAndView("Error300");
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleInternalServerError(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
        return new ModelAndView("Error500");
    }

}
