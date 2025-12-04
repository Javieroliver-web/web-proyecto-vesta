package com.vesta.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    // IMPORTANTE: Estos @JsonProperty son necesarios para que Jackson
    // serialice correctamente al enviar a la API
    @JsonProperty("correoElectronico")
    private String correoElectronico;
    
    @JsonProperty("contrasena")
    private String contrasena;
}