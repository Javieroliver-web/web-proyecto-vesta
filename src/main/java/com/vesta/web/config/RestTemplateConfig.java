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
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        logger.info("Configurando RestTemplate con timeout de conexión: {}ms, timeout de lectura: {}ms",
                connectionTimeout, readTimeout);

        // Configurar timeouts
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectionTimeout);
        requestFactory.setReadTimeout(readTimeout);

        // Usar BufferingClientHttpRequestFactory para permitir leer el body múltiples
        // veces
        BufferingClientHttpRequestFactory bufferingFactory = new BufferingClientHttpRequestFactory(requestFactory);

        RestTemplate restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(connectionTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .requestFactory(() -> bufferingFactory)
                .build();

        // Añadir interceptor para logging
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

            long startTime = System.currentTimeMillis();

            log.debug("Request: {} {}", request.getMethod(), request.getURI());

            org.springframework.http.client.ClientHttpResponse response = execution.execute(request, body);

            long duration = System.currentTimeMillis() - startTime;
            log.debug("Response: {} {} - Status: {} - Duration: {}ms",
                    request.getMethod(), request.getURI(), response.getStatusCode(), duration);

            return response;
        }
    }
}
