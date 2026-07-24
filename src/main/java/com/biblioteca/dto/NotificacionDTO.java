package com.biblioteca.dto;

/**
 * DTO liviano para el dropdown de notificaciones del header.
 * Evita mandar la entidad completa a la vista y ya trae la fecha
 * formateada como "Hoy 14:30" / "Ayer 09:15" / "21 de julio de 2026".
 */
public record NotificacionDTO(Long id, String mensaje, String fecha, boolean leida) {
}