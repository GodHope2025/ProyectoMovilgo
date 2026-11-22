package pe.movilgo.movilgo.service.tarifa;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import pe.movilgo.movilgo.model.Cliente;

/** Personas con DNI: 5 % desde 7 días, 10 % desde 30 días y 15 % el plan trimestral (90 días). */
@Component
public class TarifaPersona implements EstrategiaTarifa {

    static final BigDecimal DESCUENTO_SEMANA = new BigDecimal("0.05");
    static final BigDecimal DESCUENTO_MES = new BigDecimal("0.10");
    static final BigDecimal DESCUENTO_TRIMESTRE = new BigDecimal("0.15");

    @Override
    public boolean aplicaA(Cliente cliente) {
        return !cliente.esEmpresa();
    }

    @Override
    public BigDecimal calcular(BigDecimal precioDia, long dias) {
        BigDecimal bruto = precioDia.multiply(BigDecimal.valueOf(dias));
        BigDecimal descuento = BigDecimal.ZERO;
        if (dias >= DIAS_TRIMESTRE) {
            descuento = DESCUENTO_TRIMESTRE;
        } else if (dias >= DIAS_MES) {
            descuento = DESCUENTO_MES;
        } else if (dias >= DIAS_SEMANA) {
            descuento = DESCUENTO_SEMANA;
        }
        return bruto.multiply(BigDecimal.ONE.subtract(descuento)).setScale(2, RoundingMode.HALF_UP);
    }
}
