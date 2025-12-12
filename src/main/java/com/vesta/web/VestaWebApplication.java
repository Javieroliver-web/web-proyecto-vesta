package com.vesta.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VestaWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(VestaWebApplication.class, args);
        System.out.println("🌐 WEB Vesta iniciada en el puerto 80");
    }
}