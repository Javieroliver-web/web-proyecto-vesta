package com.vesta.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");

        if (token == null) {
            return "redirect:/";
        }

        if ("ADMIN".equals(rol)) {
            return "redirect:/admin/dashboard";
        }

        String nombre = (String) session.getAttribute("usuarioNombre");
        model.addAttribute("nombreUsuario", nombre != null ? nombre : "Cliente");

        return "cliente/dashboard";
    }

    @GetMapping("/configuracion")
    public String configuracion(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return "redirect:/login-page";
        }

        model.addAttribute("nombreUsuario", session.getAttribute("usuarioNombre"));
        model.addAttribute("usuarioId", session.getAttribute("usuarioId"));

        return "cliente/configuracion";
    }

    // === MARKETPLACE ===

    @GetMapping("/marketplace")
    public String marketplace(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return "redirect:/";
        }

        if ("ADMIN".equals(session.getAttribute("rol"))) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("nombreUsuario", session.getAttribute("usuarioNombre"));
        return "cliente/marketplace";
    }

    @GetMapping("/producto/{id}")
    public String productoDetalle(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return "redirect:/";
        }

        if ("ADMIN".equals(session.getAttribute("rol"))) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("nombreUsuario", session.getAttribute("usuarioNombre"));
        model.addAttribute("productoId", id);
        return "cliente/producto-detalle";
    }

    @GetMapping("/mis-polizas")
    public String misPolizas(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return "redirect:/";
        }

        if ("ADMIN".equals(session.getAttribute("rol"))) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("nombreUsuario", session.getAttribute("usuarioNombre"));
        return "cliente/mis-polizas";
    }
}