package pe.movilgo.movilgo.service.tarifa;

import java.math.BigDecimal;

import pe.movilgo.movilgo.model.Cliente;

/** Patrón Strategy: cada tipo de cliente tiene su forma de calcular el precio. */
public interface EstrategiaTarifa {

    boolean aplicaA(Cliente cliente);

    BigDecimal calcular(BigDecimal precioDia, long dias);
}
