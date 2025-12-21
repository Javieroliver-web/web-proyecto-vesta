package com.vesta.web.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de diagnóstico para verificar sesiones HTTP
 * TEMPORAL - Solo para debugging
 * IMPORTANTE: Solo disponible en perfil 'dev' por seguridad
 */
@RestController
@RequestMapping("/debug")
@Profile("dev")
public class DebugController {
    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

    @GetMapping("/session")
    public Map<String, Object> checkSession(HttpSession session) {
        Map<String, Object> info = new HashMap<>();

        info.put("sessionId", session.getId());
        info.put("isNew", session.isNew());
        info.put("creationTime", session.getCreationTime());
        info.put("lastAccessedTime", session.getLastAccessedTime());
        info.put("maxInactiveInterval", session.getMaxInactiveInterval());

        // Verificar atributos de sesión
        info.put("token", session.getAttribute("token") != null ? "PRESENTE" : "NULL");
        info.put("rol", session.getAttribute("rol"));
        info.put("usuarioNombre", session.getAttribute("usuarioNombre"));
        info.put("usuarioId", session.getAttribute("usuarioId"));

        logger.debug("🔍 DEBUG - Verificación de sesión:");
        logger.debug("   Session ID: {}", session.getId());
        logger.debug("   Token: {}", (session.getAttribute("token") != null ? "✓" : "✗"));
        logger.debug("   Rol: {}", session.getAttribute("rol"));

        return info;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "OK");
        status.put("service", "vesta-web");
        status.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return status;
    }
}
