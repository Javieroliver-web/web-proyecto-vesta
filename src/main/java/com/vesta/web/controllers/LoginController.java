package com.vesta.web.controllers;

import com.vesta.web.dto.LoginResponse;
import com.vesta.web.services.ApiService;
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

    // 1. Mostrar el formulario de Login (GET)
    @GetMapping("/")
    public String showLoginForm(HttpSession session) {
        // Si ya está logueado, redirigir al dashboard correspondiente
        if (session.getAttribute("token") != null) {
            String rol = (String) session.getAttribute("rol");
            return "admin".equals(rol) ? "redirect:/admin/dashboard" : "redirect:/cliente/dashboard";
        }
        return "login"; // Busca login.html en templates
    }

    // 2. Procesar el Login (POST)
    @PostMapping("/login")
    public String processLogin(@RequestParam String email, 
                               @RequestParam String password, 
                               HttpSession session, 
                               Model model) {
        try {
            // Llamamos a la API
            LoginResponse response = apiService.login(email, password);

            // Guardamos la sesión
            session.setAttribute("token", response.getToken());
            session.setAttribute("rol", response.getRol());
            session.setAttribute("usuarioNombre", response.getNombre());

            // Redirigimos según el rol
            if ("admin".equals(response.getRol())) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/cliente/dashboard";
            }

        } catch (RuntimeException e) {
            // Si falla, volvemos al login con mensaje de error
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    // 3. Cerrar Sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}