package com.vesta.web.service; // <--- CAMBIO A SINGULAR

import com.vesta.web.dto.LoginDTO;         // Usaremos nombres Sprintix
import com.vesta.web.dto.AuthResponseDTO;  // Usaremos nombres Sprintix
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

    // Cambiamos a devolver AuthResponseDTO
    public AuthResponseDTO login(String email, String password) {
        try {
            LoginDTO request = new LoginDTO(); // Usamos el DTO nuevo
            request.setEmail(email);
            request.setPassword(password);
            
            String url = apiUrl + "/auth/login";

            ResponseEntity<AuthResponseDTO> response = restTemplate.postForEntity(url, request, AuthResponseDTO.class);
            
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Credenciales incorrectas");
        } catch (Exception e) {
            throw new RuntimeException("Error de conexión con el servidor API");
        }
    }
}