package com.vesta.web.controller;

import com.vesta.web.service.ApiService;
import java.util.List;
import java.util.Map;
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

        // === NUEVO: Cargar lista de usuarios (para pestaña usuarios) ===
        model.addAttribute("usuarios", apiService.obtenerTodosLosUsuarios(token));

        // --- ESTADÍSTICAS REALES ---
        java.util.Map<String, Object> stats = apiService.obtenerEstadisticas(token);

        // Procesar Ventas
        java.util.List<java.util.Map<String, Object>> ventas = (java.util.List<java.util.Map<String, Object>>) stats
                .getOrDefault("ventas", java.util.Collections.emptyList());
        java.util.List<String> ventasLabels = new java.util.ArrayList<>();
        java.util.List<Double> ventasData = new java.util.ArrayList<>();

        String[] meses = { "", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" };

        for (java.util.Map<String, Object> v : ventas) {
            Integer mesNum = (Integer) v.get("mes");
            if (mesNum != null && mesNum >= 1 && mesNum <= 12) {
                ventasLabels.add(meses[mesNum]);
                ventasData.add(Double.valueOf(v.get("total").toString()));
            }
        }

        // Si no hay datos, inicializar vacío para evitar error JS
        if (ventasLabels.isEmpty()) {
            ventasLabels.add("Sin Datos");
            ventasData.add(0.0);
        }

        model.addAttribute("chartVentasLabels", ventasLabels);
        model.addAttribute("chartVentasData", ventasData);

        // Procesar Siniestros
        java.util.List<java.util.Map<String, Object>> siniestros = (java.util.List<java.util.Map<String, Object>>) stats
                .getOrDefault("siniestros", java.util.Collections.emptyList());
        java.util.List<String> siniestrosLabels = new java.util.ArrayList<>();
        java.util.List<Long> siniestrosData = new java.util.ArrayList<>();

        for (java.util.Map<String, Object> s : siniestros) {
            siniestrosLabels.add((String) s.get("categoria"));
            siniestrosData.add(Long.valueOf(s.get("cantidad").toString()));
        }

        if (siniestrosLabels.isEmpty()) {
            siniestrosLabels.add("Sin Datos");
            siniestrosData.add(0L);
        }

        model.addAttribute("chartSiniestrosLabels", siniestrosLabels);
        model.addAttribute("chartSiniestrosData", siniestrosData);

        model.addAttribute("nombreUsuario", session.getAttribute("usuarioNombre"));

        return "admin/dashboard";
    }

    @GetMapping("/configuracion")
    public String configuracion(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");
        if (token == null || !"ADMIN".equals(rol))
            return "redirect:/";
        model.addAttribute("usuarioId", session.getAttribute("usuarioId"));
        return "admin/configuracion";
    }

    @GetMapping("/catalogo")
    public String catalogo(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");
        if (token == null || !"ADMIN".equals(rol))
            return "redirect:/";
        // Pasamos token para que el frontend pueda llamar a la API
        // (Aunque realmente el frontend usa th:inline="javascript" y session.token)
        return "admin/catalogo";
    }

    @GetMapping("/usuarios")
    public String usuarios(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");
        if (token == null || !"ADMIN".equals(rol))
            return "redirect:/";

        List<Map<String, Object>> usuarios = apiService.obtenerTodosLosUsuarios(token);

        // Procesar usuarios para calcular edad y manejar fechas correctamente
        for (Map<String, Object> u : usuarios) {
            try {
                // Calcular Edad
                if (u.get("fechaNacimiento") != null) {
                    String fechaNacStr = u.get("fechaNacimiento").toString();
                    java.time.LocalDate fechaNac = java.time.LocalDate.parse(fechaNacStr);
                    int edad = java.time.Period.between(fechaNac, java.time.LocalDate.now()).getYears();
                    u.put("edad", edad);
                    u.put("fechaNacimientoObj", fechaNac); // Guardar objeto para formateo si es necesario
                }

                // Convertir fechaCreacion a objeto si es String
                if (u.get("fechaCreacion") != null && u.get("fechaCreacion") instanceof String) {
                    String fechaCreacionStr = u.get("fechaCreacion").toString();
                    // Asumiendo formato ISO yyyy-MM-dd que es el default de LocalDate
                    // Si viene con hora, habrá que ajustar. Generalmente toString() de JSON es
                    // simple.
                    try {
                        u.put("fechaCreacionObj", java.time.LocalDate.parse(fechaCreacionStr.substring(0, 10)));
                    } catch (Exception e) {
                        // Si falla, dejamos el original
                    }
                }
            } catch (Exception e) {
                // Si falla algo, simplemente no mostramos el dato calculado
            }
        }

        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios";
    }

    @GetMapping("/polizas")
    public String polizas(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");
        if (token == null || !"ADMIN".equals(rol))
            return "redirect:/";

        model.addAttribute("polizas", apiService.obtenerTodasLasPolizas(token));
        return "admin/polizas";
    }

    @GetMapping("/auditoria")
    public String auditoria(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");
        if (token == null || !"ADMIN".equals(rol))
            return "redirect:/";

        model.addAttribute("logs", apiService.obtenerLogsAuditoria(token));
        return "admin/auditoria";
    }
}