package com.vesta.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class VestaWebApplication extends SpringBootServletInitializer {
    private static final Logger logger = LoggerFactory.getLogger(VestaWebApplication.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(VestaWebApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(VestaWebApplication.class, args);
        logger.info("WEB Vesta iniciada en el puerto 8081");
    }
}