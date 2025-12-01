package com.vesta.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // 1. SEGURIDAD: Verificar si hay token en sesión
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");

        if (token == null || !"cliente".equals(rol)) {
            return "redirect:/"; // Si no es cliente o no hay sesión, volver al login
        }

        // 2. DATOS: Recuperar nombre para saludar
        String nombre = (String) session.getAttribute("usuarioNombre");
        model.addAttribute("nombreUsuario", nombre);

        // TODO: Aquí llamaremos a la API para traer la lista de seguros (Próximo paso)

        // 3. VISTA: Mostrar el HTML
        return "cliente/dashboard";
    }
}