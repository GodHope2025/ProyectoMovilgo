package pe.movilgo.movilgo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.movilgo.movilgo.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByNumeroDocumento(String numeroDocumento);

    Optional<Cliente> findByUsuarioCorreo(String correo);
}
