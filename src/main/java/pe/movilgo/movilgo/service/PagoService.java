package pe.movilgo.movilgo.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.movilgo.movilgo.exception.ReglaNegocioException;
import pe.movilgo.movilgo.model.EstadoReserva;
import pe.movilgo.movilgo.model.MetodoPago;
import pe.movilgo.movilgo.model.Pago;
import pe.movilgo.movilgo.model.Reserva;
import pe.movilgo.movilgo.repository.PagoRepository;

/** Registra el pago y confirma la reserva dentro del plazo de 15 minutos. */
@Service
public class PagoService {

    private final ReservaService reservas;
    private final PagoRepository pagoRepo;
    private final Clock reloj;

    public PagoService(ReservaService reservas, PagoRepository pagoRepo, Clock reloj) {
        this.reservas = reservas;
        this.pagoRepo = pagoRepo;
        this.reloj = reloj;
    }

    @Transactional
    public Pago pagar(String correo, Long idReserva, MetodoPago metodo) {
        if (metodo == null) {
            throw new ReglaNegocioException("Elige un medio de pago");
        }
        Reserva reserva = reservas.buscarDeCliente(correo, idReserva);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE_PAGO || pagoRepo.existsByReserva(reserva)) {
            throw new ReglaNegocioException("Esta reserva no está pendiente de pago");
        }
        LocalDateTime ahora = LocalDateTime.now(reloj);
        if (reserva.estaVencida(ahora)) {
            throw new ReglaNegocioException("Pasaron los 15 minutos para pagar. Vuelve a reservar el vehículo.");
        }
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return pagoRepo.save(new Pago(reserva, reserva.getMontoTotal(), metodo, ahora, nuevoCodigo()));
    }

    static String nuevoCodigo() {
        return "MG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
