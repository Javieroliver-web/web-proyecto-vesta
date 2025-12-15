package com.vesta.web.controller;

import com.vesta.web.dto.AuthResponseDTO;
import com.vesta.web.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private ApiService apiService;

    // CAMBIO: Ahora la página de login se sirve en /login-page
    // La raíz "/" queda libre para el HomeController (Landing Page)
    @GetMapping("/login-page")
    public String showLoginForm(HttpSession session) {
        // Si ya hay sesión activa, redirigir al dashboard correspondiente
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");

        if (token != null) {
            // Redirigir según el rol del usuario
            if ("ADMIN".equals(rol)) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/cliente/dashboard";
            }
        }

        return "login";
    }

    // Endpoint para procesar el login via AJAX
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> processLogin(@RequestBody LoginRequest request, HttpSession session) {
        try {
            System.out.println("🔐 Procesando login para: " + request.getEmail());

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

            // Guardar en sesión HTTP
            session.setAttribute("token", response.getToken());
            session.setAttribute("rol", response.getRol());
            session.setAttribute("usuarioNombre", response.getNombre());

            // Guardar el ID del usuario en la sesión para usarlo en RGPD
            session.setAttribute("usuarioId", response.getId());

            // Logging detallado para diagnóstico
            System.out.println(
                    "✅ Login exitoso. Sesión creada para: " + response.getNombre() + " (ID: " + response.getId() + ")");
            System.out.println("📋 Datos guardados en sesión:");
            System.out.println("   - Token: " + (response.getToken() != null ? "✓ Presente" : "✗ NULL"));
            System.out.println("   - Rol: " + response.getRol());
            System.out.println("   - Nombre: " + response.getNombre());
            System.out.println("   - ID: " + response.getId());

            // Determinar URL de redirección según el rol
            String redirectUrl;
            if ("ADMIN".equals(response.getRol())) {
                redirectUrl = "/admin/dashboard";
            } else {
                redirectUrl = "/cliente/dashboard";
            }

            System.out.println("🔀 URL de redirección: " + redirectUrl);

            // Crear respuesta con URL de redirección
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", response.getToken());
            responseData.put("rol", response.getRol());
            responseData.put("nombre", response.getNombre());
            responseData.put("id", response.getId());
            responseData.put("redirectUrl", redirectUrl);

            return ResponseEntity.ok(responseData);

        } catch (RuntimeException e) {
            System.err.println("❌ Error en login: " + e.getMessage());

            // Devolver error en formato JSON
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();

            // Error genérico
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al procesar el login. Verifica que la API esté funcionando.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        System.out.println("🚪 Cerrando sesión");
        session.invalidate();
        return "redirect:/"; // Al salir, volvemos a la Landing Page
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