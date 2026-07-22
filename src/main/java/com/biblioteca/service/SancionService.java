package com.biblioteca.service;

import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Sancion;
import com.biblioteca.model.Usuario;
import com.biblioteca.model.enums.EstadoSancion;
import com.biblioteca.model.enums.TipoSancion;
import com.biblioteca.repository.SancionRepository;
import org.springframework.stereotype.Service;

@Service
public class SancionService {

    private final SancionRepository sancionRepository;

    public SancionService(SancionRepository sancionRepository) {
        this.sancionRepository = sancionRepository;
    }

    /**
     * Crea una sancion para un prestamo puntual y bloquea al usuario
     * hasta que la resuelva (reponer el libro).
     */
    public Sancion crear(Prestamo prestamo, TipoSancion tipo) {
        Sancion sancion = new Sancion(prestamo, tipo);
        sancionRepository.save(sancion);

        Usuario usuario = prestamo.getUsuario();
        usuario.setBloqueado(true);
        return sancion;
    }

    // RF09: el bibliotecario marca la reposicion como realizada
    public void resolver(Long idSancion) {
        Sancion sancion = sancionRepository.findById(idSancion)
                .orElseThrow(() -> new IllegalArgumentException("Sancion no encontrada: " + idSancion));
        sancion.resolver();
        sancionRepository.save(sancion);

        Usuario usuario = sancion.getPrestamo().getUsuario();
        boolean tieneOtrasActivas = sancionRepository
                .existsByPrestamo_UsuarioAndEstado(usuario, EstadoSancion.ACTIVA);
        usuario.setBloqueado(tieneOtrasActivas);
    }

    public boolean estaBloqueadoPorSancion(Usuario usuario) {
        return sancionRepository.existsByPrestamo_UsuarioAndEstado(usuario, EstadoSancion.ACTIVA);
    }

    // RF09: listado de sanciones activas para que el bibliotecario las gestione
    public java.util.List<Sancion> listarActivas() {
        return sancionRepository.findByEstado(EstadoSancion.ACTIVA);
    }
}
