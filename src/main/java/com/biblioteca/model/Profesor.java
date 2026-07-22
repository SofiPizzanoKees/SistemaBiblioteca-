package com.biblioteca.model;

import com.biblioteca.model.enums.TipoUsuario;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("PROFESOR")
@NoArgsConstructor
public class Profesor extends Usuario {

    public Profesor(String nombre, String apellido, String dni, String contrasenia,
                     String email, String telefono, String materiaQueDicta) {
        super(nombre, apellido, dni, contrasenia, email, telefono, materiaQueDicta);
    }

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
        return TipoUsuario.PROFESOR;
    }
}
