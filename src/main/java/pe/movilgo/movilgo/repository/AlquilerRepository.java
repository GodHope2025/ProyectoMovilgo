package pe.movilgo.movilgo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.movilgo.movilgo.model.Alquiler;
import pe.movilgo.movilgo.model.EstadoAlquiler;

public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {

    List<Alquiler> findByEstadoOrderByFechaLimiteAsc(EstadoAlquiler estado);

    List<Alquiler> findByEstadoAndFechaLimiteBefore(EstadoAlquiler estado, LocalDateTime limite);
}
