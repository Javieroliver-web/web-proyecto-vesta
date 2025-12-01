package com.vesta.web.controller;

import com.vesta.web.dto.AuthResponseDTO; // Nombre nuevo
import com.vesta.web.service.ApiService;  // Paquete en singular
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/")
    public String showLoginForm(HttpSession session) {
        if (session.getAttribute("token") != null) {
            String rol = (String) session.getAttribute("rol");
            return "admin".equals(rol) ? "redirect:/admin/dashboard" : "redirect:/cliente/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, 
                               @RequestParam String password, 
                               HttpSession session, 
                               Model model) {
        try {
            // Usamos la clase nueva AuthResponseDTO
            AuthResponseDTO response = apiService.login(email, password);

            session.setAttribute("token", response.getToken());
            session.setAttribute("rol", response.getRol());
            session.setAttribute("usuarioNombre", response.getNombre());

            if ("admin".equals(response.getRol())) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/cliente/dashboard";
            }

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}