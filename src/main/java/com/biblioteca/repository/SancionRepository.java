package com.biblioteca.repository;

import com.biblioteca.model.Sancion;
import com.biblioteca.model.Usuario;
import com.biblioteca.model.enums.EstadoSancion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SancionRepository extends JpaRepository<Sancion, Long> {

    // Para saber si un usuario tiene sanciones activas (bloqueo)
    List<Sancion> findByPrestamo_UsuarioAndEstado(Usuario usuario, EstadoSancion estado);

    boolean existsByPrestamo_UsuarioAndEstado(Usuario usuario, EstadoSancion estado);

    java.util.List<Sancion> findByEstado(EstadoSancion estado);
}
