package com.vesta.web.controller;

import com.vesta.web.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/")
    public String home(HttpSession session) {
        // Si el usuario ya está logueado, redirigir a su dashboard correspondiente
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");

        if (token != null) {
            if ("ADMIN".equals(rol)) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/cliente/dashboard";
            }
        }

        // Si no hay sesión, mostrar la Landing Page pública
        return "index";
    }

    /**
     * Página para seleccionar método de recuperación
     */
    @GetMapping("/select-recovery-method")
    public String selectRecoveryMethod() {
        return "select-recovery-method";
    }

    /**
     * Página para solicitar recuperación de contraseña (legacy)
     */
    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    /**
     * Endpoint para verificar métodos de recuperación disponibles
     */
    @PostMapping("/check-recovery-methods")
    @ResponseBody
    public ResponseEntity<?> checkRecoveryMethods(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            Map<String, Object> methods = apiService.checkRecoveryMethods(email);

            return ResponseEntity.ok(methods);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Endpoint para procesar solicitud de recuperación de contraseña
     */
    @PostMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<?> processForgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String method = request.get("method");

            if (email == null || email.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "El correo electrónico es obligatorio");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Si no se especifica método, usar email por defecto
            if (method == null || method.trim().isEmpty()) {
                method = "email";
            }

            String message = apiService.forgotPassword(email, method);

            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Página para resetear contraseña con código
     */
    @GetMapping("/reset-password")
    public String resetPassword() {
        return "reset-password";
    }

    /**
     * Endpoint para procesar reset de contraseña
     */
    @PostMapping("/reset-password")
    @ResponseBody
    public ResponseEntity<?> processResetPassword(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            String newPassword = request.get("newPassword");

            if (token == null || token.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "El código de verificación es obligatorio");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "La nueva contraseña es obligatoria");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            String message = apiService.resetPassword(token, newPassword);

            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Página de Preguntas Frecuentes
     */
    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    /**
     * Página de Seguros Disponibles
     */
    @GetMapping("/seguros")
    public String seguros() {
        return "seguros";
    }
}
