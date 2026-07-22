package com.biblioteca.service;

import com.biblioteca.model.Ejemplar;
import com.biblioteca.model.Libro;
import com.biblioteca.model.enums.EstadoEjemplar;
import com.biblioteca.repository.EjemplarRepository;
import com.biblioteca.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final EjemplarRepository ejemplarRepository;

    public LibroService(LibroRepository libroRepository, EjemplarRepository ejemplarRepository) {
        this.libroRepository = libroRepository;
        this.ejemplarRepository = ejemplarRepository;
    }

    // RF02: alta de libro
    public Libro guardar(Libro libro) {
        return libroRepository.save(libro);
    }

    // RF02: agregar un ejemplar (copia fisica) a un libro ya existente
    public Ejemplar agregarEjemplar(Libro libro, String numeroInventario) {
        Ejemplar ejemplar = new Ejemplar(libro, numeroInventario);
        return ejemplarRepository.save(ejemplar);
    }

    // RF02: baja logica, no se borra de la base
    public void darDeBaja(Long idLibro) {
        Libro libro = buscarPorId(idLibro);
        libro.setActivo(false);
        libroRepository.save(libro);
    }

    public Libro buscarPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + id));
    }

    public List<Libro> listarActivos() {
        return libroRepository.findByActivoTrue();
    }

    // RF11 + filtro por titulo
    public List<Libro> buscarPorTitulo(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCaseAndActivoTrue(titulo);
    }

    // Filtro por categoria
    public List<Libro> buscarPorCategoria(String nombreCategoria) {
        return libroRepository.findByCategoria_NombreIgnoreCaseAndActivoTrue(nombreCategoria);
    }

    // Filtro por autor
    public List<Libro> buscarPorAutor(String nombre, String apellido) {
        return libroRepository.buscarPorAutor(nombre, apellido);
    }

    // RF07 + filtro por disponibilidad
    public List<Libro> buscarDisponibles() {
        return libroRepository.buscarDisponibles();
    }

    // RF12: consultar disponibilidad puntual de un libro
    public boolean estaDisponible(Long idLibro) {
        return buscarPorId(idLibro).estaDisponible();
    }

    public long contarEjemplaresDisponibles(Libro libro) {
        return ejemplarRepository.countByLibroAndEstado(libro, EstadoEjemplar.DISPONIBLE);
    }
}
