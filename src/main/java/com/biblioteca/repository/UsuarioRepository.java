package com.biblioteca.repository;

import com.biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Para el login: se ingresa con DNI + contraseña
    Optional<Usuario> findByDniAndActivoTrue(String dni);

    boolean existsByDni(String dni);

    List<Usuario> findByActivoTrue();
}
