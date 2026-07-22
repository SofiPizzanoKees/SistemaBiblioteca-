package com.biblioteca.repository;

import com.biblioteca.model.Notificacion;
import com.biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // RF17: ver notificaciones propias, mas nuevas primero
    List<Notificacion> findByUsuarioOrderByFechaDesc(Usuario usuario);

    long countByUsuarioAndLeidaFalse(Usuario usuario);
}
