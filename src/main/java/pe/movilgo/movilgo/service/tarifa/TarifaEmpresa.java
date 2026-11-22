package pe.movilgo.movilgo.service.tarifa;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import pe.movilgo.movilgo.model.Cliente;

/** Empresas con RUC (mineras y otras): 10 % desde 7 días y 20 % desde 30 días. */
@Component
public class TarifaEmpresa implements EstrategiaTarifa {

    static final BigDecimal DESCUENTO_SEMANA = new BigDecimal("0.10");
    static final BigDecimal DESCUENTO_MES = new BigDecimal("0.20");

    @Override
    public boolean aplicaA(Cliente cliente) {
        return cliente.esEmpresa();
    }

    @Override
    public BigDecimal calcular(BigDecimal precioDia, long dias) {
        BigDecimal bruto = precioDia.multiply(BigDecimal.valueOf(dias));
        BigDecimal descuento = BigDecimal.ZERO;
        if (dias >= 30) {
            descuento = DESCUENTO_MES;
        } else if (dias >= 7) {
            descuento = DESCUENTO_SEMANA;
        }
        return bruto.multiply(BigDecimal.ONE.subtract(descuento)).setScale(2, RoundingMode.HALF_UP);
    }
}
