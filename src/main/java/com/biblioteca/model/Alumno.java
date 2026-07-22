package com.biblioteca.model;

import com.biblioteca.model.enums.TipoUsuario;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ALUMNO")
@NoArgsConstructor
public class Alumno extends Usuario {

    public Alumno(String nombre, String apellido, String dni, String contrasenia,
                  String email, String telefono, String carrera) {
        super(nombre, apellido, dni, contrasenia, email, telefono, carrera);
    }

    @Override
    public int getMaxPrestamos() {
        return 2;
    }

    @Override
    public int getDiasDevolucion() {
        return 15;
    }

    @Override
    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.ALUMNO;
    }
}
