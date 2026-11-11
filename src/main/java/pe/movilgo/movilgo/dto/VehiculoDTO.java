package pe.movilgo.movilgo.dto;

import java.math.BigDecimal;

import pe.movilgo.movilgo.model.Vehiculo;

/** Datos del vehículo que se envían por la API REST. */
public record VehiculoDTO(Long id, String marca, String modelo, String categoria, BigDecimal precioDia, String estado) {

    public static VehiculoDTO desde(Vehiculo v) {
        return new VehiculoDTO(v.getIdVehiculo(), v.getMarca(), v.getModelo(), v.getCategoria(),
                v.getPrecioDia(), v.getEstado().name());
    }
}
