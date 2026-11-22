package pe.movilgo.movilgo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.movilgo.movilgo.config.RelojConfig;
import pe.movilgo.movilgo.dto.ReservaForm;
import pe.movilgo.movilgo.exception.ReglaNegocioException;
import pe.movilgo.movilgo.model.Cliente;
import pe.movilgo.movilgo.model.EstadoReserva;
import pe.movilgo.movilgo.model.EstadoVehiculo;
import pe.movilgo.movilgo.model.Reserva;
import pe.movilgo.movilgo.model.Rol;
import pe.movilgo.movilgo.model.TipoDocumento;
import pe.movilgo.movilgo.model.Usuario;
import pe.movilgo.movilgo.model.Vehiculo;
import pe.movilgo.movilgo.repository.ReservaRepository;
import pe.movilgo.movilgo.repository.VehiculoRepository;
import pe.movilgo.movilgo.service.tarifa.TarifaEmpresa;
import pe.movilgo.movilgo.service.tarifa.TarifaPersona;
import pe.movilgo.movilgo.service.tarifa.TarifaService;

class ReservaServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 23);
    private static final String CORREO = "ana@movilgo.pe";

    private ReservaRepository reservaRepo;
    private VehiculoRepository vehiculoRepo;
    private ClienteService clientes;
    private ReservaService servicio;
    private Vehiculo hilux;

    @BeforeEach
    void preparar() {
        reservaRepo = mock(ReservaRepository.class);
        vehiculoRepo = mock(VehiculoRepository.class);
        clientes = mock(ClienteService.class);
        Clock reloj = Clock.fixed(ZonedDateTime.of(HOY.atTime(10, 0), RelojConfig.ZONA_LIMA).toInstant(), RelojConfig.ZONA_LIMA);
        TarifaService tarifas = new TarifaService(List.of(new TarifaPersona(), new TarifaEmpresa()));
        servicio = new ReservaService(reservaRepo, vehiculoRepo, clientes, tarifas, reloj);
        hilux = new Vehiculo("Toyota", "Hilux 4x4", "Camioneta minera", new BigDecimal("300.00"));
        when(vehiculoRepo.buscarParaReservar(1L)).thenReturn(Optional.of(hilux));
        when(clientes.buscarPorCorreo(CORREO)).thenReturn(cliente(TipoDocumento.DNI, "45871236"));
        when(reservaRepo.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Cliente cliente(TipoDocumento tipo, String documento) {
        return new Cliente(tipo, documento, "Ana Quispe", "987654321", "Q1", new Usuario(CORREO, "hash", Rol.CLIENTE));
    }

    @Test
    void creaReservaPendienteConMontoPorDias() {
        Reserva reserva = servicio.crear(CORREO, new ReservaForm(1L, HOY, HOY.plusDays(2)));
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.PENDIENTE_PAGO);
        assertThat(reserva.getDias()).isEqualTo(3);
        assertThat(reserva.getMontoTotal()).isEqualByComparingTo("900.00");
        assertThat(reserva.getVenceEn()).isEqualTo(LocalDateTime.of(HOY, LocalTime.of(10, 15)));
    }

    @Test
    void empresaRecibeDescuentoPorSemana() {
        when(clientes.buscarPorCorreo(CORREO)).thenReturn(cliente(TipoDocumento.RUC, "20456789123"));
        Reserva reserva = servicio.crear(CORREO, new ReservaForm(1L, HOY, HOY.plusDays(6)));
        assertThat(reserva.getMontoTotal()).isEqualByComparingTo("1890.00");
    }

    @Test
    void rechazaFechaDeInicioPasada() {
        assertThatThrownBy(() -> servicio.crear(CORREO, new ReservaForm(1L, HOY.minusDays(1), HOY)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("pasado");
    }

    @Test
    void rechazaDevolucionAntesDelInicio() {
        assertThatThrownBy(() -> servicio.crear(CORREO, new ReservaForm(1L, HOY.plusDays(3), HOY.plusDays(1))))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void rechazaVehiculoQueNoEstaDisponible() {
        hilux.setEstado(EstadoVehiculo.EN_REVISION);
        assertThatThrownBy(() -> servicio.crear(CORREO, new ReservaForm(1L, HOY, HOY.plusDays(1))))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("disponible");
    }

    @Test
    void rechazaFechasQueSeCruzanConOtraReserva() {
        when(reservaRepo.existeCruce(any(), any(), any(), any())).thenReturn(true);
        assertThatThrownBy(() -> servicio.crear(CORREO, new ReservaForm(1L, HOY, HOY.plusDays(1))))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya está reservado");
    }

    @Test
    void rechazaClienteEnListaNegra() {
        Cliente bloqueado = cliente(TipoDocumento.DNI, "45871236");
        bloqueado.setEnListaNegra(true);
        when(clientes.buscarPorCorreo(CORREO)).thenReturn(bloqueado);
        assertThatThrownBy(() -> servicio.crear(CORREO, new ReservaForm(1L, HOY, HOY.plusDays(1))))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("restricción");
    }

    @Test
    void expiraLasReservasNoPagadas() {
        Reserva vencida = new Reserva(cliente(TipoDocumento.DNI, "45871236"), hilux, HOY, HOY,
                new BigDecimal("300.00"), HOY.atTime(9, 0));
        when(reservaRepo.findByEstadoAndVenceEnBefore(any(), any())).thenReturn(List.of(vencida));
        assertThat(servicio.expirarVencidas()).isEqualTo(1);
        assertThat(vencida.getEstado()).isEqualTo(EstadoReserva.EXPIRADA);
    }
}
