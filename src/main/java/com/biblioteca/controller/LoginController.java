package com.biblioteca.controller;

import com.biblioteca.model.Usuario;
import com.biblioteca.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    // RF01
    @PostMapping("/login")
    public String login(@RequestParam String dni, @RequestParam String contrasenia,
                         HttpSession session, Model model) {

        Optional<Usuario> usuario = usuarioService.login(dni, contrasenia);

        if (usuario.isEmpty()) {
            model.addAttribute("error", "DNI o contraseña incorrectos");
            return "login";
        }

        session.setAttribute("usuarioId", usuario.get().getId());
        session.setAttribute("nombreUsuario", usuario.get().getNombre() + " " + usuario.get().getApellido());
        session.setAttribute("tipoUsuario", usuario.get().getTipoUsuario().name());

        if (usuario.get().getTipoUsuario().name().equals("BIBLIOTECARIO")) {
            return "redirect:/admin";
        }
        return "redirect:/catalogo";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // Pantallas de "recuperar contraseña": son solo de exhibicion, no estan
    // conectadas a ningun service ni envian correos ni validan nada real.
    @GetMapping("/recuperar-contrasenia")
    public String mostrarRecuperarCorreo() {
        return "recuperar-correo";
    }

    @GetMapping("/recuperar-contrasenia/codigo")
    public String mostrarRecuperarCodigo() {
        return "recuperar-codigo";
    }
}