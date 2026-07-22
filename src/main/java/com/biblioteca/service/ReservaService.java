package com.biblioteca.service;

import com.biblioteca.model.Ejemplar;
import com.biblioteca.model.Libro;
import com.biblioteca.model.Reserva;
import com.biblioteca.model.Usuario;
import com.biblioteca.model.enums.EstadoEjemplar;
import com.biblioteca.model.enums.EstadoReserva;
import com.biblioteca.model.enums.TipoNotificacion;
import com.biblioteca.repository.EjemplarRepository;
import com.biblioteca.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final EjemplarRepository ejemplarRepository;
    private final NotificacionService notificacionService;
    private final FeriadoService feriadoService;

    public ReservaService(ReservaRepository reservaRepository,
                           EjemplarRepository ejemplarRepository,
                           NotificacionService notificacionService,
                           FeriadoService feriadoService) {
        this.reservaRepository = reservaRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.notificacionService = notificacionService;
        this.feriadoService = feriadoService;
    }

    // RF15: reservar un libro sin ejemplares disponibles, entra a la fila FIFO
    public Reserva crearReserva(Usuario usuario, Libro libro) {
        if (libro.estaDisponible()) {
            throw new IllegalStateException("El libro tiene ejemplares disponibles, no hace falta reservar");
        }
        long enFila = reservaRepository.countByLibroAndEstado(libro, EstadoReserva.EN_ESPERA);
        Reserva reserva = new Reserva(usuario, libro, (int) enFila + 1);
        return reservaRepository.save(reserva);
    }

    // RF16: el usuario cancela su propia reserva
    public void cancelarReserva(Long idReserva) {
        Reserva reserva = buscarPorId(idReserva);
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        // si tenia un ejemplar apartado esperando retiro, se lo pasamos al siguiente
        asignarSiguienteTurno(reserva.getLibro());
    }

    /**
     * Se llama cuando un ejemplar de un libro queda disponible (por devolucion).
     * Le asigna el turno al primero de la fila FIFO, si hay alguien esperando.
     */
    public void asignarSiguienteTurno(Libro libro) {
        Optional<Reserva> siguiente = reservaRepository
                .findFirstByLibroAndEstadoOrderByOrdenFilaAsc(libro, EstadoReserva.EN_ESPERA);

        if (siguiente.isEmpty()) {
            return; // nadie esperando, el ejemplar queda DISPONIBLE tal cual
        }

        Reserva reserva = siguiente.get();
        reserva.setEstado(EstadoReserva.DISPONIBLE_PARA_RETIRO);
        reserva.setFechaLimiteRetiro(sumar48HorasHabiles(LocalDate.now()));
        reservaRepository.save(reserva);

        // el ejemplar queda apartado, no lo puede tomar otro usuario
        ejemplarRepository.findFirstByLibroAndEstado(libro, EstadoEjemplar.DISPONIBLE)
                .ifPresent(ej -> {
                    ej.cambiarEstado(EstadoEjemplar.RESERVADO);
                    ejemplarRepository.save(ej);
                });

        notificacionService.crear(reserva.getUsuario(), TipoNotificacion.RESERVA_DISPONIBLE,
                "Tu reserva de \"" + libro.getTitulo() + "\" ya esta disponible para retirar.");
    }

    // Chequeo periodico (o manual del bibliotecario) de reservas vencidas por no retirar a tiempo
    public void verificarReservasVencidas() {
        List<Reserva> pendientes = reservaRepository.findByEstado(EstadoReserva.DISPONIBLE_PARA_RETIRO);
        for (Reserva r : pendientes) {
            if (r.getFechaLimiteRetiro() != null && LocalDate.now().isAfter(r.getFechaLimiteRetiro())) {
                r.setEstado(EstadoReserva.VENCIDA);
                reservaRepository.save(r);
                asignarSiguienteTurno(r.getLibro());
            }
        }
    }

    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada: " + id));
    }

    // RF13/RF17: reservas activas del usuario (en espera o ya disponibles para retirar)
    public List<Reserva> reservasActivasDeUsuario(Usuario usuario) {
        List<Reserva> resultado = new java.util.ArrayList<>();
        resultado.addAll(reservaRepository.findByUsuarioAndEstado(usuario, EstadoReserva.EN_ESPERA));
        resultado.addAll(reservaRepository.findByUsuarioAndEstado(usuario, EstadoReserva.DISPONIBLE_PARA_RETIRO));
        return resultado;
    }

    /**
     * Suma 48hs habiles a una fecha, sin contar sabado, domingo ni
     * feriados nacionales (el instituto no abre esos dias).
     */
    private LocalDate sumar48HorasHabiles(LocalDate desde) {
        LocalDate fecha = desde;
        int diasHabilesRestantes = 2;
        while (diasHabilesRestantes > 0) {
            fecha = fecha.plusDays(1);
            if (feriadoService.esDiaHabil(fecha)) {
                diasHabilesRestantes--;
            }
        }
        return fecha;
    }
}
