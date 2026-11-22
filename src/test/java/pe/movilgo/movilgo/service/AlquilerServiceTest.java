package pe.movilgo.movilgo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.movilgo.movilgo.config.RelojConfig;
import pe.movilgo.movilgo.exception.ReglaNegocioException;
import pe.movilgo.movilgo.model.Alquiler;
import pe.movilgo.movilgo.model.Cliente;
import pe.movilgo.movilgo.model.EstadoAlquiler;
import pe.movilgo.movilgo.model.EstadoReserva;
import pe.movilgo.movilgo.model.EstadoVehiculo;
import pe.movilgo.movilgo.model.Reserva;
import pe.movilgo.movilgo.model.Rol;
import pe.movilgo.movilgo.model.TipoDocumento;
import pe.movilgo.movilgo.model.Usuario;
import pe.movilgo.movilgo.model.Vehiculo;
import pe.movilgo.movilgo.repository.AlquilerRepository;
import pe.movilgo.movilgo.repository.ReservaRepository;
import pe.movilgo.movilgo.repository.VehiculoRepository;

class AlquilerServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 23);

    private AlquilerRepository alquilerRepo;
    private ReservaRepository reservaRepo;
    private VehiculoRepository vehiculoRepo;
    private AlquilerService servicio;
    private Vehiculo coaster;
    private Reserva reserva;

    @BeforeEach
    void preparar() {
        alquilerRepo = mock(AlquilerRepository.class);
        reservaRepo = mock(ReservaRepository.class);
        vehiculoRepo = mock(VehiculoRepository.class);
        Clock reloj = Clock.fixed(ZonedDateTime.of(HOY.atTime(8, 0), RelojConfig.ZONA_LIMA).toInstant(), RelojConfig.ZONA_LIMA);
        servicio = new AlquilerService(alquilerRepo, reservaRepo, vehiculoRepo, reloj);
        coaster = new Vehiculo("Toyota", "Coaster", "Bus de personal", new BigDecimal("650.00"));
        Cliente minera = new Cliente(TipoDocumento.RUC, "20456789123", "Minera Andina", "987654321", null,
                new Usuario("minera@movilgo.pe", "hash", Rol.CLIENTE));
        reserva = new Reserva(minera, coaster, HOY, HOY.plusDays(2), new BigDecimal("1950.00"), HOY.minusDays(3).atTime(9, 0));
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        when(reservaRepo.findById(5L)).thenReturn(Optional.of(reserva));
        when(alquilerRepo.save(any(Alquiler.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void entregarPoneElVehiculoComoAlquilado() {
        Alquiler alquiler = servicio.entregar(5L);
        assertThat(coaster.getEstado()).isEqualTo(EstadoVehiculo.ALQUILADO);
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.EN_CURSO);
        assertThat(alquiler.getFechaLimite()).isEqualTo(HOY.plusDays(2).atTime(23, 59));
    }

    @Test
    void noSeEntregaUnaReservaSinPagar() {
        reserva.setEstado(EstadoReserva.PENDIENTE_PAGO);
        assertThatThrownBy(() -> servicio.entregar(5L)).isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void devolverPasaElVehiculoARevision() {
        Alquiler alquiler = servicio.entregar(5L);
        when(alquilerRepo.findById(9L)).thenReturn(Optional.of(alquiler));
        servicio.devolver(9L, "Sin daños, tanque lleno");
        assertThat(alquiler.getEstado()).isEqualTo(EstadoAlquiler.DEVUELTO);
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.FINALIZADA);
        assertThat(coaster.getEstado()).isEqualTo(EstadoVehiculo.EN_REVISION);
    }

    @Test
    void aprobarRevisionDevuelveElVehiculoAlCatalogo() {
        coaster.setEstado(EstadoVehiculo.EN_REVISION);
        when(vehiculoRepo.findById(3L)).thenReturn(Optional.of(coaster));
        servicio.aprobarRevision(3L);
        assertThat(coaster.getEstado()).isEqualTo(EstadoVehiculo.DISPONIBLE);
    }

    @Test
    void marcaNoDevueltoTras24HorasDeAtraso() {
        Alquiler atrasado = new Alquiler(reserva, HOY.minusDays(5).atTime(8, 0), HOY.minusDays(2).atTime(23, 59));
        coaster.setEstado(EstadoVehiculo.ALQUILADO);
        LocalDateTime limiteEsperado = HOY.atTime(8, 0).minusHours(24);
        when(alquilerRepo.findByEstadoAndFechaLimiteBefore(eq(EstadoAlquiler.EN_CURSO), eq(limiteEsperado)))
                .thenReturn(List.of(atrasado));
        List<Alquiler> marcados = servicio.marcarNoDevueltos();
        assertThat(marcados).containsExactly(atrasado);
        assertThat(atrasado.getEstado()).isEqualTo(EstadoAlquiler.NO_DEVUELTO);
        assertThat(coaster.getEstado()).isEqualTo(EstadoVehiculo.NO_DEVUELTO);
    }
}
