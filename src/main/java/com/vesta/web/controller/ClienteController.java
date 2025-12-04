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
        // Verificar si hay token en sesión
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");

        // CORRECCIÓN: El rol de la API es "USUARIO", no "cliente"
        if (token == null) {
            System.out.println("⚠️ No hay token en sesión, redirigiendo al login");
            return "redirect:/";
        }

        // Permitir acceso si el rol es USUARIO (no es ADMIN)
        if ("ADMIN".equals(rol)) {
            System.out.println("⚠️ Usuario con rol ADMIN intentando acceder al dashboard de cliente");
            return "redirect:/admin/dashboard";
        }

        // Recuperar nombre del usuario
        String nombre = (String) session.getAttribute("usuarioNombre");
        model.addAttribute("nombreUsuario", nombre != null ? nombre : "Cliente");

        System.out.println("✅ Acceso permitido al dashboard de cliente para: " + nombre);
        
        return "cliente/dashboard";
    }
}