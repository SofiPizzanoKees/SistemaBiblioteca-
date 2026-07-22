package com.biblioteca.model;

import com.biblioteca.model.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "reserva")
@Getter
@Setter
@NoArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_libro", nullable = false)
    private Libro libro;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDate fechaReserva;

    // Se calcula cuando el ejemplar queda disponible para este usuario:
    // 48hs habiles (sin contar fin de semana) desde ese momento
    @Column(name = "fecha_limite_retiro")
    private LocalDate fechaLimiteRetiro;

    // Posicion en la fila FIFO para este libro
    @Column(name = "orden_fila", nullable = false)
    private int ordenFila;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado = EstadoReserva.EN_ESPERA;

    public Reserva(Usuario usuario, Libro libro, int ordenFila) {
        this.usuario = usuario;
        this.libro = libro;
        this.fechaReserva = LocalDate.now();
        this.ordenFila = ordenFila;
        this.estado = EstadoReserva.EN_ESPERA;
    }
}
