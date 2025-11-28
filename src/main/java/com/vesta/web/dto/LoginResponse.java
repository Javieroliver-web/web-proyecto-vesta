package com.vesta.web.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String rol;
    private String nombre;
}