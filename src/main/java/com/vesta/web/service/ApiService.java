package com.vesta.web.service;

import com.vesta.web.dto.LoginDTO;
import com.vesta.web.dto.AuthResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class ApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.url}")
    private String apiUrl;

    public AuthResponseDTO login(String email, String password) {
        try {
            LoginDTO request = new LoginDTO();
            // AHORA USAMOS LOS SETTERS EN ESPAÑOL (coinciden con el DTO nuevo)
            request.setCorreoElectronico(email);
            request.setContrasena(password);
            
            String url = apiUrl + "/auth/login";

            // LOG DEPURACIÓN: Ver qué estamos enviando
            System.out.println("📤 [WEB] Enviando Login a: " + url);
            System.out.println("📦 [WEB] Datos: " + request.toString());

            ResponseEntity<AuthResponseDTO> response = restTemplate.postForEntity(url, request, AuthResponseDTO.class);
            
            return response.getBody();

        } catch (HttpClientErrorException e) {
            // LOG DEPURACIÓN IMPORTANTE: Ver el error real que devuelve la API
            System.err.println("❌ [WEB] ERROR API (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
            
            throw new RuntimeException("Credenciales incorrectas o error de formato");
        } catch (Exception e) {
            System.err.println("❌ [WEB] ERROR CONEXIÓN: " + e.getMessage());
            throw new RuntimeException("Error de conexión con el servidor API");
        }
    }
}