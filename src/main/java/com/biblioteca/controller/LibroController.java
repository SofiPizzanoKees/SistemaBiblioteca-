package com.biblioteca.controller;

import com.biblioteca.model.Libro;
import com.biblioteca.model.Usuario;
import com.biblioteca.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/catalogo")
public class LibroController {

    private final LibroService libroService;
    private final CategoriaService categoriaService;
    private final PrestamoService prestamoService;
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    public LibroController(LibroService libroService, CategoriaService categoriaService,
                            PrestamoService prestamoService, ReservaService reservaService,
                            UsuarioService usuarioService) {
        this.libroService = libroService;
        this.categoriaService = categoriaService;
        this.prestamoService = prestamoService;
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
    }

    // RF11: buscar libros, con los filtros de titulo, categoria, autor y disponibilidad
    @GetMapping
    public String listar(@RequestParam(required = false) String titulo,
                          @RequestParam(required = false) String categoria,
                          @RequestParam(required = false) String autorNombre,
                          @RequestParam(required = false) String autorApellido,
                          @RequestParam(required = false) Boolean soloDisponibles,
                          Model model) {

        List<Libro> resultado;
        if (titulo != null && !titulo.isBlank()) {
            resultado = libroService.buscarPorTitulo(titulo);
        } else if (categoria != null && !categoria.isBlank()) {
            resultado = libroService.buscarPorCategoria(categoria);
        } else if (autorNombre != null && !autorNombre.isBlank() && autorApellido != null && !autorApellido.isBlank()) {
            resultado = libroService.buscarPorAutor(autorNombre, autorApellido);
        } else if (Boolean.TRUE.equals(soloDisponibles)) {
            resultado = libroService.buscarDisponibles();
        } else {
            resultado = libroService.listarActivos();
        }

        model.addAttribute("libros", resultado);
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "catalogo";
    }

    // RF12: consultar disponibilidad puntual + detalle
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Libro libro = libroService.buscarPorId(id);
        model.addAttribute("libro", libro);
        model.addAttribute("disponible", libro.estaDisponible());
        model.addAttribute("ejemplaresDisponibles", libroService.contarEjemplaresDisponibles(libro));
        return "libro-detalle";
    }

    // RF14: solicitar un prestamo
    @PostMapping("/{id}/prestamo")
    public String solicitarPrestamo(@PathVariable Long id, HttpSession session, Model model) {
        Libro libro = libroService.buscarPorId(id);
        Usuario usuario = usuarioService.usuarioDeSesion(session);
        try {
            prestamoService.registrarPrestamo(usuario, libro);
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("libro", libro);
            model.addAttribute("disponible", libro.estaDisponible());
            model.addAttribute("ejemplaresDisponibles", libroService.contarEjemplaresDisponibles(libro));
            return "libro-detalle";
        }
        return "redirect:/mi-cuenta";
    }

    // RF15: reservar un libro sin ejemplares disponibles
    @PostMapping("/{id}/reserva")
    public String reservar(@PathVariable Long id, HttpSession session, Model model) {
        Libro libro = libroService.buscarPorId(id);
        Usuario usuario = usuarioService.usuarioDeSesion(session);
        try {
            reservaService.crearReserva(usuario, libro);
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("libro", libro);
            model.addAttribute("disponible", libro.estaDisponible());
            model.addAttribute("ejemplaresDisponibles", libroService.contarEjemplaresDisponibles(libro));
            return "libro-detalle";
        }
        return "redirect:/mi-cuenta";
    }
}
