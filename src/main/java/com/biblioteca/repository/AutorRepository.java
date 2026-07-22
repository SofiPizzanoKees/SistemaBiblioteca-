package com.biblioteca.repository;

import com.biblioteca.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    // Para el filtro "buscar libros por autor"
    Autor findByNombreAndApellido(String nombre, String apellido);
}
