package com.vesta.web.controller;

import com.vesta.web.dto.AuthResponseDTO;
import com.vesta.web.service.ApiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final ApiService apiService;

    // CAMBIO: Ahora la página de login se sirve en /login-page
    // La raíz "/" queda libre para el HomeController (Landing Page)
    @GetMapping("/login-page")
    public String showLoginForm(HttpSession session, org.springframework.ui.Model model) {
        // Si ya hay sesión activa, redirigir al dashboard correspondiente
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");

        if (token != null) {
            // Redirigir según el rol del usuario
            if ("ADMIN".equals(rol) || "ADMINISTRADOR".equals(rol) || "OWNER".equals(rol)) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/cliente/dashboard";
            }
        }

        // 2FA Handling for Social Login
        String temp2faToken = (String) session.getAttribute("temp2faToken");
        if (temp2faToken != null) {
            logger.info("🔑 Token 2FA temporal encontrado - activando modal en vista.");
            model.addAttribute("tempToken", temp2faToken);
            session.removeAttribute("temp2faToken");
        }

        return "login";
    }

    // Endpoint para procesar el login via AJAX
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> processLogin(@RequestBody LoginRequest request, HttpSession session, HttpServletRequest httpRequest) {
        try {
            logger.info("🔐 Procesando login para: {}", request.getEmail());

            // Validar que los campos no estén vacíos
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "El correo electrónico es obligatorio");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "La contraseña es obligatoria");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Intentar hacer login
            AuthResponseDTO response = apiService.login(request.getEmail(), request.getPassword());

            // 2FA CHECK
            if (response.isRequires2fa()) {
                // Caso 2FA: Devolver token temporal y flag
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("requires2fa", true);
                responseData.put("tempToken", response.getToken());
                // No creamos sesión HTTP completa aún
                return ResponseEntity.ok(responseData);
            }

            // Guardar en sesión HTTP
            session.setAttribute("token", response.getToken());
            session.setAttribute("rol", response.getRol());
            session.setAttribute("usuarioNombre", response.getNombre());

            // Guardar el ID del usuario en la sesión para usarlo en RGPD
            session.setAttribute("usuarioId", response.getId());
            session.setAttribute("usuarioEmail", request.getEmail());

            // Logging detallado para diagnóstico
            logger.info("✅ Login exitoso. Sesión creada para: {} (ID: {})", response.getNombre(), response.getId());

            // Determinar URL de redirección según el rol
            String redirectUrl;
            String ctxPath = httpRequest.getContextPath();
            if ("ADMIN".equals(response.getRol()) || "ADMINISTRADOR".equals(response.getRol())
                    || "OWNER".equals(response.getRol())) {
                redirectUrl = ctxPath + "/admin/dashboard";
            } else {
                redirectUrl = ctxPath + "/cliente/dashboard";
            }

            // Crear respuesta con URL de redirección
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("requires2fa", false);
            responseData.put("redirectUrl", redirectUrl);

            return ResponseEntity.ok(responseData);

        } catch (RuntimeException e) {
            logger.error("❌ Error en login para {}: {} (Causa: {})", request.getEmail(), e.getMessage(),
                    e.getClass().getSimpleName());

            // Devolver error en formato JSON
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            logger.error("❌ Error inesperado en login", e);

            // Error genérico
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al procesar el login. Verifica que la API esté funcionando.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/login/verify-2fa")
    @ResponseBody
    public ResponseEntity<?> verify2fa(@RequestBody Map<String, String> request, HttpSession session, HttpServletRequest httpRequest) {
        try {
            String tempToken = request.get("tempToken");
            String code = request.get("code");

            AuthResponseDTO response = apiService.verify2fa(tempToken, code);

            // Guardar en sesión HTTP
            session.setAttribute("token", response.getToken());
            session.setAttribute("rol", response.getRol());
            session.setAttribute("usuarioNombre", response.getNombre());
            session.setAttribute("usuarioId", response.getId());

            // Determinar URL de redirección
            String ctxPath = httpRequest.getContextPath();
            String redirectUrl = ("ADMIN".equals(response.getRol()) || "ADMINISTRADOR".equals(response.getRol())
                    || "OWNER".equals(response.getRol())) ? ctxPath + "/admin/dashboard" : ctxPath + "/cliente/dashboard";

            Map<String, Object> result = new HashMap<>();
            result.put("redirectUrl", redirectUrl);

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    // NEW EXPLICIT ENDPOINT FOR SOCIAL LOGIN MOCK
    @PostMapping("/login/mock-social-session")
    @ResponseBody
    public ResponseEntity<?> createMockSocialSession(@RequestBody Map<String, String> request, HttpSession session, HttpServletRequest httpRequest) {
        String provider = request.get("provider");
        logger.info("🎭 Creando sesión mock para: {}", provider);

        // Simular un token y usuario de demo
        session.setAttribute("token", "mock-token-" + provider);
        session.setAttribute("rol", "USUARIO");
        session.setAttribute("usuarioNombre", "Usuario Demo " + provider);
        session.setAttribute("usuarioId", 999L);
        session.setAttribute("usuarioEmail", "demo-" + provider + "@vesta.com");

        Map<String, String> result = new HashMap<>();
        result.put("redirectUrl", httpRequest.getContextPath() + "/cliente/dashboard");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        logger.info("🚪 Cerrando sesión");
        session.invalidate();
        return "redirect:/"; // Al salir, volvemos a la Landing Page
    }

    // Endpoint para solicitar reenvío de confirmación
    @PostMapping("/resend-confirmation")
    @ResponseBody
    public ResponseEntity<?> resendConfirmation(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "El email es obligatorio");
                return ResponseEntity.badRequest().body(error);
            }

            String message = apiService.resendConfirmation(email);

            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // === CONFIRMACIÓN DE CUENTA ===

    @GetMapping("/api/auth/confirm-account")
    public String confirmAccount(@RequestParam String token) {
        try {
            logger.info("🔐 Procesando confirmación de cuenta con token: {}", token);

            // Llamar a la API para confirmar la cuenta
            apiService.confirmarCuenta(token);

            logger.info("✅ Cuenta confirmada exitosamente");
            return "redirect:/login-page?confirmed=true";

        } catch (Exception e) {
            logger.error("❌ Error al confirmar cuenta: {}", e.getMessage());
            return "redirect:/login-page?error=invalid_token";
        }
    }

    // DTO interno para recibir el JSON del frontend
    public static class LoginRequest {
        private String correoElectronico;
        private String contrasena;

        public String getEmail() {
            return correoElectronico;
        }

        public void setEmail(String email) {
            this.correoElectronico = email;
        }

        public String getPassword() {
            return contrasena;
        }

        public void setPassword(String password) {
            this.contrasena = password;
        }

        public String getCorreoElectronico() {
            return correoElectronico;
        }

        public void setCorreoElectronico(String correoElectronico) {
            this.correoElectronico = correoElectronico;
        }

        public String getContrasena() {
            return contrasena;
        }

        public void setContrasena(String contrasena) {
            this.contrasena = contrasena;
        }
    }
}