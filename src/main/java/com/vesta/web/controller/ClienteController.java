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

        // Logging detallado para diagnóstico
        System.out.println("🔍 ClienteController.dashboard() - Verificando sesión:");
        System.out.println("   - Token: " + (token != null ? "✓ Presente" : "✗ NULL"));
        System.out.println("   - Rol: " + (rol != null ? rol : "✗ NULL"));
        System.out.println("   - Session ID: " + session.getId());

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

    // === NUEVO MÉTODO PARA CONFIGURACIÓN ===
    @GetMapping("/configuracion")
    public String configuracion(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        // Si no hay sesión, redirigir al login
        if (token == null) {
            return "redirect:/login-page";
        }

        // Pasamos datos básicos a la vista para el header
        model.addAttribute("nombreUsuario", session.getAttribute("usuarioNombre"));

        // IMPORTANTE: Pasamos el ID del usuario a la vista.
        // Esto es necesario para que el JavaScript del formulario de contraseña
        // sepa a qué endpoint de la API llamar (PUT /api/usuarios/{id}).
        model.addAttribute("usuarioId", session.getAttribute("usuarioId"));

        return "cliente/configuracion";
    }
}