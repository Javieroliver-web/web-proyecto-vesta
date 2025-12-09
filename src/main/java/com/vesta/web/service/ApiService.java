package com.vesta.web.service;

import com.vesta.web.dto.AuthResponseDTO;
import com.vesta.web.dto.CartItem;
import com.vesta.web.dto.LoginDTO;
import com.vesta.web.dto.RegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference; // <--- NUEVO
import org.springframework.http.HttpEntity; // <--- NUEVO
import org.springframework.http.HttpHeaders; // <--- NUEVO
import org.springframework.http.HttpMethod; // <--- NUEVO
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.url}")
    private String apiUrl;

    // ... (MANTÉN LOS MÉTODOS login, registrar y realizarCheckout QUE YA TIENES) ...
    public AuthResponseDTO login(String email, String password) {
        // (Tu código actual de login)
        try {
            LoginDTO request = new LoginDTO();
            request.setCorreoElectronico(email);
            request.setContrasena(password);
            String url = apiUrl + "/auth/login";
            ResponseEntity<AuthResponseDTO> response = restTemplate.postForEntity(url, request, AuthResponseDTO.class);
            return response.getBody();
        } catch (Exception e) { throw new RuntimeException("Error login"); }
    }

    public void registrar(RegisterDTO registro) {
        // (Tu código actual de registro)
        try { restTemplate.postForEntity(apiUrl + "/auth/register", registro, String.class); }
        catch (Exception e) { throw new RuntimeException("Error registro"); }
    }

    public void realizarCheckout(Long usuarioId, List<CartItem> carrito) {
        // (Tu código actual de checkout)
        // ...
        String url = apiUrl + "/ordenes/checkout";
        Map<String, Object> request = new HashMap<>();
        request.put("usuarioId", usuarioId);
        List<Map<String, Object>> items = carrito.stream().map(item -> {
            Map<String, Object> i = new HashMap<>();
            i.put("seguroId", item.getSeguroId());
            i.put("cantidad", item.getCantidad());
            return i;
        }).collect(Collectors.toList());
        request.put("items", items);
        try { restTemplate.postForEntity(url, request, String.class); }
        catch (Exception e) { throw new RuntimeException("Error checkout"); }
    }

    // === NUEVOS MÉTODOS PARA ADMIN ===

    public List<Map<String, Object>> obtenerTodasLasOrdenes(String token) {
        String url = apiUrl + "/ordenes";
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                new HttpEntity<>(getHeaders(token)), 
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); 
        }
    }

    public List<Map<String, Object>> obtenerSolicitudesRGPD(String token) {
        String url = apiUrl + "/derechos/todas"; // Apunta al nuevo endpoint
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                new HttpEntity<>(getHeaders(token)), 
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }
}