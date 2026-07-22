package com.biblioteca.service;

import com.biblioteca.model.Ejemplar;
import com.biblioteca.model.Libro;
import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;
import com.biblioteca.model.enums.EstadoEjemplar;
import com.biblioteca.model.enums.EstadoPrestamo;
import com.biblioteca.model.enums.TipoNotificacion;
import com.biblioteca.model.enums.TipoSancion;
import com.biblioteca.repository.EjemplarRepository;
import com.biblioteca.repository.PrestamoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PrestamoService {

    private static final int DIAS_LIMITE_PARA_REPOSICION = 60; // "dos meses" definidos en el enunciado

    private final PrestamoRepository prestamoRepository;
    private final EjemplarRepository ejemplarRepository;
    private final SancionService sancionService;
    private final ReservaService reservaService;
    private final NotificacionService notificacionService;

    public PrestamoService(PrestamoRepository prestamoRepository,
                            EjemplarRepository ejemplarRepository,
                            SancionService sancionService,
                            ReservaService reservaService,
                            NotificacionService notificacionService) {
        this.prestamoRepository = prestamoRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.sancionService = sancionService;
        this.reservaService = reservaService;
        this.notificacionService = notificacionService;
    }

    /**
     * Valida si un usuario puede pedir un nuevo prestamo:
     * - no debe estar bloqueado por una sancion activa
     * - no debe tener ya un prestamo vencido sin devolver
     * - no debe haber llegado a su limite de prestamos simultaneos
     */
    public boolean puedeSolicitarPrestamo(Usuario usuario) {
        if (usuario.isBloqueado()) {
            return false;
        }
        boolean tieneVencidos = prestamoRepository
                .findByUsuarioAndEstado(usuario, EstadoPrestamo.ACTIVO)
                .stream().anyMatch(Prestamo::estaVencido);
        if (tieneVencidos) {
            return false;
        }
        long activos = prestamoRepository.countByUsuarioAndEstado(usuario, EstadoPrestamo.ACTIVO);
        return activos < usuario.getMaxPrestamos();
    }

    // RF04 / RF14: registrar un prestamo
    public Prestamo registrarPrestamo(Usuario usuario, Libro libro) {
        if (!puedeSolicitarPrestamo(usuario)) {
            throw new IllegalStateException(
                    "El usuario no puede solicitar un prestamo (bloqueado, con vencidos o en su limite)");
        }

        Ejemplar ejemplar = ejemplarRepository
                .findFirstByLibroAndEstado(libro, EstadoEjemplar.DISPONIBLE)
                .orElseThrow(() -> new IllegalStateException(
                        "No hay ejemplares disponibles, el usuario deberia reservar el libro"));

        ejemplar.cambiarEstado(EstadoEjemplar.PRESTADO);
        ejemplarRepository.save(ejemplar);

        Prestamo prestamo = new Prestamo(usuario, ejemplar);
        return prestamoRepository.save(prestamo);
    }

    // RF05: registrar devolucion
    public void registrarDevolucion(Long idPrestamo, boolean ejemplarDaniado) {
        Prestamo prestamo = buscarPorId(idPrestamo);
        prestamo.registrarDevolucion();

        Ejemplar ejemplar = prestamo.getEjemplar();

        if (ejemplarDaniado) {
            ejemplar.cambiarEstado(EstadoEjemplar.DANIADO);
            sancionService.crear(prestamo, TipoSancion.REPOSICION_POR_DANIO);
        } else {
            ejemplar.cambiarEstado(EstadoEjemplar.DISPONIBLE);
        }
        ejemplarRepository.save(ejemplar);
        prestamoRepository.save(prestamo);

        // si hay alguien esperando este libro en la fila de reservas, se le asigna el turno
        if (!ejemplarDaniado) {
            reservaService.asignarSiguienteTurno(ejemplar.getLibro());
        }
    }

    /**
     * Chequeo periodico (o manual del bibliotecario, RF09) que recorre los
     * prestamos vencidos y genera la sancion de reposicion cuando superan
     * los 2 meses de atraso sin devolver el ejemplar.
     */
    public void verificarSancionesPorAtraso() {
        for (Prestamo p : prestamoRepository.buscarVencidos()) {
            long diasAtraso = ChronoUnit.DAYS.between(p.getFechaDevolucionEsperada(), LocalDate.now());
            if (diasAtraso >= DIAS_LIMITE_PARA_REPOSICION && !sancionService.estaBloqueadoPorSancion(p.getUsuario())) {
                sancionService.crear(p, TipoSancion.REPOSICION_POR_PERDIDA);
            } else {
                // aviso simple de vencimiento proximo/actual, sin llegar todavia a la sancion
                notificacionService.crear(p.getUsuario(), TipoNotificacion.VENCIMIENTO_PROXIMO,
                        "Tu prestamo de \"" + p.getEjemplar().getLibro().getTitulo() + "\" esta vencido.");
            }
        }
    }

    // RF06
    public List<Prestamo> listarActivos() {
        return prestamoRepository.findByEstado(EstadoPrestamo.ACTIVO);
    }

    // RF08
    public List<Prestamo> listarVencidos() {
        return prestamoRepository.buscarVencidos();
    }

    // RF13: prestamos activos del usuario (los que todavia no devolvio)
    public List<Prestamo> activosDeUsuario(Usuario usuario) {
        return prestamoRepository.findByUsuarioAndEstado(usuario, EstadoPrestamo.ACTIVO);
    }

    // RF13
    public List<Prestamo> historialDeUsuario(Usuario usuario) {
        return prestamoRepository.findByUsuarioAndEstado(usuario, EstadoPrestamo.DEVUELTO);
    }

    // RF10: estadisticas
    public List<Object[]> librosMasPrestados() {
        return prestamoRepository.librosMasPrestados();
    }

    public List<Object[]> usuariosConMasPrestamos() {
        return prestamoRepository.usuariosConMasPrestamos();
    }

    public Prestamo buscarPorId(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prestamo no encontrado: " + id));
    }
}
