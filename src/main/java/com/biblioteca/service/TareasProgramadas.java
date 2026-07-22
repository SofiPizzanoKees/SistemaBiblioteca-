package com.biblioteca.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Corre una vez por dia (de madrugada) y dispara los chequeos automaticos.
 * Si el dia no es habil (fin de semana o feriado nacional argentino) no
 * hace nada, ya que el instituto esta cerrado y no corresponde generar
 * vencimientos ni sanciones ese dia.
 */
@Component
public class TareasProgramadas {

    private static final Logger log = LoggerFactory.getLogger(TareasProgramadas.class);

    private final PrestamoService prestamoService;
    private final ReservaService reservaService;
    private final FeriadoService feriadoService;

    public TareasProgramadas(PrestamoService prestamoService,
                              ReservaService reservaService,
                              FeriadoService feriadoService) {
        this.prestamoService = prestamoService;
        this.reservaService = reservaService;
        this.feriadoService = feriadoService;
    }

    // Todos los dias a las 06:00 am
    @Scheduled(cron = "0 0 6 * * *")
    public void ejecutarChequeosDiarios() {
        LocalDate hoy = LocalDate.now();
        if (!feriadoService.esDiaHabil(hoy)) {
            log.info("Hoy ({}) no es dia habil, no se ejecutan los chequeos automaticos.", hoy);
            return;
        }

        log.info("Ejecutando chequeos automaticos del dia {}", hoy);
        prestamoService.verificarSancionesPorAtraso();
        reservaService.verificarReservasVencidas();
    }
}
