package com.vesta.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        // Si el usuario ya está logueado, redirigir a su dashboard correspondiente
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");

        if (token != null) {
            if ("ADMIN".equals(rol)) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/cliente/dashboard";
            }
        }

        // Si no hay sesión, mostrar la Landing Page pública
        return "index";
    }
}