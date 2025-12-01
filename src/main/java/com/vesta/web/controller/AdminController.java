package com.vesta.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        String token = (String) session.getAttribute("token");
        String rol = (String) session.getAttribute("rol");

        if (token == null || !"admin".equals(rol)) {
            return "redirect:/";
        }
        return "admin/dashboard"; // Necesitas crear un HTML simple en templates/admin/dashboard.html
    }
}