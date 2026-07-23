package com.biblioteca.config;

import com.biblioteca.model.*;
import com.biblioteca.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final AutorRepository autorRepository;
    private final EditorialRepository editorialRepository;
    private final CategoriaRepository categoriaRepository;
    private final LibroRepository libroRepository;
    private final EjemplarRepository ejemplarRepository;

    public DataInitializer(UsuarioRepository usuarioRepository, AutorRepository autorRepository,
                            EditorialRepository editorialRepository, CategoriaRepository categoriaRepository,
                            LibroRepository libroRepository, EjemplarRepository ejemplarRepository) {
        this.usuarioRepository = usuarioRepository;
        this.autorRepository = autorRepository;
        this.editorialRepository = editorialRepository;
        this.categoriaRepository = categoriaRepository;
        this.libroRepository = libroRepository;
        this.ejemplarRepository = ejemplarRepository;
    }

    @Override
    public void run(String... args) {
        // Si ya hay usuarios cargados, no volvemos a insertar (evita duplicados en cada reinicio)
        if (usuarioRepository.count() > 0) {
            return;
        }

        // --- Usuarios de prueba ---
        Bibliotecario bibliotecario = new Bibliotecario(
                "Admin", "Biblioteca", "12345678", "admin123",
                "admin@biblioteca.edu", "011-1234-5678", null);
        usuarioRepository.save(bibliotecario);

        Alumno alumno = new Alumno(
                "Juan", "Pérez", "87654321", "juan123",
                "juan.perez@alumno.edu", "011-9876-5432", "Ingeniería en Sistemas");
        usuarioRepository.save(alumno);

        Profesor profesor = new Profesor(
                "Carlos", "Rodríguez", "55667788", "carlos123",
                "carlos.rodriguez@profesor.edu", "011-5566-7788", "Algoritmos y Estructuras de Datos");
        usuarioRepository.save(profesor);

        // --- Autores ---
        Autor garciaMarquez = autorRepository.save(new Autor("Gabriel", "García Márquez", "Colombiana"));
        Autor rowling = autorRepository.save(new Autor("J.K.", "Rowling", "Británica"));

        // --- Editoriales ---
        Editorial sudamericana = editorialRepository.save(
                new Editorial("Editorial Sudamericana", "Argentina", "011-4321-5678"));
        Editorial planeta = editorialRepository.save(
                new Editorial("Planeta", "Colombia", "571-567-8901"));

        // --- Categorias ---
        Categoria novela = categoriaRepository.save(new Categoria("Novela"));
        Categoria fantasia = categoriaRepository.save(new Categoria("Fantasía"));

        // --- Libros con sus ejemplares ---
        Libro cienAnios = new Libro();
        cienAnios.setTitulo("Cien años de soledad");
        cienAnios.setIsbn("978-987-566-123-4");
        cienAnios.setAnio(1967);
        cienAnios.setIdioma("Español");
        cienAnios.setCategoria(novela);
        cienAnios.setEditorial(sudamericana);
        cienAnios.getAutores().add(garciaMarquez);
        libroRepository.save(cienAnios);
        ejemplarRepository.save(new Ejemplar(cienAnios, "LIB-001-01"));
        ejemplarRepository.save(new Ejemplar(cienAnios, "LIB-001-02"));

        Libro harryPotter = new Libro();
        harryPotter.setTitulo("Harry Potter y la piedra filosofal");
        harryPotter.setIsbn("978-987-566-234-5");
        harryPotter.setAnio(1997);
        harryPotter.setIdioma("Español");
        harryPotter.setCategoria(fantasia);
        harryPotter.setEditorial(planeta);
        harryPotter.getAutores().add(rowling);
        libroRepository.save(harryPotter);
        ejemplarRepository.save(new Ejemplar(harryPotter, "LIB-002-01"));

        System.out.println("=======================================================");
        System.out.println(" Datos de prueba cargados. Usuarios para loguearse:");
        System.out.println(" Bibliotecario -> DNI: 12345678 / Contraseña: admin123");
        System.out.println(" Alumno        -> DNI: 87654321 / Contraseña: juan123");
        System.out.println(" Profesor      -> DNI: 55667788 / Contraseña: carlos123");
        System.out.println("=======================================================");
    }
}