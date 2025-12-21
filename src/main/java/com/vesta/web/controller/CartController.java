package com.vesta.web.controller;

import com.vesta.web.dto.CartItem;
import com.vesta.web.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cliente/carrito")
@RequiredArgsConstructor
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final ApiService apiService;

    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        // Verificar sesión
        if (session.getAttribute("token") == null)
            return "redirect:/";

        List<CartItem> carrito = obtenerCarrito(session);
        BigDecimal total = calcularTotal(carrito);

        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
        model.addAttribute("nombreUsuario", session.getAttribute("usuarioNombre"));

        return "cliente/carrito";
    }

    @PostMapping("/agregar")
    @ResponseBody // Respuesta JSON para el fetch de JS
    public String agregarItem(@RequestBody CartItem item, HttpSession session) {
        List<CartItem> carrito = obtenerCarrito(session);

        boolean existe = false;
        for (CartItem i : carrito) {
            if (i.getSeguroId().equals(item.getSeguroId())) {
                i.setCantidad(i.getCantidad() + 1);
                existe = true;
                break;
            }
        }

        if (!existe) {
            item.setCantidad(1);
            carrito.add(item);
        }

        return "OK";
    }

    @GetMapping("/eliminar/{index}")
    public String eliminarItem(@PathVariable int index, HttpSession session) {
        List<CartItem> carrito = obtenerCarrito(session);
        if (index >= 0 && index < carrito.size()) {
            carrito.remove(index);
        }
        return "redirect:/cliente/carrito";
    }

    @PostMapping("/checkout")
    public String procesarCheckout(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        List<CartItem> carrito = obtenerCarrito(session);

        if (carrito.isEmpty()) {
            return "redirect:/cliente/carrito?error=empty";
        }

        try {
            apiService.realizarCheckout(usuarioId, carrito);

            // Vaciar carrito tras compra exitosa
            session.setAttribute("carrito", new ArrayList<>());
            return "redirect:/cliente/dashboard?success=checkout";

        } catch (Exception e) {
            logger.error("Error procesando checkout", e);
            return "redirect:/cliente/carrito?error=api_fail";
        }
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> obtenerCarrito(HttpSession session) {
        List<CartItem> carrito = (List<CartItem>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    private BigDecimal calcularTotal(List<CartItem> carrito) {
        return carrito.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}