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
        logger.info("🔵 Entrando a handleOAuthSuccess");

        if (principal == null) {
            logger.error("❌ Principal es NULL en success handler");
            return "redirect:/login-page?error=oauth_failure_null_principal";
        }

        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        logger.info("👤 Principal recibido: Email={}, Name={}", email, name);

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
            logger.info("📡 Llamando a ApiService.socialLogin...");
            AuthResponseDTO response = apiService.socialLogin(email, name, provider);
            logger.info("⬇️ Respuesta de API recibida.{}",
                    response.getToken() != null ? " Token generado." : " Token NULO (Verificación pendiente).");

            if (response.getToken() == null) {
                logger.info("ℹ️ Cuenta creada o inactiva. Redirigiendo a verificación.");
                return "redirect:/login-page?error=verification_sent&email=" + email;
            }

            // 2FA CHECK
            if (response.isRequires2fa()) {
                logger.info("🔐 2FA Requerido para: {}", email);
                session.setAttribute("temp2faToken", response.getToken());
                return "redirect:/login-page?error=2fa_required";
            }

            // Guardar sesión Vesta
            session.setAttribute("token", response.getToken());
            session.setAttribute("rol", response.getRol());
            session.setAttribute("usuarioNombre", response.getNombre());
            session.setAttribute("usuarioId", response.getId());
            session.setAttribute("usuarioEmail", email);

            logger.info("🚀 Redirigiendo a Dashboard");
            return "redirect:/cliente/dashboard";

        } catch (Exception e) {
            String msg = e.getMessage();
            logger.error("❌ Error CRÍTICO en login social backend: {}", msg, e);

            // Verificar si es cuenta no verificada
            if (msg != null && (msg.contains("Cuenta no verificada") || msg.contains("not verified"))) {
                return "redirect:/login-page?error=account_not_verified&email=" + email;
            }

            // Verificar si es cuenta bloqueada
            if (msg != null && (msg.contains("bloqueada") || msg.contains("administrador"))) {
                try {
                    String encodedMsg = java.net.URLEncoder.encode(msg, "UTF-8");
                    return "redirect:/login-page?error=account_blocked&msg=" + encodedMsg;
                } catch (java.io.UnsupportedEncodingException ex) {
                    logger.error("Error encoding message", ex);
                    return "redirect:/login-page?error=account_blocked";
                }
            }

            // Error genérico
            try {
                String encodedMsg = msg != null ? java.net.URLEncoder.encode(msg, "UTF-8") : "";
                return "redirect:/login-page?error=social_backend_error&msg=" + encodedMsg;
            } catch (java.io.UnsupportedEncodingException ex) {
                logger.error("Error encoding message", ex);
                return "redirect:/login-page?error=social_backend_error";
            }
        }
    }
}
