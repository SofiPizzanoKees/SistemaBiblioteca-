package com.biblioteca.repository;

import com.biblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    // RF11: buscar libros por titulo (contiene, sin importar mayus/minus)
    List<Libro> findByTituloContainingIgnoreCaseAndActivoTrue(String titulo);

    // Filtro por categoria
    List<Libro> findByCategoria_NombreIgnoreCaseAndActivoTrue(String nombreCategoria);

    // Filtro por autor (nombre y apellido)
    @Query("""
            SELECT l FROM Libro l JOIN l.autores a
            WHERE LOWER(a.nombre) = LOWER(:nombre)
            AND LOWER(a.apellido) = LOWER(:apellido)
            AND l.activo = true
            """)
    List<Libro> buscarPorAutor(@Param("nombre") String nombre, @Param("apellido") String apellido);

    // Filtro/listado: solo libros que tienen al menos un ejemplar disponible
    @Query("""
            SELECT DISTINCT l FROM Libro l JOIN l.ejemplares e
            WHERE e.estado = com.biblioteca.model.enums.EstadoEjemplar.DISPONIBLE
            AND l.activo = true
            """)
    List<Libro> buscarDisponibles();

    List<Libro> findByActivoTrue();
}
