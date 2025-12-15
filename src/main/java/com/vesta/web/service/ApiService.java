package com.vesta.web.service;

import com.vesta.web.dto.AuthResponseDTO;
import com.vesta.web.dto.ApiResponseWrapper;
import com.vesta.web.dto.CartItem;
import com.vesta.web.dto.LoginDTO;
import com.vesta.web.dto.RegisterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para comunicación con la API backend
 */
@Service
public class ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.url}")
    private String apiUrl;

    // === AUTENTICACIÓN ===

    public AuthResponseDTO login(String email, String password) {
        try {
            LoginDTO request = new LoginDTO();
            request.setCorreoElectronico(email);
            request.setContrasena(password);

            String url = apiUrl + "/auth/login";
            logger.debug("Intentando login para: {}", email);

            // La API devuelve ApiResponse<AuthResponseDTO>, no directamente AuthResponseDTO
            ResponseEntity<ApiResponseWrapper<AuthResponseDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<ApiResponseWrapper<AuthResponseDTO>>() {
                    });

            logger.info("Login exitoso para: {}", email);

            // Extraer el AuthResponseDTO del wrapper
            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            } else {
                throw new RuntimeException("Respuesta de la API vacía o inválida");
            }

        } catch (HttpClientErrorException e) {
            logger.error("Error de cliente en login para {}: {} - {}", email, e.getStatusCode(),
                    e.getResponseBodyAsString());
            throw new RuntimeException("Error en login: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            logger.error("Error de servidor en login: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del servidor. Por favor, intente más tarde.");
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión con la API: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor. Verifique su conexión.");
        } catch (Exception e) {
            logger.error("Error inesperado en login: {}", e.getMessage(), e);
            throw new RuntimeException("Error en login: " + e.getMessage());
        }
    }

    public void registrar(RegisterDTO registro) {
        try {
            String url = apiUrl + "/auth/register";
            logger.debug("Intentando registrar usuario: {}", registro.getCorreoElectronico());

            restTemplate.postForEntity(url, registro, String.class);

            logger.info("Registro exitoso para: {}", registro.getCorreoElectronico());

        } catch (HttpClientErrorException e) {
            logger.error("Error de cliente en registro para {}: {} - {}", registro.getCorreoElectronico(),
                    e.getStatusCode(),
                    e.getResponseBodyAsString());
            throw new RuntimeException("Error en registro: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            logger.error("Error de servidor en registro: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del servidor. Por favor, intente más tarde.");
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión con la API: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor. Verifique su conexión.");
        } catch (Exception e) {
            logger.error("Error inesperado en registro: {}", e.getMessage(), e);
            throw new RuntimeException("Error en registro: " + e.getMessage());
        }
    }

    // === VENTAS (CLIENTE) ===

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
            logger.debug("Realizando checkout para usuario: {}", usuarioId);
            restTemplate.postForEntity(url, request, String.class);
            logger.info("Checkout exitoso para usuario: {}", usuarioId);

        } catch (HttpClientErrorException e) {
            logger.error("Error de cliente en checkout: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error checkout: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            logger.error("Error de servidor en checkout: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del servidor. Por favor, intente más tarde.");
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión con la API: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor. Verifique su conexión.");
        } catch (Exception e) {
            logger.error("Error inesperado en checkout: {}", e.getMessage(), e);
            throw new RuntimeException("Error checkout: " + e.getMessage());
        }
    }

    // === GESTIÓN (ADMIN) ===

    public List<Map<String, Object>> obtenerTodasLasOrdenes(String token) {
        String url = apiUrl + "/ordenes";
        try {
            logger.debug("Obteniendo todas las órdenes");

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error al obtener órdenes: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<Map<String, Object>> obtenerSolicitudesRGPD(String token) {
        String url = apiUrl + "/derechos/todas";
        try {
            logger.debug("Obteniendo solicitudes RGPD");

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error al obtener solicitudes RGPD: {}", e.getMessage(), e);
            return List.of();
        }
    }

    // === NUEVO MÉTODO: OBTENER SINIESTROS ===
    public List<Map<String, Object>> obtenerSiniestros(String token) {
        String url = apiUrl + "/siniestros";
        try {
            logger.debug("Obteniendo siniestros");

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error al obtener siniestros: {}", e.getMessage(), e);
            return List.of();
        }
    }

    // === UTILIDADES ===

    private HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }
}
