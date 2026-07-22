package com.biblioteca.controller;

import com.biblioteca.model.*;
import com.biblioteca.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/libros")
public class AdminLibroController {

    private final LibroService libroService;
    private final AutorService autorService;
    private final EditorialService editorialService;
    private final CategoriaService categoriaService;
    private final EjemplarService ejemplarService;

    public AdminLibroController(LibroService libroService, AutorService autorService,
                                 EditorialService editorialService, CategoriaService categoriaService,
                                 EjemplarService ejemplarService) {
        this.libroService = libroService;
        this.autorService = autorService;
        this.editorialService = editorialService;
        this.categoriaService = categoriaService;
        this.ejemplarService = ejemplarService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("libros", libroService.listarActivos());
        return "admin/libros";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("libro", new Libro());
        cargarListasAuxiliares(model);
        return "admin/libro-form";
    }

    // RF02: alta de libro
    @PostMapping
    public String guardar(@RequestParam String titulo, @RequestParam(required = false) Integer anio,
                           @RequestParam(required = false) String isbn, @RequestParam(required = false) String idioma,
                           @RequestParam(required = false) Long idCategoria, @RequestParam(required = false) Long idEditorial,
                           @RequestParam(required = false) List<Long> idsAutores) {

        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setAnio(anio);
        libro.setIsbn(isbn);
        libro.setIdioma(idioma);
        if (idCategoria != null) libro.setCategoria(categoriaService.buscarPorId(idCategoria));
        if (idEditorial != null) libro.setEditorial(editorialService.buscarPorId(idEditorial));

        Set<Autor> autores = new HashSet<>();
        if (idsAutores != null) {
            idsAutores.forEach(id -> autores.add(autorService.buscarPorId(id)));
        }
        libro.setAutores(autores);

        libroService.guardar(libro);
        return "redirect:/admin/libros";
    }

    // RF02: baja logica
    @PostMapping("/{id}/baja")
    public String darDeBaja(@PathVariable Long id) {
        libroService.darDeBaja(id);
        return "redirect:/admin/libros";
    }

    // RF02: gestion de ejemplares (copias fisicas) de un libro
    @GetMapping("/{id}/ejemplares")
    public String ejemplares(@PathVariable Long id, Model model) {
        Libro libro = libroService.buscarPorId(id);
        model.addAttribute("libro", libro);
        return "admin/libro-ejemplares";
    }

    @PostMapping("/{id}/ejemplares")
    public String agregarEjemplar(@PathVariable Long id, @RequestParam String numeroInventario) {
        Libro libro = libroService.buscarPorId(id);
        libroService.agregarEjemplar(libro, numeroInventario);
        return "redirect:/admin/libros/" + id + "/ejemplares";
    }

    @PostMapping("/ejemplares/{idEjemplar}/reparar")
    public String repararEjemplar(@PathVariable Long idEjemplar) {
        Ejemplar ejemplar = ejemplarService.buscarPorId(idEjemplar);
        ejemplarService.reparar(idEjemplar);
        return "redirect:/admin/libros/" + ejemplar.getLibro().getId() + "/ejemplares";
    }

    @PostMapping("/ejemplares/{idEjemplar}/baja")
    public String darDeBajaEjemplar(@PathVariable Long idEjemplar) {
        Ejemplar ejemplar = ejemplarService.buscarPorId(idEjemplar);
        ejemplarService.darDeBaja(idEjemplar);
        return "redirect:/admin/libros/" + ejemplar.getLibro().getId() + "/ejemplares";
    }

    // altas rapidas de autor / editorial / categoria (usadas desde el formulario de libro)
    @PostMapping("/autores")
    public String nuevoAutor(@RequestParam String nombre, @RequestParam String apellido,
                              @RequestParam(required = false) String nacionalidad, Model model) {
        autorService.guardar(new Autor(nombre, apellido, nacionalidad));
        model.addAttribute("libro", new Libro());
        cargarListasAuxiliares(model);
        return "admin/libro-form";
    }

    @PostMapping("/editoriales")
    public String nuevaEditorial(@RequestParam String nombre, @RequestParam(required = false) String pais,
                                  @RequestParam(required = false) String contacto, Model model) {
        editorialService.guardar(new Editorial(nombre, pais, contacto));
        model.addAttribute("libro", new Libro());
        cargarListasAuxiliares(model);
        return "admin/libro-form";
    }

    @PostMapping("/categorias")
    public String nuevaCategoria(@RequestParam String nombre, Model model) {
        categoriaService.guardar(new Categoria(nombre));
        model.addAttribute("libro", new Libro());
        cargarListasAuxiliares(model);
        return "admin/libro-form";
    }

    private void cargarListasAuxiliares(Model model) {
        model.addAttribute("autores", autorService.listarTodos());
        model.addAttribute("editoriales", editorialService.listarTodas());
        model.addAttribute("categorias", categoriaService.listarTodas());
    }
}
