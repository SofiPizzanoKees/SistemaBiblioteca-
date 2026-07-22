package com.biblioteca.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

/**
 * Calcula los feriados nacionales inamovibles y los trasladables/moviles
 * de Argentina para poder excluirlos de los calculos de dias habiles.
 *
 * IMPORTANTE (suposicion adoptada): el listado cubre los feriados fijos
 * definidos por ley y los moviles calculables (Carnaval y Semana Santa).
 * No contempla feriados "puente" que el Poder Ejecutivo agrega por
 * decreto cada anio, ya que esos no se pueden calcular de antemano.
 */
@Service
public class FeriadoService {

    public boolean esFeriado(LocalDate fecha) {
        return feriadosDelAnio(fecha.getYear()).contains(fecha);
    }

    public boolean esDiaHabil(LocalDate fecha) {
        DayOfWeek dia = fecha.getDayOfWeek();
        return dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY && !esFeriado(fecha);
    }

    public Set<LocalDate> feriadosDelAnio(int anio) {
        Set<LocalDate> feriados = new HashSet<>();

        // --- Feriados fijos ---
        feriados.add(LocalDate.of(anio, Month.JANUARY, 1));    // Año Nuevo
        feriados.add(LocalDate.of(anio, Month.MARCH, 24));     // Dia de la Memoria
        feriados.add(LocalDate.of(anio, Month.APRIL, 2));      // Malvinas
        feriados.add(LocalDate.of(anio, Month.MAY, 1));        // Dia del Trabajador
        feriados.add(LocalDate.of(anio, Month.MAY, 25));       // Revolucion de Mayo
        feriados.add(LocalDate.of(anio, Month.JUNE, 20));      // Paso a la Inmortalidad de Belgrano
        feriados.add(LocalDate.of(anio, Month.JULY, 9));       // Independencia
        feriados.add(LocalDate.of(anio, Month.DECEMBER, 8));   // Inmaculada Concepcion
        feriados.add(LocalDate.of(anio, Month.DECEMBER, 25));  // Navidad

        // --- Feriados con fecha variable segun el anio (trasladables por ley,
        // se aplica la fecha original; el corrimiento a lunes se decide por
        // decreto y queda fuera del alcance de este calculo) ---
        feriados.add(LocalDate.of(anio, Month.AUGUST, 17));    // San Martin
        feriados.add(LocalDate.of(anio, Month.OCTOBER, 12));   // Diversidad Cultural
        feriados.add(LocalDate.of(anio, Month.NOVEMBER, 20));  // Soberania Nacional

        // --- Feriados moviles calculados a partir de Pascua ---
        LocalDate domingoDePascua = calcularDomingoDePascua(anio);
        feriados.add(domingoDePascua.minusDays(2));  // Viernes Santo
        feriados.add(domingoDePascua.minusDays(48)); // Lunes de Carnaval
        feriados.add(domingoDePascua.minusDays(47)); // Martes de Carnaval

        return feriados;
    }

    /**
     * Algoritmo de Meeus/Jones/Butcher para calcular el domingo de Pascua
     * (calendario gregoriano), base para Semana Santa y Carnaval.
     */
    private LocalDate calcularDomingoDePascua(int anio) {
        int a = anio % 19;
        int b = anio / 100;
        int c = anio % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int mes = (h + l - 7 * m + 114) / 31;
        int dia = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(anio, mes, dia);
    }
}
