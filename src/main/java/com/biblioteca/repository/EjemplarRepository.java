package com.biblioteca.repository;

import com.biblioteca.model.Ejemplar;
import com.biblioteca.model.Libro;
import com.biblioteca.model.enums.EstadoEjemplar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EjemplarRepository extends JpaRepository<Ejemplar, Long> {

    List<Ejemplar> findByLibroAndEstado(Libro libro, EstadoEjemplar estado);

    // Trae cualquier ejemplar disponible de un libro (para registrar un prestamo)
    Optional<Ejemplar> findFirstByLibroAndEstado(Libro libro, EstadoEjemplar estado);

    long countByLibroAndEstado(Libro libro, EstadoEjemplar estado);
}
