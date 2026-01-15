package com.vesta.web.controller;

import com.vesta.web.dto.AuthResponseDTO;
import com.vesta.web.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequiredArgsConstructor
public class OAuthController {

    private static final Logger logger = LoggerFactory.getLogger(OAuthController.class);
    private final ApiService apiService;

    @GetMapping("/oauth2/success-handler")
    public String handleOAuthSuccess(@AuthenticationPrincipal OAuth2User principal, HttpSession session) {
        if (principal == null) {
            return "redirect:/login-page?error=oauth_failure";
        }

        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");

        // Identificar proveedor (simplificado, podría mejorarse)
        // Google usa "sub", Apple "sub" también, pero atributos varían
        String provider = "google"; // Default a Google por ahora, se puede refinar
        if (principal.getAttributes().containsKey("iss")
                && principal.getAttribute("iss").toString().contains("apple")) {
            provider = "apple";
        }

        logger.info("✅ OAuth Login exitoso para: {}", email);

        try {
            // Llamar a la API de Vesta para obtener el JWT real
            // Nota: Se asume que existe un método socialLogin en ApiService
            AuthResponseDTO response = apiService.socialLogin(email, name, provider);

            // Guardar sesión Vesta
            session.setAttribute("token", response.getToken());
            session.setAttribute("rol", response.getRol());
            session.setAttribute("usuarioNombre", response.getNombre());
            session.setAttribute("usuarioId", response.getId());
            session.setAttribute("usuarioEmail", email);

            return "redirect:/cliente/dashboard";

        } catch (Exception e) {
            logger.error("Error en login social backend: {}", e.getMessage());
            return "redirect:/login-page?error=social_backend_error";
        }
    }
}
