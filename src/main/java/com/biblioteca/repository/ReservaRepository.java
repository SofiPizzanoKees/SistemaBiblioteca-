package com.biblioteca.repository;

import com.biblioteca.model.Libro;
import com.biblioteca.model.Reserva;
import com.biblioteca.model.Usuario;
import com.biblioteca.model.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Fila de espera de un libro, ordenada por orden_fila (FIFO)
    List<Reserva> findByLibroAndEstadoOrderByOrdenFilaAsc(Libro libro, EstadoReserva estado);

    // Primero de la fila, para cuando se libera un ejemplar
    Optional<Reserva> findFirstByLibroAndEstadoOrderByOrdenFilaAsc(Libro libro, EstadoReserva estado);

    List<Reserva> findByUsuarioAndEstado(Usuario usuario, EstadoReserva estado);

    // Para el chequeo periodico de reservas vencidas por no retirar a tiempo
    List<Reserva> findByEstado(EstadoReserva estado);

    long countByLibroAndEstado(Libro libro, EstadoReserva estado);
}
