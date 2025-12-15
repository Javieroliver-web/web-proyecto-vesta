package com.vesta.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para mapear la respuesta envuelta de la API
 * La API devuelve: ApiResponse<AuthResponseDTO>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseWrapper<T> {
    private boolean success;
    private String message;
    private T data;
}
