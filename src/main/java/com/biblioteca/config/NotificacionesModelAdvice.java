package com.biblioteca.config;

import com.biblioteca.model.Notificacion;
import com.biblioteca.model.Usuario;
import com.biblioteca.service.NotificacionService;
import com.biblioteca.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Agrega las notificaciones del usuario logueado (Alumno/Profesor) al modelo
 * de cualquier vista, para poder mostrar el desplegable de notificaciones
 * en el header sin repetir la consulta en cada controller.
 */
@ControllerAdvice
public class NotificacionesModelAdvice {

    private final UsuarioService usuarioService;
    private final NotificacionService notificacionService;

    public NotificacionesModelAdvice(UsuarioService usuarioService, NotificacionService notificacionService) {
        this.usuarioService = usuarioService;
        this.notificacionService = notificacionService;
    }

    @ModelAttribute
    public void agregarNotificaciones(HttpSession session, Model model) {
        Object usuarioId = session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return; // no hay sesion (ej. pagina de login)
        }

        // El panel del Bibliotecario tiene su propio header, no necesita este desplegable
        if ("BIBLIOTECARIO".equals(session.getAttribute("tipoUsuario"))) {
            return;
        }

        Usuario usuario = usuarioService.buscarPorId((Long) usuarioId);
        List<Notificacion> notificaciones = notificacionService.listarPorUsuario(usuario);
        long noLeidas = notificaciones.stream().filter(n -> !n.isLeida()).count();

        model.addAttribute("notificacionesDropdown", notificaciones);
        model.addAttribute("notificacionesNoLeidas", noLeidas);
    }
}