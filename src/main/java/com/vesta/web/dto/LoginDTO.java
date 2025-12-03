package com.vesta.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    // Usamos DIRECTAMENTE los nombres que espera la API
    // Eliminamos @JsonProperty para evitar confusiones
    private String correoElectronico;
    private String contrasena;
}