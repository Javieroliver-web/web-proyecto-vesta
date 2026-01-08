package com.vesta.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Configurar el manejo de recursos estáticos
                registry.addResourceHandler("/images/**")
                                .addResourceLocations("classpath:/static/images/", "file:/app/images/");

                registry.addResourceHandler("/css/**")
                                .addResourceLocations("classpath:/static/css/");

                registry.addResourceHandler("/js/**")
                                .addResourceLocations("classpath:/static/js/");
        }
}
