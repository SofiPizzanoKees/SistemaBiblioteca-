package com.biblioteca.model;

import com.biblioteca.model.enums.TipoUsuario;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("BIBLIOTECARIO")
@NoArgsConstructor
public class Bibliotecario extends Usuario {

    public Bibliotecario(String nombre, String apellido, String dni, String contrasenia,
                          String email, String telefono, String materia) {
        super(nombre, apellido, dni, contrasenia, email, telefono, materia);
    }

    // Mismas condiciones de prestamo y devolucion que Profesor, segun lo definido
    @Override
    public int getMaxPrestamos() {
        return 3;
    }

    @Override
    public int getDiasDevolucion() {
        return 30;
    }

    @Override
    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.BIBLIOTECARIO;
    }
}
