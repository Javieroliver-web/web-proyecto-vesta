package com.vesta.web.service;

import com.vesta.web.dto.AuthResponseDTO;
import com.vesta.web.dto.CartItem;
import com.vesta.web.dto.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

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

    public AuthResponseDTO login(String email, String password) {
        try {
            LoginDTO request = new LoginDTO();
            request.setCorreoElectronico(email);
            request.setContrasena(password);
            
            String url = apiUrl + "/auth/login";
            System.out.println("📤 [WEB] Enviando Login a: " + url);

            ResponseEntity<AuthResponseDTO> response = restTemplate.postForEntity(url, request, AuthResponseDTO.class);
            return response.getBody();

        } catch (HttpClientErrorException e) {
            System.err.println("❌ [WEB] ERROR API (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
            throw new RuntimeException("Credenciales incorrectas o error de formato");
        } catch (Exception e) {
            System.err.println("❌ [WEB] ERROR CONEXIÓN: " + e.getMessage());
            throw new RuntimeException("Error de conexión con el servidor API");
        }
    }

    // NUEVO MÉTODO PARA CHECKOUT
    public void realizarCheckout(Long usuarioId, List<CartItem> carrito) {
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

        try {
            System.out.println("📤 [WEB] Procesando checkout para usuario: " + usuarioId);
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            System.err.println("❌ [WEB] Error en checkout: " + e.getMessage());
            throw new RuntimeException("Error al procesar el pago");
        }
    }
}