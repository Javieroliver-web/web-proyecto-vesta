package com.vesta.web.controller;

import com.vesta.web.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminApiController {

    @Autowired
    private ApiService apiService;

    // === GESTIÓN DE USUARIOS ===

    @GetMapping("/api/usuarios")
    @ResponseBody
    public ResponseEntity<?> obtenerTodosLosUsuarios(HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object usuarios = apiService.obtenerTodosLosUsuarios(token);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al obtener usuarios: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/api/usuarios/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Map<String, Object> updates,
            HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            // Usar método genérico para soportar cambio de rol y estado
            apiService.actualizarUsuario(token, id, updates);
            return ResponseEntity.ok("{\"message\":\"Usuario actualizado correctamente\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"Error al actualizar usuario: " + e.getMessage() + "\"}");
        }
    }

    // === GESTIÓN DE SINIESTROS ===

    @GetMapping("/api/siniestros")
    @ResponseBody
    public ResponseEntity<?> obtenerSiniestros(HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object siniestros = apiService.obtenerSiniestros(token);
            return ResponseEntity.ok(siniestros);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"Error al obtener siniestros: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/api/siniestros/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> actualizarEstadoSiniestro(@PathVariable Long id, @RequestBody Map<String, Object> updates,
            HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object resultado = apiService.actualizarEstadoSiniestro(token, id, updates);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"Error al actualizar siniestro: " + e.getMessage() + "\"}");
        }
    }

    // === ESTADÍSTICAS ===

    @GetMapping("/api/estadisticas")
    @ResponseBody
    public ResponseEntity<?> obtenerEstadisticas(HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object estadisticas = apiService.obtenerEstadisticas(token);
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"Error al obtener estadísticas: " + e.getMessage() + "\"}");
        }
    }

    // === GESTIÓN DE PRODUCTOS ===

    @GetMapping("/api/productos")
    @ResponseBody
    public ResponseEntity<?> obtenerProductosAdmin(HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object productos = apiService.getProductos(); // Los productos son públicos
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"Error al obtener productos: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/api/productos")
    @ResponseBody
    public ResponseEntity<?> crearProducto(@RequestBody Map<String, Object> productData, HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object nuevoProducto = apiService.crearProducto(token, productData);
            return ResponseEntity.ok(nuevoProducto);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"Error al crear producto: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/api/productos/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Map<String, Object> productData,
            HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object productoActualizado = apiService.actualizarProducto(token, id, productData);
            return ResponseEntity.ok(productoActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"Error al actualizar producto: " + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/api/productos/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            apiService.eliminarProducto(token, id);
            return ResponseEntity.ok("{\"message\":\"Producto eliminado correctamente\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"Error al eliminar producto: " + e.getMessage() + "\"}");
        }
    }

    // === GESTIÓN DE ÓRDENES/VENTAS ===

    @GetMapping("/api/ordenes")
    @ResponseBody
    public ResponseEntity<?> obtenerTodasLasOrdenes(HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object ordenes = apiService.obtenerTodasLasOrdenes(token);
            return ResponseEntity.ok(ordenes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al obtener órdenes: " + e.getMessage() + "\"}");
        }
    }

    // === GESTIÓN DE PÓLIZAS ===

    @GetMapping("/api/polizas")
    @ResponseBody
    public ResponseEntity<?> obtenerTodasLasPolizas(HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object polizas = apiService.obtenerTodasLasPolizas(token);
            return ResponseEntity.ok(polizas);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al obtener pólizas: " + e.getMessage() + "\"}");
        }
    }

    // === AUDITORÍA ===

    @GetMapping("/api/auditoria")
    @ResponseBody
    public ResponseEntity<?> obtenerLogsAuditoria(HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object logs = apiService.obtenerLogsAuditoria(token);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"error\":\"Error al obtener logs de auditoría: " + e.getMessage() + "\"}");
        }
    }
}