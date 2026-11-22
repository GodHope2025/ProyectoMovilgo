package pe.movilgo.movilgo.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.movilgo.movilgo.exception.ReglaNegocioException;
import pe.movilgo.movilgo.model.Alquiler;
import pe.movilgo.movilgo.model.EstadoAlquiler;
import pe.movilgo.movilgo.model.EstadoReserva;
import pe.movilgo.movilgo.model.EstadoVehiculo;
import pe.movilgo.movilgo.model.Reserva;
import pe.movilgo.movilgo.model.Vehiculo;
import pe.movilgo.movilgo.repository.AlquilerRepository;
import pe.movilgo.movilgo.repository.ReservaRepository;
import pe.movilgo.movilgo.repository.VehiculoRepository;

/** Ciclo DISPONIBLE → ALQUILADO → EN_REVISION → DISPONIBLE y alerta de no devolución a las 24 h (RN-05). */
@Service
public class AlquilerService {

    public static final int HORAS_TOLERANCIA = 24;
    static final LocalTime HORA_LIMITE = LocalTime.of(23, 59);

    private final AlquilerRepository alquilerRepo;
    private final ReservaRepository reservaRepo;
    private final VehiculoRepository vehiculoRepo;
    private final Clock reloj;

    public AlquilerService(AlquilerRepository alquilerRepo, ReservaRepository reservaRepo,
            VehiculoRepository vehiculoRepo, Clock reloj) {
        this.alquilerRepo = alquilerRepo;
        this.reservaRepo = reservaRepo;
        this.vehiculoRepo = vehiculoRepo;
        this.reloj = reloj;
    }

    @Transactional
    public Alquiler entregar(Long idReserva) {
        Reserva reserva = reservaRepo.findById(idReserva)
                .orElseThrow(() -> new ReglaNegocioException("La reserva no existe"));
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ReglaNegocioException("Solo se entregan vehículos de reservas pagadas (CONFIRMADA)");
        }
        if (LocalDate.now(reloj).isBefore(reserva.getFechaInicio())) {
            throw new ReglaNegocioException("La entrega es a partir del " + reserva.getFechaInicio());
        }
        Vehiculo vehiculo = reserva.getVehiculo();
        if (vehiculo.getEstado() != EstadoVehiculo.DISPONIBLE) {
            throw new ReglaNegocioException("El vehículo no está disponible para entregarse");
        }
        reserva.setEstado(EstadoReserva.EN_CURSO);
        vehiculo.setEstado(EstadoVehiculo.ALQUILADO);
        vehiculoRepo.save(vehiculo);
        reservaRepo.save(reserva);
        LocalDateTime limite = reserva.getFechaFin().atTime(HORA_LIMITE);
        return alquilerRepo.save(new Alquiler(reserva, LocalDateTime.now(reloj), limite));
    }

    @Transactional
    public Alquiler devolver(Long idAlquiler, String observaciones) {
        Alquiler alquiler = alquilerRepo.findById(idAlquiler)
                .orElseThrow(() -> new ReglaNegocioException("El alquiler no existe"));
        if (alquiler.getEstado() == EstadoAlquiler.DEVUELTO) {
            throw new ReglaNegocioException("Este vehículo ya fue devuelto");
        }
        alquiler.registrarDevolucion(LocalDateTime.now(reloj), observaciones);
        Reserva reserva = alquiler.getReserva();
        reserva.setEstado(EstadoReserva.FINALIZADA);
        reserva.getVehiculo().setEstado(EstadoVehiculo.EN_REVISION);
        vehiculoRepo.save(reserva.getVehiculo());
        reservaRepo.save(reserva);
        return alquilerRepo.save(alquiler);
    }

    /** Después del checklist de devolución, el vehículo vuelve al catálogo. */
    @Transactional
    public void aprobarRevision(Long idVehiculo) {
        Vehiculo vehiculo = vehiculoRepo.findById(idVehiculo)
                .orElseThrow(() -> new ReglaNegocioException("El vehículo no existe"));
        if (vehiculo.getEstado() != EstadoVehiculo.EN_REVISION) {
            throw new ReglaNegocioException("El vehículo no está en revisión");
        }
        vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
        vehiculoRepo.save(vehiculo);
    }

    /** RN-05: si pasan 24 horas de la fecha límite sin devolución, se marca NO_DEVUELTO (posible robo). */
    @Transactional
    public List<Alquiler> marcarNoDevueltos() {
        LocalDateTime limite = LocalDateTime.now(reloj).minusHours(HORAS_TOLERANCIA);
        List<Alquiler> atrasados = alquilerRepo.findByEstadoAndFechaLimiteBefore(EstadoAlquiler.EN_CURSO, limite);
        for (Alquiler alquiler : atrasados) {
            alquiler.setEstado(EstadoAlquiler.NO_DEVUELTO);
            alquiler.getReserva().getVehiculo().setEstado(EstadoVehiculo.NO_DEVUELTO);
            vehiculoRepo.save(alquiler.getReserva().getVehiculo());
        }
        alquilerRepo.saveAll(atrasados);
        return atrasados;
    }

    @Transactional(readOnly = true)
    public List<Reserva> porEntregar() {
        return reservaRepo.findByEstadoOrderByFechaInicioAsc(EstadoReserva.CONFIRMADA);
    }

    @Transactional(readOnly = true)
    public List<Alquiler> enCurso() {
        return alquilerRepo.findByEstadoOrderByFechaLimiteAsc(EstadoAlquiler.EN_CURSO);
    }

    @Transactional(readOnly = true)
    public List<Alquiler> noDevueltos() {
        return alquilerRepo.findByEstadoOrderByFechaLimiteAsc(EstadoAlquiler.NO_DEVUELTO);
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> enRevision() {
        return vehiculoRepo.findByEstado(EstadoVehiculo.EN_REVISION);
    }
}
