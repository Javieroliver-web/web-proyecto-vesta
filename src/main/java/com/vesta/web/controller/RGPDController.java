package com.vesta.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RGPDController {

    @GetMapping("/privacidad")
    public String politicaPrivacidad() {
        return "legal/privacidad";
    }

    @GetMapping("/cookies")
    public String politicaCookies() {
        return "legal/cookies";
    }

    @GetMapping("/terminos")
    public String terminosCondiciones() {
        return "legal/terminos";
    }

    @GetMapping("/mis-datos")
    public String misDatos(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        
        if (token == null) {
            return "redirect:/?error=login_required";
        }

        String nombre = (String) session.getAttribute("usuarioNombre");
        model.addAttribute("nombreUsuario", nombre);
        
        return "legal/mis-datos";
    }
}