package com.biblioteca.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formatea fechas/horas para mostrar en la vista:
 *  - Si es hoy              -> "Hoy HH:mm"
 *  - Si fue ayer            -> "Ayer HH:mm"
 *  - En cualquier otro caso -> "d 'de' MMMM 'de' yyyy"  (ej: 21 de julio de 2026)
 */
public class FechaUtil {

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private static final DateTimeFormatter FORMATO_FECHA_LARGA =
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "AR"));

    private FechaUtil() {
    }

    public static String formatearNotificacion(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            return "-";
        }

        LocalDate fecha = fechaHora.toLocalDate();
        LocalDate hoy = LocalDate.now();

        if (fecha.isEqual(hoy)) {
            return "Hoy " + fechaHora.format(FORMATO_HORA);
        }
        if (fecha.isEqual(hoy.minusDays(1))) {
            return "Ayer " + fechaHora.format(FORMATO_HORA);
        }
        return fechaHora.format(FORMATO_FECHA_LARGA);
    }
}