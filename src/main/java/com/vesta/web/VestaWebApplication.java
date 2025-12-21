package com.vesta.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VestaWebApplication {
    private static final Logger logger = LoggerFactory.getLogger(VestaWebApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(VestaWebApplication.class, args);
        logger.info("🌐 WEB Vesta iniciada en el puerto 80");
    }
}