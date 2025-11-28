package com.vesta.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class VestaWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(VestaWebApplication.class, args);
        System.out.println("🌐 WEB Vesta iniciada en el puerto 80");
    }

    // Registramos RestTemplate para poder inyectarlo en los servicios
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}