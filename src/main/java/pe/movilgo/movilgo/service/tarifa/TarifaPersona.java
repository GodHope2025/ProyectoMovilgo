package pe.movilgo.movilgo.service.tarifa;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import pe.movilgo.movilgo.model.Cliente;

/** Personas con DNI: precio por día sin descuento. */
@Component
public class TarifaPersona implements EstrategiaTarifa {

    @Override
    public boolean aplicaA(Cliente cliente) {
        return !cliente.esEmpresa();
    }

    @Override
    public BigDecimal calcular(BigDecimal precioDia, long dias) {
        return precioDia.multiply(BigDecimal.valueOf(dias)).setScale(2, RoundingMode.HALF_UP);
    }
}
