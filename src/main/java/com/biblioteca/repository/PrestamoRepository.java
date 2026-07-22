package com.biblioteca.repository;

import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;
import com.biblioteca.model.enums.EstadoPrestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByUsuarioAndEstado(Usuario usuario, EstadoPrestamo estado);

    long countByUsuarioAndEstado(Usuario usuario, EstadoPrestamo estado);

    // RF08 / RF06: prestamos activos y vencidos
    List<Prestamo> findByEstado(EstadoPrestamo estado);

    @Query("""
            SELECT p FROM Prestamo p
            WHERE p.estado = com.biblioteca.model.enums.EstadoPrestamo.ACTIVO
            AND p.fechaDevolucionEsperada < CURRENT_DATE
            """)
    List<Prestamo> buscarVencidos();

    // RF10: libro mas prestado -> agrupa por libro y cuenta prestamos
    @Query("""
            SELECT p.ejemplar.libro.titulo, COUNT(p)
            FROM Prestamo p
            GROUP BY p.ejemplar.libro.titulo
            ORDER BY COUNT(p) DESC
            """)
    List<Object[]> librosMasPrestados();

    // RF10: usuarios con mas prestamos
    @Query("""
            SELECT p.usuario, COUNT(p)
            FROM Prestamo p
            GROUP BY p.usuario
            ORDER BY COUNT(p) DESC
            """)
    List<Object[]> usuariosConMasPrestamos();
}
