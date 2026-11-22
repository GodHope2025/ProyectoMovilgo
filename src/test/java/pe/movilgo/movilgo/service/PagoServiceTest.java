package pe.movilgo.movilgo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.movilgo.movilgo.config.RelojConfig;
import pe.movilgo.movilgo.exception.ReglaNegocioException;
import pe.movilgo.movilgo.model.Cliente;
import pe.movilgo.movilgo.model.EstadoReserva;
import pe.movilgo.movilgo.model.MetodoPago;
import pe.movilgo.movilgo.model.Pago;
import pe.movilgo.movilgo.model.Reserva;
import pe.movilgo.movilgo.model.Rol;
import pe.movilgo.movilgo.model.TipoDocumento;
import pe.movilgo.movilgo.model.Usuario;
import pe.movilgo.movilgo.model.Vehiculo;
import pe.movilgo.movilgo.repository.PagoRepository;

class PagoServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 23);
    private static final String CORREO = "ana@movilgo.pe";

    private ReservaService reservas;
    private PagoRepository pagoRepo;
    private Reserva reserva;

    @BeforeEach
    void preparar() {
        reservas = mock(ReservaService.class);
        pagoRepo = mock(PagoRepository.class);
        Cliente cliente = new Cliente(TipoDocumento.DNI, "45871236", "Ana", "987654321", "Q1",
                new Usuario(CORREO, "hash", Rol.CLIENTE));
        Vehiculo hilux = new Vehiculo("Toyota", "Hilux 4x4", "Camioneta minera", new BigDecimal("300.00"));
        reserva = new Reserva(cliente, hilux, HOY, HOY.plusDays(1), new BigDecimal("600.00"), HOY.atTime(10, 0));
        when(reservas.buscarDeCliente(CORREO, 7L)).thenReturn(reserva);
        when(pagoRepo.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private PagoService servicioALas(int hora, int minuto) {
        Clock reloj = Clock.fixed(ZonedDateTime.of(HOY.atTime(hora, minuto), RelojConfig.ZONA_LIMA).toInstant(),
                RelojConfig.ZONA_LIMA);
        return new PagoService(reservas, pagoRepo, reloj);
    }

    @Test
    void pagoDentroDelPlazoConfirmaLaReserva() {
        Pago pago = servicioALas(10, 10).pagar(CORREO, 7L, MetodoPago.YAPE);
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
        assertThat(pago.getMonto()).isEqualByComparingTo("600.00");
        assertThat(pago.getCodigoOperacion()).startsWith("MG-").hasSize(11);
    }

    @Test
    void pagoDespuesDeQuinceMinutosEsRechazado() {
        assertThatThrownBy(() -> servicioALas(10, 16).pagar(CORREO, 7L, MetodoPago.TARJETA))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("15 minutos");
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.PENDIENTE_PAGO);
    }

    @Test
    void noSePagaDosVeces() {
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        assertThatThrownBy(() -> servicioALas(10, 5).pagar(CORREO, 7L, MetodoPago.PLIN))
                .isInstanceOf(ReglaNegocioException.class);
    }
}
