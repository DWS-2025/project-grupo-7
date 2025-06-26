package com.example.proyectodws.configuration;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.proyectodws.dto.UserDTO;
import com.example.proyectodws.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.example.proyectodws.controllers")
public class UserModelAttributes {

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            UserDTO user = userService.getUserByUsername(principal.getName());

            if (user != null) {
                model.addAttribute("logged", true);
                model.addAttribute("userName", principal.getName());
                model.addAttribute("userId", user.id());
                model.addAttribute("admin", request.isUserInRole("ADMIN"));
            } else {
                SecurityContextHolder.clearContext();
                request.getSession().invalidate();
                model.addAttribute("logged", false);
                model.addAttribute("userName", "");
                model.addAttribute("userId", 0);
                model.addAttribute("admin", false);
            }
        } else {
            model.addAttribute("logged", false);
            model.addAttribute("userName", "");
            model.addAttribute("userId", 0);
            model.addAttribute("admin", false);
        }
    }

}
