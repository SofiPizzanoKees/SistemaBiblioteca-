package com.biblioteca.service;

import com.biblioteca.model.Notificacion;
import com.biblioteca.model.Usuario;
import com.biblioteca.model.enums.TipoNotificacion;
import com.biblioteca.repository.NotificacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public Notificacion crear(Usuario usuario, TipoNotificacion tipo, String mensaje) {
        return notificacionRepository.save(new Notificacion(usuario, tipo, mensaje));
    }

    // RF17: ver notificaciones propias
    public List<Notificacion> listarPorUsuario(Usuario usuario) {
        return notificacionRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    public void marcarLeida(Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificacion no encontrada: " + id));
        n.marcarLeida();
        notificacionRepository.save(n);
    }
}
