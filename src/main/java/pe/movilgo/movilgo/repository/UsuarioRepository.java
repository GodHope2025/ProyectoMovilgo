package pe.movilgo.movilgo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.movilgo.movilgo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
