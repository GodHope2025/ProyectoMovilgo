package pe.movilgo.movilgo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.movilgo.movilgo.model.EstadoVehiculo;
import pe.movilgo.movilgo.model.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    List<Vehiculo> findByEstadoOrderByPrecioDiaDesc(EstadoVehiculo estado);
}
