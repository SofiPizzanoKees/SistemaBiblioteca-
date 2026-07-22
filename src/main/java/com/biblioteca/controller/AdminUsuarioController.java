package com.biblioteca.controller;

import com.biblioteca.model.Alumno;
import com.biblioteca.model.Bibliotecario;
import com.biblioteca.model.Profesor;
import com.biblioteca.model.Usuario;
import com.biblioteca.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarActivos());
        return "admin/usuarios";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo() {
        return "admin/usuario-form";
    }

    // RF03: alta de usuario. El tipo define que subclase se instancia (Alumno/Profesor/Bibliotecario)
    @PostMapping
    public String guardar(@RequestParam String tipoUsuario, @RequestParam String nombre,
                           @RequestParam String apellido, @RequestParam String dni,
                           @RequestParam String contrasenia, @RequestParam(required = false) String email,
                           @RequestParam(required = false) String telefono,
                           @RequestParam(required = false) String materia,
                           Model model) {

        Usuario usuario = switch (tipoUsuario) {
            case "ALUMNO" -> new Alumno(nombre, apellido, dni, contrasenia, email, telefono, materia);
            case "PROFESOR" -> new Profesor(nombre, apellido, dni, contrasenia, email, telefono, materia);
            case "BIBLIOTECARIO" -> new Bibliotecario(nombre, apellido, dni, contrasenia, email, telefono, materia);
            default -> throw new IllegalArgumentException("Tipo de usuario invalido: " + tipoUsuario);
        };

        try {
            usuarioService.guardar(usuario);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "admin/usuario-form";
        }
        return "redirect:/admin/usuarios";
    }

    // RF03: baja logica
    @PostMapping("/{id}/baja")
    public String darDeBaja(@PathVariable Long id) {
        usuarioService.darDeBaja(id);
        return "redirect:/admin/usuarios";
    }
}
