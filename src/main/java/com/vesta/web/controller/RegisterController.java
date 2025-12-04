package com.vesta.web.controller;

import com.vesta.web.dto.RegisterDTO;
import com.vesta.web.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RegisterController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> processRegister(@RequestBody RegisterDTO request) {
        try {
            // Validaciones básicas
            if (!request.isAceptaTerminos() || !request.isAceptaPrivacidad()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Debes aceptar los Términos y la Política de Privacidad."));
            }
            
            // Llamada a la API
            apiService.registrar(request);
            
            return ResponseEntity.ok(Map.of("message", "Registro exitoso"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}