package com.biblioteca.model;

import com.biblioteca.model.enums.EstadoEjemplar;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ejemplar")
@Getter
@Setter
@NoArgsConstructor
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_libro", nullable = false)
    private Libro libro;

    @Column(name = "numero_inventario", nullable = false, unique = true)
    private String numeroInventario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEjemplar estado = EstadoEjemplar.DISPONIBLE;

    public Ejemplar(Libro libro, String numeroInventario) {
        this.libro = libro;
        this.numeroInventario = numeroInventario;
        this.estado = EstadoEjemplar.DISPONIBLE;
    }

    public void cambiarEstado(EstadoEjemplar nuevoEstado) {
        this.estado = nuevoEstado;
    }
}
