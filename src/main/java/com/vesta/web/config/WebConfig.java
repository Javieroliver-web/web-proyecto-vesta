package com.vesta.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
                // Configurar el manejo de recursos estáticos con caché optimizado
                registry.addResourceHandler("/images/**")
                                .addResourceLocations("classpath:/static/images/", "file:/app/images/")
                                .setCachePeriod(3600) // Cache por 1 hora
                                .resourceChain(true);

                registry.addResourceHandler("/css/**")
                                .addResourceLocations("classpath:/static/css/")
                                .setCachePeriod(3600) // Cache por 1 hora
                                .resourceChain(true);

                registry.addResourceHandler("/js/**")
                                .addResourceLocations("classpath:/static/js/")
                                .setCachePeriod(3600) // Cache por 1 hora
                                .resourceChain(true);

                // Configurar favicon
                registry.addResourceHandler("/favicon.ico", "/favicon.svg", "/favicon-*.png")
                                .addResourceLocations("classpath:/static/")
                                .setCachePeriod(86400) // Cache por 24 horas
                                .resourceChain(true);

                // Configurar recursos estáticos generales
                registry.addResourceHandler("/static/**")
                                .addResourceLocations("classpath:/static/")
                                .setCachePeriod(3600) // Cache por 1 hora
                                .resourceChain(true);
        }
}
