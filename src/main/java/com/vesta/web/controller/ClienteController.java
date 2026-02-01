package com.vesta.web.controller;

import com.vesta.web.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");

        if (token == null) {
            return "redirect:/";
        }

        if ("ADMIN".equals(rol) || "ADMINISTRADOR".equals(rol)) {
            return "redirect:/admin/dashboard";
        }

        String nombre = (String) session.getAttribute("usuarioNombre");
        model.addAttribute("nombreUsuario", nombre != null ? nombre : "Cliente");
        model.addAttribute("usuarioEmail", session.getAttribute("usuarioEmail"));

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

        if ("ADMIN".equals(session.getAttribute("rol")) || "ADMINISTRADOR".equals(session.getAttribute("rol"))) {
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

        if ("ADMIN".equals(session.getAttribute("rol")) || "ADMINISTRADOR".equals(session.getAttribute("rol"))) {
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

        if ("ADMIN".equals(session.getAttribute("rol")) || "ADMINISTRADOR".equals(session.getAttribute("rol"))) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("nombreUsuario", session.getAttribute("usuarioNombre"));
        return "cliente/mis-polizas";
    }

    // === TPV VIRTUAL ===

    @GetMapping("/tpv-simulator")
    public String tpvSimulator(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return "redirect:/login-page";
        }

        if ("ADMIN".equals(session.getAttribute("rol")) || "ADMINISTRADOR".equals(session.getAttribute("rol"))) {
            return "redirect:/admin/dashboard";
        }

        // Obtener órdenes pendientes del usuario actual
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        java.util.List<java.util.Map<String, Object>> ordenesPendientes = apiService
                .obtenerOrdenesPendientesUsuario(token, usuarioId);

        // Preparar datos del carrito basados en órdenes pendientes
        java.util.Map<String, Object> cartData = new java.util.HashMap<>();
        java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
        double totalAmount = 0.0;

        if (ordenesPendientes != null && !ordenesPendientes.isEmpty()) {
            for (java.util.Map<String, Object> orden : ordenesPendientes) {
                @SuppressWarnings("unchecked")
                java.util.List<java.util.Map<String, Object>> ordenItems = (java.util.List<java.util.Map<String, Object>>) orden
                        .get("items");

                if (ordenItems != null) {
                    for (java.util.Map<String, Object> ordenItem : ordenItems) {
                        java.util.Map<String, Object> item = new java.util.HashMap<>();
                        item.put("seguroId", ordenItem.get("seguroId"));
                        item.put("cantidad", ordenItem.get("cantidad"));
                        item.put("nombre", ordenItem.get("nombreSeguro"));
                        item.put("precio", ordenItem.get("precioUnitario"));
                        item.put("subtotal", ordenItem.get("subtotal"));
                        items.add(item);

                        totalAmount += ((Number) ordenItem.get("subtotal")).doubleValue();
                    }
                }
            }
        }

        cartData.put("items", items);

        model.addAttribute("cartData", cartData);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreUsuario", session.getAttribute("usuarioNombre"));

        return "cliente/tpv-simulator";
    }

    @PostMapping("/api/checkout-tpv")
    @ResponseBody
    public ResponseEntity<?> checkoutTPV(@RequestBody Map<String, Object> request, HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }

        try {
            Object resultado = apiService.checkoutTPV(token, request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al procesar pago: " + e.getMessage()));
        }
    }

    @GetMapping("/api/tpv/tarjetas-prueba")
    @ResponseBody
    public ResponseEntity<?> getTarjetasPrueba(HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }

        try {
            Object tarjetas = apiService.getTarjetasPrueba(token);
            return ResponseEntity.ok(tarjetas);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al obtener tarjetas: " + e.getMessage()));
        }
    }

    // === API PROXY ENDPOINTS ===

    @GetMapping("/api/polizas")
    @ResponseBody
    public ResponseEntity<?> obtenerPolizasUsuario(HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            // Usar el ApiService para hacer la llamada a la API interna
            Object polizas = apiService.obtenerPolizasUsuario(token);
            return ResponseEntity.ok(polizas);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al obtener pólizas: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/api/productos")
    @ResponseBody
    public ResponseEntity<?> obtenerProductos() {
        try {
            // Los productos son públicos, no necesitan autenticación
            Object productos = apiService.getProductos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"Error al obtener productos: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/api/productos/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id) {
        try {
            Object producto = apiService.getProductoPorId(id);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al obtener producto: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/api/innovation/recommendation")
    @ResponseBody
    public ResponseEntity<?> obtenerRecomendacion(HttpSession session, @RequestParam(required = false) String email) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            // Usar el email de la sesión si no se proporciona
            if (email == null) {
                email = (String) session.getAttribute("usuarioEmail");
            }

            Object recomendacion = apiService.obtenerRecomendacionIA(token, email);
            return ResponseEntity.ok(recomendacion);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"Error al obtener recomendación: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/api/innovation/chat")
    @ResponseBody
    public ResponseEntity<?> chatIA(HttpSession session, @RequestBody Map<String, String> request) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object respuesta = apiService.chatIA(token, request.get("pregunta"));
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error en chat IA: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/api/polizas/contratar")
    @ResponseBody
    public ResponseEntity<?> contratarPoliza(HttpSession session, @RequestBody Map<String, Object> request) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object resultado = apiService.contratarPoliza(token, request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al contratar póliza: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/api/reportes/polizas/pdf")
    @ResponseBody
    public ResponseEntity<?> descargarReportePDF(HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            byte[] pdfBytes = apiService.generarReportePDF(token);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=Vesta_Resumen_Polizas.pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al generar reporte: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/api/siniestros")
    @ResponseBody
    public ResponseEntity<?> reportarSiniestro(
            HttpSession session,
            @RequestParam("polizaId") Long polizaId,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {

        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }

        try {
            Object resultado = apiService.reportarSiniestro(token, polizaId, descripcion, file);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al reportar siniestro: " + e.getMessage()));
        }
    }
}