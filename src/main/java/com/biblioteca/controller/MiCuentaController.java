package com.biblioteca.controller;

import com.biblioteca.model.Usuario;
import com.biblioteca.service.NotificacionService;
import com.biblioteca.service.PrestamoService;
import com.biblioteca.service.ReservaService;
import com.biblioteca.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mi-cuenta")
public class MiCuentaController {

    private final UsuarioService usuarioService;
    private final PrestamoService prestamoService;
    private final ReservaService reservaService;
    private final NotificacionService notificacionService;

    public MiCuentaController(UsuarioService usuarioService, PrestamoService prestamoService,
                               ReservaService reservaService, NotificacionService notificacionService) {
        this.usuarioService = usuarioService;
        this.prestamoService = prestamoService;
        this.reservaService = reservaService;
        this.notificacionService = notificacionService;
    }

    // RF13 + RF17: historial de prestamos, prestamos activos, reservas y notificaciones
    @GetMapping
    public String miCuenta(HttpSession session, Model model) {
        Usuario usuario = usuarioService.usuarioDeSesion(session);

        model.addAttribute("usuario", usuario);
        model.addAttribute("prestamosActivos", prestamoService.activosDeUsuario(usuario));
        model.addAttribute("historial", prestamoService.historialDeUsuario(usuario));
        model.addAttribute("reservas", reservaService.reservasActivasDeUsuario(usuario));
        model.addAttribute("notificaciones", notificacionService.listarPorUsuario(usuario));
        return "mi-cuenta";
    }

    // RF16: cancelar una reserva propia
    @PostMapping("/reservas/{id}/cancelar")
    public String cancelarReserva(@PathVariable Long id) {
        reservaService.cancelarReserva(id);
        return "redirect:/mi-cuenta";
    }

    @PostMapping("/notificaciones/{id}/leer")
    public String marcarLeida(@PathVariable Long id) {
        notificacionService.marcarLeida(id);
        return "redirect:/mi-cuenta";
    }

    // RF17: borrar todo el historial de notificaciones propio.
    // El boton "Borrar historial" aparece en el header de TODAS las paginas
    // (catalogo, libro-detalle, mi-cuenta), asi que redirigimos a la misma
    // pagina desde donde se llamo en vez de siempre a /mi-cuenta.
    @PostMapping("/notificaciones/borrar")
    public String borrarHistorial(HttpSession session, HttpServletRequest request) {
        Usuario usuario = usuarioService.usuarioDeSesion(session);
        notificacionService.borrarHistorial(usuario);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/mi-cuenta");
    }
}