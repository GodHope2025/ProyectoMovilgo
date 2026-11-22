package pe.movilgo.movilgo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import pe.movilgo.movilgo.model.EstadoVehiculo;
import pe.movilgo.movilgo.model.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    List<Vehiculo> findByEstadoOrderByPrecioDiaDesc(EstadoVehiculo estado);

    /**
     * Lee el vehículo e incrementa su @Version al confirmar la transacción.
     * Si dos clientes reservan el mismo vehículo a la vez, el segundo recibe un error de concurrencia (RN-02).
     */
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("select v from Vehiculo v where v.idVehiculo = :id")
    Optional<Vehiculo> buscarParaReservar(@Param("id") Long id);
}
