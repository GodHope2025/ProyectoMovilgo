package pe.movilgo.movilgo.service.tarifa;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import pe.movilgo.movilgo.model.Cliente;
import pe.movilgo.movilgo.model.Rol;
import pe.movilgo.movilgo.model.TipoDocumento;
import pe.movilgo.movilgo.model.Usuario;

class TarifaServiceTest {

    private final TarifaService tarifas = new TarifaService(List.of(new TarifaPersona(), new TarifaEmpresa()));
    private final BigDecimal precioDia = new BigDecimal("650.00");

    private Cliente cliente(TipoDocumento tipo) {
        return new Cliente(tipo, "1", "Cliente", "987654321", null, new Usuario("c@movilgo.pe", "hash", Rol.CLIENTE));
    }

    @Test
    void personaPagaPrecioCompletoMenosDeSieteDias() {
        assertThat(tarifas.calcular(cliente(TipoDocumento.DNI), precioDia, 6)).isEqualByComparingTo("3900.00");
    }

    @Test
    void personaCincoPorCientoSemanalYDiezMensual() {
        assertThat(tarifas.calcular(cliente(TipoDocumento.DNI), precioDia, 10)).isEqualByComparingTo("6175.00");
        assertThat(tarifas.calcular(cliente(TipoDocumento.DNI), precioDia, 30)).isEqualByComparingTo("17550.00");
    }

    @Test
    void planTrimestralDescuentaMas() {
        assertThat(tarifas.calcular(cliente(TipoDocumento.DNI), precioDia, 90)).isEqualByComparingTo("49725.00");
        assertThat(tarifas.calcular(cliente(TipoDocumento.RUC), precioDia, 90)).isEqualByComparingTo("43875.00");
    }

    @Test
    void empresaSinDescuentoMenosDeSieteDias() {
        assertThat(tarifas.calcular(cliente(TipoDocumento.RUC), precioDia, 6)).isEqualByComparingTo("3900.00");
    }

    @Test
    void empresaDiezPorCientoDesdeSieteDias() {
        assertThat(tarifas.calcular(cliente(TipoDocumento.RUC), precioDia, 7)).isEqualByComparingTo("4095.00");
    }

    @Test
    void empresaVeintePorCientoDesdeTreintaDias() {
        assertThat(tarifas.calcular(cliente(TipoDocumento.RUC), precioDia, 30)).isEqualByComparingTo("15600.00");
    }
}
