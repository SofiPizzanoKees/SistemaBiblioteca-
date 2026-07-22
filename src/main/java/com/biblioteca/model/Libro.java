package com.biblioteca.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "libro")
@Getter
@Setter
@NoArgsConstructor
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private Integer anio;

    @Column(unique = true)
    private String isbn;

    private String idioma;

    // Baja logica: un libro dado de baja no se borra, se marca como inactivo
    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "id_editorial")
    private Editorial editorial;

    @ManyToMany
    @JoinTable(
            name = "libro_autor",
            joinColumns = @JoinColumn(name = "id_libro"),
            inverseJoinColumns = @JoinColumn(name = "id_autor")
    )
    private Set<Autor> autores = new HashSet<>();

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ejemplar> ejemplares = new HashSet<>();

    /**
     * Un libro esta disponible si tiene al menos un ejemplar en estado DISPONIBLE.
     */
    public boolean estaDisponible() {
        return ejemplares.stream()
                .anyMatch(e -> e.getEstado() == com.biblioteca.model.enums.EstadoEjemplar.DISPONIBLE);
    }
}
