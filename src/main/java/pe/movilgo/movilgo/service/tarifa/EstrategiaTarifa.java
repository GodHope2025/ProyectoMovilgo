package pe.movilgo.movilgo.service.tarifa;

import java.math.BigDecimal;

import pe.movilgo.movilgo.model.Cliente;

/** Patrón Strategy: cada tipo de cliente tiene su forma de calcular el precio. */
public interface EstrategiaTarifa {

    /** Planes de larga duración: semanal, mensual y trimestral (la reserva máxima es de 90 días). */
    long DIAS_SEMANA = 7;
    long DIAS_MES = 30;
    long DIAS_TRIMESTRE = 90;

    boolean aplicaA(Cliente cliente);

    BigDecimal calcular(BigDecimal precioDia, long dias);
}
