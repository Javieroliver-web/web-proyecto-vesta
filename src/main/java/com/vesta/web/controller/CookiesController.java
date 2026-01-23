package com.vesta.web.controller;

import com.vesta.web.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/cookies")
public class CookiesController {

    @Autowired
    private ApiService apiService;

    // === GESTIÓN DE CONSENTIMIENTO DE COOKIES ===
    
    @PostMapping("/api/consentimiento")
    @ResponseBody
    public ResponseEntity<?> guardarConsentimiento(@RequestBody Map<String, Object> request, HttpSession session) {
        String token = (String) session.getAttribute("token");
        
        if (token == null) {
            return ResponseEntity.status(401).body("{\"error\":\"No autenticado\"}");
        }

        try {
            Object resultado = apiService.guardarConsentimientoCookies(token, request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al guardar consentimiento: " + e.getMessage() + "\"}");
        }
    }
}