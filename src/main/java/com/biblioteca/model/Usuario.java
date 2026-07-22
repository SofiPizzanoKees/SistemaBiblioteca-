package com.biblioteca.model;

import com.biblioteca.model.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase base de los 3 perfiles del sistema. Usa herencia SINGLE_TABLE:
 * las 3 subclases (Alumno, Profesor, Bibliotecario) se guardan todas en
 * la misma tabla "usuario", distinguidas por la columna discriminadora
 * "tipo_usuario". Esto nos permite usar polimorfismo en el codigo Java
 * (cada subclase define sus propios limites de prestamo) sin necesidad
 * de tener tablas separadas en la base de datos.
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false)
    private String contrasenia;

    private String email;

    private String telefono;

    // Carrera (alumno) o materia que dicta (profesor)
    private String materia;

    // Baja logica
    @Column(nullable = false)
    private boolean activo = true;

    // true mientras tenga un ejemplar sin devolver o una sancion activa
    @Column(nullable = false)
    private boolean bloqueado = false;

    protected Usuario(String nombre, String apellido, String dni, String contrasenia,
                       String email, String telefono, String materia) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.contrasenia = contrasenia;
        this.email = email;
        this.telefono = telefono;
        this.materia = materia;
    }

    /**
     * Cantidad maxima de prestamos simultaneos permitidos. Cada subclase
     * lo define segun su perfil (RF04: registrar prestamo valida este limite).
     */
    public abstract int getMaxPrestamos();

    /**
     * Dias habiles de duracion de un prestamo antes de la fecha de
     * devolucion esperada. Cada subclase lo define segun su perfil.
     */
    public abstract int getDiasDevolucion();

    public abstract TipoUsuario getTipoUsuario();
}
