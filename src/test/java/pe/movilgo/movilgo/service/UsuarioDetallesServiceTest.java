package pe.movilgo.movilgo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import pe.movilgo.movilgo.model.Rol;
import pe.movilgo.movilgo.model.Usuario;
import pe.movilgo.movilgo.repository.UsuarioRepository;

class UsuarioDetallesServiceTest {

    private UsuarioRepository usuarioRepo;
    private UsuarioDetallesService servicio;

    @BeforeEach
    void preparar() {
        usuarioRepo = mock(UsuarioRepository.class);
        servicio = new UsuarioDetallesService(usuarioRepo);
    }

    @Test
    void cargaUsuarioConSuRol() {
        when(usuarioRepo.findByCorreo("admin@movilgo.pe"))
                .thenReturn(Optional.of(new Usuario("admin@movilgo.pe", "hash", Rol.ADMIN)));
        UserDetails detalles = servicio.loadUserByUsername(" Admin@MovilGo.pe ");
        assertThat(detalles.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
        assertThat(detalles.isAccountNonLocked()).isTrue();
    }

    @Test
    void cuentaBloqueadaNoPuedeIngresar() {
        Usuario usuario = new Usuario("ana@movilgo.pe", "hash", Rol.CLIENTE);
        for (int i = 0; i < IntentosLoginListener.MAXIMO_INTENTOS; i++) {
            usuario.registrarIntentoFallido(IntentosLoginListener.MAXIMO_INTENTOS);
        }
        when(usuarioRepo.findByCorreo("ana@movilgo.pe")).thenReturn(Optional.of(usuario));
        assertThat(servicio.loadUserByUsername("ana@movilgo.pe").isAccountNonLocked()).isFalse();
    }

    @Test
    void usuarioInexistenteLanzaExcepcion() {
        when(usuarioRepo.findByCorreo("nadie@movilgo.pe")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> servicio.loadUserByUsername("nadie@movilgo.pe"));
    }
}
