package com.vesta.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {

    private String nombreCompleto;
    private String correoElectronico; // Antes: email
    private String movil;
    private String contrasena; // Antes: password

    // Nuevos campos
    private java.time.LocalDate fechaNacimiento;
    private String direccion;
    private String codigoPostal;
    private String ciudad;
    private String pais;

    private String tipoUsuario = "USUARIO";

    // Campos de control (no se envían a la API, pero sirven para validación en el
    // controller)
    private boolean aceptaTerminos;
    private boolean aceptaPrivacidad;
    private boolean aceptaMarketing;
}