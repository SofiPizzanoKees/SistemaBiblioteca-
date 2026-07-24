package com.biblioteca.repository;

import com.biblioteca.model.Notificacion;
import com.biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // RF17: ver notificaciones propias, mas nuevas primero
    List<Notificacion> findByUsuarioOrderByFechaDesc(Usuario usuario);

    long countByUsuarioAndLeidaFalse(Usuario usuario);

    // NUEVO: para el desplegable del header (las ultimas 8, ya ordenadas)
    List<Notificacion> findTop8ByUsuarioOrderByFechaDesc(Usuario usuario);

    // NUEVO: para el boton "Borrar historial"
    void deleteByUsuario(Usuario usuario);
}