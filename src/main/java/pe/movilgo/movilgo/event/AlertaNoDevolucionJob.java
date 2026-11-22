package pe.movilgo.movilgo.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pe.movilgo.movilgo.model.Alquiler;
import pe.movilgo.movilgo.service.AlquilerService;

/** Tarea programada: cada 15 minutos busca vehículos no devueltos y lanza la alerta de posible robo. */
@Component
public class AlertaNoDevolucionJob {

    private static final Logger LOG = LoggerFactory.getLogger(AlertaNoDevolucionJob.class);

    private final AlquilerService alquileres;

    public AlertaNoDevolucionJob(AlquilerService alquileres) {
        this.alquileres = alquileres;
    }

    @Scheduled(fixedDelay = 900_000, initialDelay = 60_000)
    public void revisar() {
        List<Alquiler> atrasados = alquileres.marcarNoDevueltos();
        for (Alquiler alquiler : atrasados) {
            LOG.warn("ALERTA posible robo: el vehículo {} {} (reserva {}) no se devolvió en {} horas",
                    alquiler.getReserva().getVehiculo().getMarca(), alquiler.getReserva().getVehiculo().getModelo(),
                    alquiler.getReserva().getIdReserva(), AlquilerService.HORAS_TOLERANCIA);
        }
    }
}
