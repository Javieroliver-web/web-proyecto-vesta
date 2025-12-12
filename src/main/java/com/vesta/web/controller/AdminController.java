package com.vesta.web.controller;

import com.vesta.web.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");

        // Seguridad: Solo ADMIN puede entrar
        if (token == null || !"ADMIN".equals(rol)) {
            return "redirect:/";
        }

        // Obtener datos reales de la API
        model.addAttribute("ordenes", apiService.obtenerTodasLasOrdenes(token));
        model.addAttribute("solicitudes", apiService.obtenerSolicitudesRGPD(token));
        
        // === NUEVO: Cargar lista de siniestros ===
        model.addAttribute("siniestros", apiService.obtenerSiniestros(token));
        
        model.addAttribute("nombreUsuario", session.getAttribute("usuarioNombre"));

        return "admin/dashboard";
    }
}