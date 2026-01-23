package com.vesta.web.controller;

import com.vesta.web.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/derechos")
public class DerechosController {

    @Autowired
    private ApiService apiService;

    // === DERECHOS RGPD ===
    
    @PostMapping("/api/solicitar-supresion")
    @ResponseBody
    public ResponseEntity<?> solicitarSupresion(@RequestBody Map<String, Object> request, HttpSession session) {
        String token = (String) session.getAttribute("token");
        
        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object resultado = apiService.solicitarSupresionDatos(token, request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al solicitar supresión: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/api/{endpoint}")
    @ResponseBody
    public ResponseEntity<?> solicitarDerecho(@PathVariable String endpoint, @RequestBody Map<String, Object> request, HttpSession session) {
        String token = (String) session.getAttribute("token");
        
        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object resultado = apiService.solicitarDerecho(token, endpoint, request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al procesar solicitud: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/api/mis-solicitudes/{userId}")
    @ResponseBody
    public ResponseEntity<?> obtenerMisSolicitudes(@PathVariable Long userId, HttpSession session) {
        String token = (String) session.getAttribute("token");
        
        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object solicitudes = apiService.obtenerSolicitudesUsuario(token, userId);
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al obtener solicitudes: " + e.getMessage() + "\"}");
        }
    }
}