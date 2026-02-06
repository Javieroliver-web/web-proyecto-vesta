package com.vesta.web.controller;

import com.vesta.web.service.ApiService;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private ApiService apiService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");

        // Seguridad: Solo ADMIN u OWNER pueden entrar
        if (token == null || (!"ADMIN".equals(rol) && !"ADMINISTRADOR".equals(rol) && !"OWNER".equals(rol))) {
            return "redirect:/";
        }

        // Obtener datos reales de la API
        model.addAttribute("ordenes", apiService.obtenerTodasLasOrdenes(token));
        model.addAttribute("solicitudes", apiService.obtenerSolicitudesRGPD(token));

        // === NUEVO: Cargar lista de usuarios (para pestaña usuarios) ===
        model.addAttribute("usuarios", apiService.obtenerTodosLosUsuarios(token));

        // --- ESTADÍSTICAS REALES ---
        java.util.Map<String, Object> stats = apiService.obtenerEstadisticas(token);

        // Procesar Ventas
        @SuppressWarnings("unchecked")
        java.util.List<java.util.Map<String, Object>> ventas = (java.util.List<java.util.Map<String, Object>>) stats
                .getOrDefault("ventas", java.util.Collections.emptyList());
        java.util.List<String> ventasLabels = new java.util.ArrayList<>();
        java.util.List<Double> ventasData = new java.util.ArrayList<>();

        String[] meses = { "", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" };

        for (java.util.Map<String, Object> v : ventas) {
            Object mesObj = v.get("mes");
            Object totalObj = v.get("total");

            Integer mesNum = null;
            if (mesObj instanceof Number) {
                mesNum = ((Number) mesObj).intValue();
            } else if (mesObj != null) {
                try {
                    mesNum = Integer.parseInt(mesObj.toString());
                } catch (Exception e) {
                }
            }

            if (mesNum != null && mesNum >= 1 && mesNum <= 12) {
                ventasLabels.add(meses[mesNum]);

                Double totalVal = 0.0;
                if (totalObj instanceof Number) {
                    totalVal = ((Number) totalObj).doubleValue();
                } else if (totalObj != null) {
                    try {
                        totalVal = Double.parseDouble(totalObj.toString());
                    } catch (Exception e) {
                    }
                }
                ventasData.add(totalVal);
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
        @SuppressWarnings("unchecked")
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
        if (token == null || (!"ADMIN".equals(rol) && !"ADMINISTRADOR".equals(rol) && !"OWNER".equals(rol)))
            return "redirect:/";
        model.addAttribute("usuarioId", session.getAttribute("usuarioId"));
        return "admin/configuracion";
    }

    @GetMapping("/catalogo")
    public String catalogo(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");
        if (token == null || (!"ADMIN".equals(rol) && !"ADMINISTRADOR".equals(rol) && !"OWNER".equals(rol)))
            return "redirect:/";
        // Pasamos token para que el frontend pueda llamar a la API
        // (Aunque realmente el frontend usa th:inline="javascript" y session.token)
        return "admin/catalogo";
    }

    @GetMapping("/usuarios")
    public String usuarios(HttpSession session, Model model,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String keyword,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String role,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String status) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");

        if (token == null || (!"ADMIN".equals(rol) && !"ADMINISTRADOR".equals(rol) && !"OWNER".equals(rol))) {
            return "redirect:/";
        }

        // Validar y normalizar página
        if (page < 0)
            page = 0;

        // Obtener página de usuarios (size 10 por defecto) con filtros
        Map<String, Object> pageData = apiService.obtenerUsuariosPaginados(token, page, 10, keyword, role, status);
        logger.debug("DEBUG ADMIN: Received pageData for users: {}", pageData);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> usuarios = (List<Map<String, Object>>) pageData.getOrDefault("content", List.of());
        logger.debug("DEBUG ADMIN: Users list size: {}", usuarios.size());

        // Safe casting for numbers using Number to avoid ClassCastException
        Number totalPagesNum = (Number) pageData.getOrDefault("totalPages", 0);
        int totalPages = totalPagesNum.intValue();

        Number numberNum = (Number) pageData.getOrDefault("number", 0);
        int currentPage = numberNum.intValue();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);

        // Preservar parámetros de filtro en el modelo
        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);
        model.addAttribute("status", status);

        // Procesar usuarios para calcular edad y manejar fechas correctamente
        for (Map<String, Object> u : usuarios) {
            try {
                // Calcular Edad
                if (u.get("fechaNacimiento") != null) {
                    try {
                        String fechaNacStr = u.get("fechaNacimiento").toString();
                        if (fechaNacStr.length() >= 10) {
                            java.time.LocalDate fechaNac = java.time.LocalDate.parse(fechaNacStr.substring(0, 10));
                            int edad = java.time.Period.between(fechaNac, java.time.LocalDate.now()).getYears();
                            u.put("edad", edad);
                            u.put("fechaNacimientoObj", fechaNac);
                        }
                    } catch (Exception ex) {
                        // Ignorar error de parseo de fecha nacimiento
                    }
                }

                // Ensure keys exist to prevent EL1008E
                if (!u.containsKey("edad"))
                    u.put("edad", null);
                if (!u.containsKey("fechaNacimientoObj"))
                    u.put("fechaNacimientoObj", null);
                if (!u.containsKey("fechaCreacionObj"))
                    u.put("fechaCreacionObj", null);

                // Convertir fechaCreacion a objeto si es String
                if (u.get("fechaCreacion") != null) {
                    try {
                        if (u.get("fechaCreacion") instanceof String) {
                            String fechaCreacionStr = u.get("fechaCreacion").toString();
                            // Intentar parsear ISO (yyyy-MM-dd)
                            if (fechaCreacionStr.length() >= 10) {
                                u.put("fechaCreacionObj", java.time.LocalDate.parse(fechaCreacionStr.substring(0, 10)));
                            }
                        } else if (u.get("fechaCreacion") instanceof java.util.List) {
                            // Si viene como lista [yyyy, mm, dd...] (común en JSON de LocalTime)
                            java.util.List<?> dateList = (java.util.List<?>) u.get("fechaCreacion");
                            if (dateList.size() >= 3) {
                                int year = Integer.parseInt(dateList.get(0).toString());
                                int month = Integer.parseInt(dateList.get(1).toString());
                                int day = Integer.parseInt(dateList.get(2).toString());
                                u.put("fechaCreacionObj", java.time.LocalDate.of(year, month, day));
                            }
                        }
                    } catch (Exception e) {
                        // Si falla, dejamos el original
                    }
                }
            } catch (Exception e) {
                // Si falla algo global en el loop, continuar
            }
        }

        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios";
    }

    @GetMapping("/polizas")
    public String polizas(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");
        if (token == null || (!"ADMIN".equals(rol) && !"ADMINISTRADOR".equals(rol) && !"OWNER".equals(rol)))
            return "redirect:/";

        model.addAttribute("polizas", apiService.obtenerTodasLasPolizas(token));
        return "admin/polizas";
    }

    @GetMapping("/auditoria")
    public String auditoria(HttpSession session, Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String search) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");
        if (token == null || (!"ADMIN".equals(rol) && !"ADMINISTRADOR".equals(rol) && !"OWNER".equals(rol)))
            return "redirect:/";

        Map<String, Object> pageData = apiService.obtenerLogsAuditoria(token, page, search);
        model.addAttribute("logs", pageData.get("content"));
        model.addAttribute("search", search);

        // Pagination logic
        Number totalPagesNum = (Number) pageData.getOrDefault("totalPages", 0);
        int totalPages = totalPagesNum.intValue();
        Number numberNum = (Number) pageData.getOrDefault("number", 0);
        int currentPage = numberNum.intValue();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);

        return "admin/auditoria";
    }

    @GetMapping("/siniestros")
    public String siniestros(HttpSession session, Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");
        if (token == null || (!"ADMIN".equals(rol) && !"ADMINISTRADOR".equals(rol) && !"OWNER".equals(rol)))
            return "redirect:/";

        Map<String, Object> pageData = apiService.obtenerSiniestros(token, page, search, estado);
        logger.debug("DEBUG ADMIN: Received pageData for siniestros: {}", pageData);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> contentList = (List<Map<String, Object>>) pageData.getOrDefault("content", List.of());
        logger.debug("DEBUG ADMIN: Siniestros list size: {}", contentList.size());

        model.addAttribute("siniestros", contentList);
        model.addAttribute("search", search);
        model.addAttribute("estado", estado);

        // Pagination logic
        Number totalPagesNum = (Number) pageData.getOrDefault("totalPages", 0);
        int totalPages = totalPagesNum.intValue();
        Number numberNum = (Number) pageData.getOrDefault("number", 0);
        int currentPage = numberNum.intValue();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);

        return "admin/siniestros";
    }

    // === API ENDPOINTS ===

    @DeleteMapping("/api/usuarios/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("message", "No autenticado"));
        }

        try {
            apiService.eliminarUsuario(token, id);
            return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error al eliminar usuario: " + e.getMessage()));
        }
    }

    @PostMapping("/api/auditoria/exportar")
    @ResponseBody
    @SuppressWarnings("null")
    public ResponseEntity<String> exportarLogs(@RequestBody Map<String, String> payload, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        try {
            String email = payload.get("email");
            String content = apiService.exportarLogsUsuario(token, email);
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"logs_" + email + ".txt\"")
                    .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                    .body(content);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al exportar logs: " + e.getMessage());
        }
    }

    @PostMapping("/debug/seed")
    @ResponseBody
    public ResponseEntity<?> generarDatosPrueba(HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("message", "No autenticado"));
        }
        try {
            Map<String, Object> result = apiService.generarDatosPrueba(token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}