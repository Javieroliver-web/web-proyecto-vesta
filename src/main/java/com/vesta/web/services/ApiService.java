package com.vesta.web.services;

import com.vesta.web.dto.LoginRequest;
import com.vesta.web.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class ApiService {

    @Autowired
    private RestTemplate restTemplate;

    // Leemos la URL de la API del application.properties
    @Value("${api.url}")
    private String apiUrl;

    public LoginResponse login(String email, String password) {
        try {
            // Preparamos los datos
            LoginRequest request = new LoginRequest(email, password);
            String url = apiUrl + "/auth/login";

            // Hacemos la llamada POST a la API
            ResponseEntity<LoginResponse> response = restTemplate.postForEntity(url, request, LoginResponse.class);
            
            return response.getBody();
        } catch (HttpClientErrorException e) {
            // Si la API devuelve 400/401, lanzamos error para que el Controller lo capture
            throw new RuntimeException("Credenciales incorrectas");
        } catch (Exception e) {
            throw new RuntimeException("Error de conexión con el servidor API");
        }
    }
}