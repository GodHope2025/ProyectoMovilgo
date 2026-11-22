package pe.movilgo.movilgo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.movilgo.movilgo.model.Pago;
import pe.movilgo.movilgo.model.Reserva;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    boolean existsByReserva(Reserva reserva);
}
