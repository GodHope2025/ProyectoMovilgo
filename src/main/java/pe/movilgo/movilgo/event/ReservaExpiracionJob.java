package pe.movilgo.movilgo.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pe.movilgo.movilgo.service.ReservaService;

/** Tarea programada: cada minuto libera las reservas que no se pagaron a tiempo. */
@Component
public class ReservaExpiracionJob {

    private static final Logger LOG = LoggerFactory.getLogger(ReservaExpiracionJob.class);

    private final ReservaService reservas;

    public ReservaExpiracionJob(ReservaService reservas) {
        this.reservas = reservas;
    }

    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void expirar() {
        int total = reservas.expirarVencidas();
        if (total > 0) {
            LOG.info("Se liberaron {} reservas no pagadas en {} minutos", total, 15);
        }
    }
}
