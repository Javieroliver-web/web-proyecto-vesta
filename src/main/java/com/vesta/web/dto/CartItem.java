package com.vesta.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private String seguroId;
    private String nombre;
    private BigDecimal precio;
    private String imagenUrl;
    private Integer cantidad;
    
    public BigDecimal getSubtotal() {
        if (precio == null || cantidad == null) return BigDecimal.ZERO;
        return precio.multiply(new BigDecimal(cantidad));
    }
}