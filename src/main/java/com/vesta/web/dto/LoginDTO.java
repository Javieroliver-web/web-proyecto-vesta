package com.vesta.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    
    // Mapeamos el campo "email" de Java al JSON "correoElectronico"
    @JsonProperty("correoElectronico") 
    private String email;

    // Mapeamos el campo "password" de Java al JSON "contrasena"
    @JsonProperty("contrasena") 
    private String password;
}