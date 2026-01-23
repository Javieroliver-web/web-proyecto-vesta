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
            logger.error("Error de cliente en login para {}: {}", email, e.getResponseBodyAsString());
            throw new RuntimeException(extractErrorMessage(e));
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

    public AuthResponseDTO verify2fa(String tempToken, String code) {
        try {
            String url = apiUrl + "/auth/2fa/validate-login";
            logger.debug("Verificando código 2FA");

            Map<String, String> request = new HashMap<>();
            request.put("code", code);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + tempToken);

            ResponseEntity<ApiResponseWrapper<AuthResponseDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, headers),
                    new ParameterizedTypeReference<ApiResponseWrapper<AuthResponseDTO>>() {
                    });

            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            } else {
                throw new RuntimeException("Respuesta 2FA vacía");
            }

        } catch (HttpClientErrorException e) {
            throw new RuntimeException(extractErrorMessage(e));
        } catch (Exception e) {
            throw new RuntimeException("Error en 2FA: " + e.getMessage());
        }
    }

    public AuthResponseDTO socialLogin(String email, String name, String provider) {
        try {
            String url = apiUrl + "/auth/social-login";
            logger.debug("Procesando login social para: {}", email);

            Map<String, String> request = new HashMap<>();
            request.put("email", email);
            request.put("nombre", name);
            request.put("proveedor", provider);

            ResponseEntity<ApiResponseWrapper<AuthResponseDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<ApiResponseWrapper<AuthResponseDTO>>() {
                    });

            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            } else {
                throw new RuntimeException("Respuesta de API vacía en social login");
            }
        } catch (Exception e) {
            logger.error("Error en social login: {}", e.getMessage());
            throw new RuntimeException("Error al procesar login social");
        }
    }

    public void registrar(RegisterDTO registro) {
        try {
            String url = apiUrl + "/auth/register";
            logger.debug("Intentando registrar usuario: {}", registro.getCorreoElectronico());

            restTemplate.postForEntity(url, registro, String.class);

            logger.info("Registro exitoso para: {}", registro.getCorreoElectronico());

        } catch (HttpClientErrorException e) {
            logger.error("Error de cliente en registro para {}: {}", registro.getCorreoElectronico(),
                    e.getResponseBodyAsString());
            throw new RuntimeException(extractErrorMessage(e));
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

    /**
     * Solicita recuperación de contraseña
     */
    public String forgotPassword(String email) {
        try {
            String url = apiUrl + "/auth/forgot-password";
            logger.debug("Solicitando recuperación de contraseña para: {}", email);

            Map<String, String> request = new HashMap<>();
            request.put("email", email);

            ResponseEntity<ApiResponseWrapper<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<ApiResponseWrapper<String>>() {
                    });

            logger.info("Solicitud de recuperación enviada para: {}", email);

            if (response.getBody() != null && response.getBody().getMessage() != null) {
                return response.getBody().getMessage();
            } else {
                return "Se ha enviado un código de verificación a tu correo electrónico";
            }

        } catch (HttpClientErrorException e) {
            logger.error("Error de cliente en forgot-password para {}: {}", email, e.getResponseBodyAsString());
            throw new RuntimeException(extractErrorMessage(e));
        } catch (HttpServerErrorException e) {
            logger.error("Error de servidor en forgot-password: {} - {}", e.getStatusCode(),
                    e.getResponseBodyAsString());
            throw new RuntimeException("Error del servidor. Por favor, intente más tarde.");
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión con la API: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor. Verifique su conexión.");
        } catch (Exception e) {
            logger.error("Error inesperado en forgot-password: {}", e.getMessage(), e);
            throw new RuntimeException("Error al solicitar recuperación de contraseña: " + e.getMessage());
        }
    }

    /**
     * Solicita recuperación de contraseña con método específico
     */
    public String forgotPassword(String email, String method) {
        try {
            String url = apiUrl + "/auth/forgot-password";
            logger.debug("Solicitando recuperación de contraseña para: {} por {}", email, method);

            Map<String, String> request = new HashMap<>();
            request.put("email", email);
            request.put("method", method);

            ResponseEntity<ApiResponseWrapper<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<ApiResponseWrapper<String>>() {
                    });

            logger.info("Solicitud de recuperación enviada para: {} por {}", email, method);

            if (response.getBody() != null && response.getBody().getMessage() != null) {
                return response.getBody().getMessage();
            } else {
                return "Se ha enviado un código de verificación";
            }

        } catch (HttpClientErrorException e) {
            logger.error("Error de cliente en forgot-password para {}: {}", email, e.getResponseBodyAsString());
            throw new RuntimeException(extractErrorMessage(e));
        } catch (HttpServerErrorException e) {
            logger.error("Error de servidor en forgot-password: {} - {}", e.getStatusCode(),
                    e.getResponseBodyAsString());
            throw new RuntimeException("Error del servidor. Por favor, intente más tarde.");
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión con la API: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor. Verifique su conexión.");
        } catch (Exception e) {
            logger.error("Error inesperado en forgot-password: {}", e.getMessage(), e);
            throw new RuntimeException("Error al solicitar recuperación de contraseña");
        }
    }

    /**
     * Verifica qué métodos de recuperación están disponibles para un usuario
     */
    public Map<String, Object> checkRecoveryMethods(String email) {
        try {
            String url = apiUrl + "/auth/check-recovery-methods";
            logger.debug("Verificando métodos de recuperación para: {}", email);

            Map<String, String> request = new HashMap<>();
            request.put("email", email);

            ResponseEntity<ApiResponseWrapper<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<ApiResponseWrapper<Map<String, Object>>>() {
                    });

            return response.getBody().getData();

        } catch (HttpClientErrorException e) {
            logger.error("Error verificando métodos: {}", e.getResponseBodyAsString());
            throw new RuntimeException(extractErrorMessage(e));
        } catch (Exception e) {
            logger.error("Error inesperado verificando métodos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al verificar métodos de recuperación");
        }
    }

    /**
     * Solicita el reenvío del correo de confirmación
     */
    public String resendConfirmation(String email) {
        try {
            String url = apiUrl + "/auth/resend-confirmation";
            logger.debug("Solicitando reenvío de confirmación para: {}", email);

            Map<String, String> request = new HashMap<>();
            request.put("email", email);

            ResponseEntity<ApiResponseWrapper<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<ApiResponseWrapper<String>>() {
                    });

            if (response.getBody() != null && response.getBody().getMessage() != null) {
                return response.getBody().getMessage();
            } else {
                return "Correo de confirmación reenviado";
            }

        } catch (HttpClientErrorException e) {
            logger.error("Error de cliente en reenvío: {}", e.getResponseBodyAsString());
            throw new RuntimeException(extractErrorMessage(e));
        } catch (Exception e) {
            logger.error("Error inesperado en reenvío: {}", e.getMessage(), e);
            throw new RuntimeException("Error al reenviar correo");
        }
    }

    /**
     * Resetea la contraseña con un token válido
     */
    public String resetPassword(String token, String newPassword) {
        try {
            String url = apiUrl + "/auth/reset-password";
            logger.debug("Reseteando contraseña con token");

            Map<String, String> request = new HashMap<>();
            request.put("token", token);
            request.put("newPassword", newPassword);

            ResponseEntity<ApiResponseWrapper<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<ApiResponseWrapper<String>>() {
                    });

            logger.info("Contraseña reseteada exitosamente");

            if (response.getBody() != null && response.getBody().getMessage() != null) {
                return response.getBody().getMessage();
            } else {
                return "Contraseña actualizada exitosamente";
            }

        } catch (HttpClientErrorException e) {
            logger.error("Error de cliente en reset-password: {}", e.getResponseBodyAsString());
            throw new RuntimeException(extractErrorMessage(e));
        } catch (HttpServerErrorException e) {
            logger.error("Error de servidor en reset-password: {} - {}", e.getStatusCode(),
                    e.getResponseBodyAsString());
            throw new RuntimeException("Error del servidor. Por favor, intente más tarde.");
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión con la API: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor. Verifique su conexión.");
        } catch (Exception e) {
            logger.error("Error inesperado en reset-password: {}", e.getMessage(), e);
            throw new RuntimeException("Error al resetear contraseña: " + e.getMessage());
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
            logger.error("Error de cliente en checkout: {}", e.getResponseBodyAsString());
            throw new RuntimeException(extractErrorMessage(e));
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

    // === PÓLIZAS DEL USUARIO ===
    
    public List<Map<String, Object>> obtenerPolizasUsuario(String token) {
        String url = apiUrl + "/polizas/usuario";
        try {
            logger.debug("Obteniendo pólizas del usuario");

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });

            return response.getBody();

        } catch (HttpClientErrorException e) {
            logger.error("Error de cliente al obtener pólizas del usuario: {}", e.getResponseBodyAsString());
            throw new RuntimeException(extractErrorMessage(e));
        } catch (HttpServerErrorException e) {
            logger.error("Error de servidor al obtener pólizas: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del servidor. Por favor, intente más tarde.");
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión con la API: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor. Verifique su conexión.");
        } catch (Exception e) {
            logger.error("Error inesperado al obtener pólizas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener pólizas: " + e.getMessage());
        }
    }

    // === RECOMENDACIONES IA ===
    
    public Map<String, Object> obtenerRecomendacionIA(String token, String email) {
        String url = apiUrl + "/innovation/recommendation";
        if (email != null) {
            url += "?email=" + email;
        }
        
        try {
            logger.debug("Obteniendo recomendación IA para: {}", email);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error al obtener recomendación IA: {}", e.getMessage(), e);
            // Devolver una recomendación por defecto en caso de error
            Map<String, Object> defaultRecommendation = new HashMap<>();
            defaultRecommendation.put("mensaje", "Protégete hoy con nuestros seguros personalizados");
            defaultRecommendation.put("icono", "SUN");
            return defaultRecommendation;
        }
    }

    public Map<String, Object> chatIA(String token, String pregunta) {
        String url = apiUrl + "/innovation/chat";
        
        try {
            logger.debug("Enviando pregunta al chat IA");

            Map<String, String> request = new HashMap<>();
            request.put("pregunta", pregunta);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, getHeaders(token)),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error en chat IA: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("respuesta", "Lo siento, no puedo procesar tu consulta en este momento. Por favor, intenta más tarde.");
            return errorResponse;
        }
    }

    // === CONTRATACIÓN DE PÓLIZAS ===
    
    public Map<String, Object> contratarPoliza(String token, Map<String, Object> request) {
        String url = apiUrl + "/polizas/contratar";
        
        try {
            logger.debug("Contratando póliza");

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, getHeaders(token)),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return response.getBody();

        } catch (HttpClientErrorException e) {
            logger.error("Error de cliente al contratar póliza: {}", e.getResponseBodyAsString());
            throw new RuntimeException(extractErrorMessage(e));
        } catch (HttpServerErrorException e) {
            logger.error("Error de servidor al contratar póliza: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del servidor. Por favor, intente más tarde.");
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión con la API: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor. Verifique su conexión.");
        } catch (Exception e) {
            logger.error("Error inesperado al contratar póliza: {}", e.getMessage(), e);
            throw new RuntimeException("Error al contratar póliza: " + e.getMessage());
        }
    }

    // === REPORTES PDF ===
    
    public byte[] generarReportePDF(String token) {
        String url = apiUrl + "/reportes/polizas/pdf";
        
        try {
            logger.debug("Generando reporte PDF de pólizas");

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    byte[].class
            );

            return response.getBody();

        } catch (HttpClientErrorException e) {
            logger.error("Error de cliente al generar PDF: {}", e.getResponseBodyAsString());
            throw new RuntimeException(extractErrorMessage(e));
        } catch (HttpServerErrorException e) {
            logger.error("Error de servidor al generar PDF: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del servidor. Por favor, intente más tarde.");
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión con la API: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor. Verifique su conexión.");
        } catch (Exception e) {
            logger.error("Error inesperado al generar PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar reporte: " + e.getMessage());
        }
    }

    // === SINIESTROS ===
    
    public Map<String, Object> reportarSiniestro(String token, Map<String, Object> request) {
        String url = apiUrl + "/siniestros";
        
        try {
            logger.debug("Reportando siniestro");

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, getHeaders(token)),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return response.getBody();

        } catch (HttpClientErrorException e) {
            logger.error("Error de cliente al reportar siniestro: {}", e.getResponseBodyAsString());
            throw new RuntimeException(extractErrorMessage(e));
        } catch (HttpServerErrorException e) {
            logger.error("Error de servidor al reportar siniestro: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del servidor. Por favor, intente más tarde.");
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión con la API: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor. Verifique su conexión.");
        } catch (Exception e) {
            logger.error("Error inesperado al reportar siniestro: {}", e.getMessage(), e);
            throw new RuntimeException("Error al reportar siniestro: " + e.getMessage());
        }
    }

    // === GESTIÓN DE USUARIOS INDIVIDUALES ===
    
    public Map<String, Object> obtenerUsuario(String token, Long userId) {
        String url = apiUrl + "/usuarios/" + userId;
        
        try {
            logger.debug("Obteniendo usuario: {}", userId);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error al obtener usuario {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error al obtener usuario: " + e.getMessage());
        }
    }

    public Map<String, Object> actualizarUsuario(String token, Long userId, Map<String, Object> updates) {
        String url = apiUrl + "/usuarios/" + userId;
        
        try {
            logger.debug("Actualizando usuario: {}", userId);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(updates, getHeaders(token)),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error al actualizar usuario {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage());
        }
    }

    public void eliminarUsuario(String token, Long userId) {
        String url = apiUrl + "/usuarios/" + userId;
        
        try {
            logger.debug("Eliminando usuario: {}", userId);

            restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    new HttpEntity<>(getHeaders(token)),
                    String.class
            );

        } catch (Exception e) {
            logger.error("Error al eliminar usuario {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage());
        }
    }

    // === DERECHOS RGPD ===
    
    public Map<String, Object> solicitarSupresionDatos(String token, Map<String, Object> request) {
        String url = apiUrl + "/derechos/solicitar-supresion";
        
        try {
            logger.debug("Solicitando supresión de datos");

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, getHeaders(token)),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error al solicitar supresión: {}", e.getMessage());
            throw new RuntimeException("Error al solicitar supresión: " + e.getMessage());
        }
    }

    public Map<String, Object> solicitarDerecho(String token, String endpoint, Map<String, Object> request) {
        String url = apiUrl + "/derechos/" + endpoint;
        
        try {
            logger.debug("Solicitando derecho: {}", endpoint);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, getHeaders(token)),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error al solicitar derecho {}: {}", endpoint, e.getMessage());
            throw new RuntimeException("Error al procesar solicitud: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> obtenerSolicitudesUsuario(String token, Long userId) {
        String url = apiUrl + "/derechos/mis-solicitudes/" + userId;
        
        try {
            logger.debug("Obteniendo solicitudes del usuario: {}", userId);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error al obtener solicitudes del usuario {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error al obtener solicitudes: " + e.getMessage());
        }
    }

    // === GESTIÓN DE SINIESTROS (ADMIN) ===
    
    public Map<String, Object> actualizarEstadoSiniestro(String token, Long siniestroId, Map<String, Object> updates) {
        String url = apiUrl + "/siniestros/" + siniestroId + "/estado";
        
        try {
            logger.debug("Actualizando estado de siniestro: {}", siniestroId);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(updates, getHeaders(token)),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error al actualizar siniestro {}: {}", siniestroId, e.getMessage());
            throw new RuntimeException("Error al actualizar siniestro: " + e.getMessage());
        }
    }

    // === GESTIÓN DE PRODUCTOS (ADMIN) ===
    
    public void eliminarProducto(String token, Long productoId) {
        String url = apiUrl + "/productos/" + productoId;
        
        try {
            logger.debug("Eliminando producto: {}", productoId);

            restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    new HttpEntity<>(getHeaders(token)),
                    String.class
            );

        } catch (Exception e) {
            logger.error("Error al eliminar producto {}: {}", productoId, e.getMessage());
            throw new RuntimeException("Error al eliminar producto: " + e.getMessage());
        }
    }

    // === GESTIÓN DE COOKIES ===
    
    public Map<String, Object> guardarConsentimientoCookies(String token, Map<String, Object> request) {
        String url = apiUrl + "/cookies/consentimiento";
        
        try {
            logger.debug("Guardando consentimiento de cookies");

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, getHeaders(token)),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error al guardar consentimiento: {}", e.getMessage());
            throw new RuntimeException("Error al guardar consentimiento: " + e.getMessage());
        }
    }

    // === CONFIRMACIÓN DE CUENTA ===
    
    public void confirmarCuenta(String token) {
        String url = apiUrl + "/auth/confirm-account?token=" + token;
        
        try {
            logger.debug("Confirmando cuenta con token: {}", token);

            restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );

            logger.info("Cuenta confirmada exitosamente");

        } catch (Exception e) {
            logger.error("Error al confirmar cuenta: {}", e.getMessage());
            throw new RuntimeException("Error al confirmar cuenta: " + e.getMessage());
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

    public List<Map<String, Object>> obtenerOrdenesPendientesUsuario(String token, Long usuarioId) {
        String url = apiUrl + "/ordenes/usuario/" + usuarioId + "/pendientes";
        try {
            logger.debug("Obteniendo órdenes pendientes para usuario: {}", usuarioId);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error al obtener órdenes pendientes del usuario {}: {}", usuarioId, e.getMessage(), e);
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

    // === GESTIÓN DE USUARIOS ===
    public List<Map<String, Object>> obtenerTodosLosUsuarios(String token) {
        String url = apiUrl + "/usuarios";
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error al obtener usuarios: {}", e.getMessage());
            return List.of();
        }
    }

    public void actualizarEstadoUsuario(Long userId, boolean activo, String token) {
        String url = apiUrl + "/usuarios/" + userId;
        Map<String, Object> updates = new HashMap<>();
        updates.put("activo", activo);

        try {
            restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(updates, getHeaders(token)),
                    String.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar estado de usuario: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> obtenerTodasLasPolizas(String token) {
        String url = apiUrl + "/polizas";
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error al obtener pólizas: {}", e.getMessage());
            return List.of();
        }
    }

    // === AUDITORÍA ===
    public List<Map<String, Object>> obtenerLogsAuditoria(String token) {
        String url = apiUrl + "/auditoria";
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error al obtener logs: {}", e.getMessage());
            return List.of();
        }
    }

    // === ESTADÍSTICAS ===
    public Map<String, Object> obtenerEstadisticas(String token) {
        String url = apiUrl + "/admin/estadisticas";
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    // === PRODUCTOS (PÚBLICO) ===
    public List<Map<String, Object>> getProductos() {
        String url = apiUrl + "/productos";
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null, // No auth headers required
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error al obtener productos: {}", e.getMessage());
            return List.of();
        }
    }

    public Map<String, Object> getProductoPorId(Long id) {
        String url = apiUrl + "/productos/" + id;
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null, // No auth headers required
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error al obtener producto por ID {}: {}", id, e.getMessage());
            return new HashMap<>();
        }
    }

    // === UTILIDADES ===

    private String extractErrorMessage(HttpClientErrorException e) {
        String responseBody = e.getResponseBodyAsString();
        logger.error("🔴 Error Raw Body: {}", responseBody); // DIAGNÓSTICO

        try {
            // Intentar parsear el JSON de error
            // La estructura es {"success": false, "message": "...", ...}
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            // Usamos TypeReference o Map para mayor flexibilidad si el wrapper falla
            Map<String, Object> errorMap = mapper.readValue(responseBody,
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                    });

            if (errorMap != null && errorMap.containsKey("message")) {
                return (String) errorMap.get("message");
            }
        } catch (Exception parseException) {
            // Si falla el parseo, devolver el mensaje original o el cuerpo
            logger.warn("No se pudo parsear el error de la API: {}", parseException.getMessage());
        }

        // Si no se pudo extraer un mensaje limpio, devolver algo útil pero limpio
        // Evitar devolver todo el JSON crudo si es posible
        if (responseBody != null && responseBody.contains("\"message\":\"")) {
            // Fallback muy básico por si falla Jackson
            int start = responseBody.indexOf("\"message\":\"") + 11;
            int end = responseBody.indexOf("\"", start);
            if (end > start) {
                return responseBody.substring(start, end);
            }
        }

        return "Error " + e.getStatusCode();
    }

    // === TPV VIRTUAL ===
    
    public Map<String, Object> checkoutTPV(String token, Map<String, Object> request) {
        String url = apiUrl + "/ordenes/checkout-tpv";
        
        try {
            logger.debug("Procesando checkout con TPV");

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, getHeaders(token)),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return response.getBody();

        } catch (HttpClientErrorException e) {
            logger.error("Error de cliente en checkout TPV: {}", e.getResponseBodyAsString());
            throw new RuntimeException(extractErrorMessage(e));
        } catch (Exception e) {
            logger.error("Error inesperado en checkout TPV: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar pago: " + e.getMessage());
        }
    }

    public Map<String, String> getTarjetasPrueba(String token) {
        String url = apiUrl + "/tpv/tarjetas-prueba";
        
        try {
            logger.debug("Obteniendo tarjetas de prueba");

            ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders(token)),
                    new ParameterizedTypeReference<Map<String, String>>() {
                    });

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error al obtener tarjetas de prueba: {}", e.getMessage(), e);
            // Devolver tarjetas por defecto en caso de error
            Map<String, String> defaultCards = new HashMap<>();
            defaultCards.put("4111111111111111", "Visa - Pago Exitoso");
            defaultCards.put("4000000000000002", "Visa - Tarjeta Rechazada");
            return defaultCards;
        }
    }

    private HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }
}
