package com.biblioteca.service;

import com.biblioteca.dto.NotificacionDTO;
import com.biblioteca.model.Notificacion;
import com.biblioteca.model.Usuario;
import com.biblioteca.model.enums.TipoNotificacion;
import com.biblioteca.repository.NotificacionRepository;
import com.biblioteca.util.FechaUtil;
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

    // RF17: ver notificaciones propias (listado completo, para una pantalla dedicada si la tenes)
    public List<Notificacion> listarPorUsuario(Usuario usuario) {
        return notificacionRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    // Usado por el ControllerAdvice para el desplegable del header
    public List<NotificacionDTO> listarParaDropdown(Usuario usuario) {
        return notificacionRepository
                .findTop8ByUsuarioOrderByFechaDesc(usuario)
                .stream()
                .map(n -> new NotificacionDTO(
                        n.getId(),
                        n.getMensaje(),
                        FechaUtil.formatearNotificacion(n.getFecha()),
                        n.isLeida()))
                .toList();
    }

    // Usado por el ControllerAdvice para el numerito del badge
    public long contarNoLeidas(Usuario usuario) {
        return notificacionRepository.countByUsuarioAndLeidaFalse(usuario);
    }

    public void marcarLeida(Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificacion no encontrada: " + id));
        n.marcarLeida();
        notificacionRepository.save(n);
    }

    // Borra todo el historial de notificaciones del usuario logueado
    public void borrarHistorial(Usuario usuario) {
        notificacionRepository.deleteByUsuario(usuario);
    }
}