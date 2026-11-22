package pe.movilgo.movilgo.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.movilgo.movilgo.dto.ReservaForm;
import pe.movilgo.movilgo.exception.ReglaNegocioException;
import pe.movilgo.movilgo.model.Cliente;
import pe.movilgo.movilgo.model.EstadoReserva;
import pe.movilgo.movilgo.model.EstadoVehiculo;
import pe.movilgo.movilgo.model.Reserva;
import pe.movilgo.movilgo.model.Vehiculo;
import pe.movilgo.movilgo.repository.ReservaRepository;
import pe.movilgo.movilgo.repository.VehiculoRepository;

/** Reglas de las reservas: fechas válidas, sin cruces y con bloqueo optimista (RN-02 y RN-03). */
@Service
public class ReservaService {

    public static final int MAXIMO_DIAS = 90;
    static final Set<EstadoReserva> ACTIVAS = EnumSet.of(EstadoReserva.PENDIENTE_PAGO,
            EstadoReserva.CONFIRMADA, EstadoReserva.EN_CURSO);

    private final ReservaRepository reservaRepo;
    private final VehiculoRepository vehiculoRepo;
    private final ClienteService clientes;
    private final Clock reloj;

    public ReservaService(ReservaRepository reservaRepo, VehiculoRepository vehiculoRepo, ClienteService clientes,
            Clock reloj) {
        this.reservaRepo = reservaRepo;
        this.vehiculoRepo = vehiculoRepo;
        this.clientes = clientes;
        this.reloj = reloj;
    }

    @Transactional
    public Reserva crear(String correo, ReservaForm form) {
        Cliente cliente = clientes.buscarPorCorreo(correo);
        if (cliente.isEnListaNegra()) {
            throw new ReglaNegocioException("Tu cuenta tiene una restricción. Comunícate con MovilGo.");
        }
        LocalDate inicio = form.getFechaInicio();
        LocalDate fin = form.getFechaFin();
        validarFechas(inicio, fin);
        Vehiculo vehiculo = vehiculoRepo.buscarParaReservar(form.getIdVehiculo())
                .orElseThrow(() -> new ReglaNegocioException("El vehículo no existe"));
        if (vehiculo.getEstado() != EstadoVehiculo.DISPONIBLE) {
            throw new ReglaNegocioException("El vehículo no está disponible para reservas");
        }
        if (reservaRepo.existeCruce(vehiculo.getIdVehiculo(), inicio, fin, ACTIVAS)) {
            throw new ReglaNegocioException("El vehículo ya está reservado en esas fechas. Elige otras fechas u otro vehículo.");
        }
        long dias = Reserva.contarDias(inicio, fin);
        BigDecimal monto = vehiculo.getPrecioDia().multiply(BigDecimal.valueOf(dias));
        return reservaRepo.save(new Reserva(cliente, vehiculo, inicio, fin, monto, LocalDateTime.now(reloj)));
    }

    @Transactional(readOnly = true)
    public List<Reserva> deCliente(String correo) {
        return reservaRepo.findByClienteOrderByCreadaEnDesc(clientes.buscarPorCorreo(correo));
    }

    @Transactional(readOnly = true)
    public Reserva buscarDeCliente(String correo, Long idReserva) {
        Reserva reserva = reservaRepo.findById(idReserva)
                .orElseThrow(() -> new ReglaNegocioException("La reserva no existe"));
        if (!reserva.getCliente().getUsuario().getCorreo().equals(correo)) {
            throw new ReglaNegocioException("La reserva no existe");
        }
        return reserva;
    }

    @Transactional
    public void cancelar(String correo, Long idReserva) {
        Reserva reserva = buscarDeCliente(correo, idReserva);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE_PAGO && reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ReglaNegocioException("Solo se pueden cancelar reservas pendientes o confirmadas");
        }
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepo.save(reserva);
    }

    /** RN-03: las reservas no pagadas en 15 minutos se liberan. */
    @Transactional
    public int expirarVencidas() {
        List<Reserva> vencidas = reservaRepo.findByEstadoAndVenceEnBefore(EstadoReserva.PENDIENTE_PAGO,
                LocalDateTime.now(reloj));
        vencidas.forEach(r -> r.setEstado(EstadoReserva.EXPIRADA));
        reservaRepo.saveAll(vencidas);
        return vencidas.size();
    }

    void validarFechas(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            throw new ReglaNegocioException("Elige las fechas de inicio y devolución");
        }
        if (inicio.isBefore(LocalDate.now(reloj))) {
            throw new ReglaNegocioException("La fecha de inicio no puede estar en el pasado");
        }
        if (fin.isBefore(inicio)) {
            throw new ReglaNegocioException("La devolución debe ser el mismo día del inicio o después");
        }
        if (Reserva.contarDias(inicio, fin) > MAXIMO_DIAS) {
            throw new ReglaNegocioException("Una reserva puede durar como máximo " + MAXIMO_DIAS + " días");
        }
    }
}
