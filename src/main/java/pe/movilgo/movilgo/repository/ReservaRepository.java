package pe.movilgo.movilgo.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.movilgo.movilgo.model.Cliente;
import pe.movilgo.movilgo.model.EstadoReserva;
import pe.movilgo.movilgo.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /** RN-02: ¿hay otra reserva activa del mismo vehículo que se cruce con esas fechas? */
    @Query("""
            select case when count(r) > 0 then true else false end
            from Reserva r
            where r.vehiculo.idVehiculo = :idVehiculo
              and r.estado in :estados
              and r.fechaInicio <= :fin
              and r.fechaFin >= :inicio
            """)
    boolean existeCruce(@Param("idVehiculo") Long idVehiculo, @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin, @Param("estados") Collection<EstadoReserva> estados);

    List<Reserva> findByClienteOrderByCreadaEnDesc(Cliente cliente);

    List<Reserva> findByEstadoAndVenceEnBefore(EstadoReserva estado, LocalDateTime limite);

    List<Reserva> findByEstadoOrderByFechaInicioAsc(EstadoReserva estado);
}
