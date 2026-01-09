package com.vesta.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuración de RestTemplate para comunicación con la API
 */
@Configuration
public class RestTemplateConfig {

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);

    @Value("${api.connection.timeout:5000}")
    private int connectionTimeout;

    @Value("${api.read.timeout:10000}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate() {
        logger.info("Configurando RestTemplate MANUAL con timeout conn: {}ms, read: {}ms", connectionTimeout,
                readTimeout);

        // 1. Factory Base (Simple)
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectionTimeout);
        requestFactory.setReadTimeout(readTimeout);
        requestFactory.setOutputStreaming(false); // Importante para buffering en algunos casos

        // 2. Decorador Buffering (Clave para leer body mÃºltiples veces)
        BufferingClientHttpRequestFactory bufferingFactory = new BufferingClientHttpRequestFactory(requestFactory);

        // 3. Instancia directa (sin Builder para evitar magias)
        RestTemplate restTemplate = new RestTemplate(bufferingFactory);

        // 4. Interceptor con Logging del Body DE RESPUESTA
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingInterceptor());
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }

    /**
     * Interceptor para logging de requests y responses
     */
    private static class LoggingInterceptor implements ClientHttpRequestInterceptor {

        private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

        @Override
        public org.springframework.http.client.ClientHttpResponse intercept(
                org.springframework.http.HttpRequest request,
                byte[] body,
                org.springframework.http.client.ClientHttpRequestExecution execution) throws java.io.IOException {

            log.debug(">>> Request: {} {}", request.getMethod(), request.getURI());

            // Ejecutar request
            org.springframework.http.client.ClientHttpResponse response = execution.execute(request, body);

            // Leer respuesta (seguro porque usamos BufferingClientHttpRequestFactory)
            StringBuilder inputStringBuilder = new StringBuilder();
            try (java.io.BufferedReader bufferedReader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(response.getBody(), java.nio.charset.StandardCharsets.UTF_8))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    inputStringBuilder.append(line);
                }
            } catch (Exception e) {
                log.error("Error leyendo body en interceptor: {}", e.getMessage());
            }

            String responseBody = inputStringBuilder.toString();
            log.debug("<<< Response: {} {} - Status: {} - Body: {}",
                    request.getMethod(), request.getURI(), response.getStatusCode(), responseBody);

            return response;
        }
    }
}
