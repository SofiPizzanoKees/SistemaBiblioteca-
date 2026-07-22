package com.biblioteca.model;

import com.biblioteca.model.enums.EstadoSancion;
import com.biblioteca.model.enums.TipoSancion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "sancion")
@Getter
@Setter
@NoArgsConstructor
public class Sancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_prestamo", nullable = false)
    private Prestamo prestamo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSancion tipo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_resolucion")
    private LocalDate fechaResolucion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSancion estado = EstadoSancion.ACTIVA;

    public Sancion(Prestamo prestamo, TipoSancion tipo) {
        this.prestamo = prestamo;
        this.tipo = tipo;
        this.fechaInicio = LocalDate.now();
        this.estado = EstadoSancion.ACTIVA;
    }

    public void resolver() {
        this.fechaResolucion = LocalDate.now();
        this.estado = EstadoSancion.RESUELTA;
    }
}
