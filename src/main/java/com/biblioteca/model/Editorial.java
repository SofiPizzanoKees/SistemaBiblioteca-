package com.biblioteca.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "editorial")
@Getter
@Setter
@NoArgsConstructor
public class Editorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String pais;

    private String contacto;

    public Editorial(String nombre, String pais, String contacto) {
        this.nombre = nombre;
        this.pais = pais;
        this.contacto = contacto;
    }
}
