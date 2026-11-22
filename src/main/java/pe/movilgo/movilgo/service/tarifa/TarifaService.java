package pe.movilgo.movilgo.service.tarifa;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import pe.movilgo.movilgo.exception.ReglaNegocioException;
import pe.movilgo.movilgo.model.Cliente;

/** Elige la estrategia de tarifa según el cliente. Spring inyecta todas las estrategias. */
@Service
public class TarifaService {

    private final List<EstrategiaTarifa> estrategias;

    public TarifaService(List<EstrategiaTarifa> estrategias) {
        this.estrategias = estrategias;
    }

    public BigDecimal calcular(Cliente cliente, BigDecimal precioDia, long dias) {
        return estrategias.stream()
                .filter(e -> e.aplicaA(cliente))
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException("No hay tarifa para este cliente"))
                .calcular(precioDia, dias);
    }
}
