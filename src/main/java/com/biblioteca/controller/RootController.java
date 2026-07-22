package com.biblioteca.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String raiz(HttpSession session) {
        if (session.getAttribute("usuarioId") == null) {
            return "redirect:/login";
        }
        if ("BIBLIOTECARIO".equals(session.getAttribute("tipoUsuario"))) {
            return "redirect:/admin";
        }
        return "redirect:/catalogo";
    }
}
