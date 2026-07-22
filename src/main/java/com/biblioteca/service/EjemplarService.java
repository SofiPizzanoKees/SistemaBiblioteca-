package com.biblioteca.service;

import com.biblioteca.model.Ejemplar;
import com.biblioteca.model.enums.EstadoEjemplar;
import com.biblioteca.repository.EjemplarRepository;
import org.springframework.stereotype.Service;

@Service
public class EjemplarService {

    private final EjemplarRepository ejemplarRepository;

    public EjemplarService(EjemplarRepository ejemplarRepository) {
        this.ejemplarRepository = ejemplarRepository;
    }

    public Ejemplar buscarPorId(Long id) {
        return ejemplarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ejemplar no encontrado: " + id));
    }

    /**
     * El bibliotecario marca un ejemplar DANIADO como reparado:
     * vuelve a quedar disponible para prestamo.
     */
    public void reparar(Long idEjemplar) {
        Ejemplar ejemplar = buscarPorId(idEjemplar);
        if (ejemplar.getEstado() != EstadoEjemplar.DANIADO) {
            throw new IllegalStateException("Solo se puede reparar un ejemplar en estado DANIADO");
        }
        ejemplar.cambiarEstado(EstadoEjemplar.DISPONIBLE);
        ejemplarRepository.save(ejemplar);
    }

    /**
     * El bibliotecario decide que el ejemplar no se puede reparar:
     * lo saca definitivamente de circulacion.
     */
    public void darDeBaja(Long idEjemplar) {
        Ejemplar ejemplar = buscarPorId(idEjemplar);
        ejemplar.cambiarEstado(EstadoEjemplar.DE_BAJA);
        ejemplarRepository.save(ejemplar);
    }
}
