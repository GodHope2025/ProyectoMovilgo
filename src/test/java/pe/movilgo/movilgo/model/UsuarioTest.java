package pe.movilgo.movilgo.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void seBloqueaAlQuintoIntentoFallido() {
        Usuario usuario = new Usuario("ana@movilgo.pe", "hash", Rol.CLIENTE);
        for (int i = 0; i < 4; i++) {
            usuario.registrarIntentoFallido(5);
        }
        assertThat(usuario.isBloqueado()).isFalse();
        usuario.registrarIntentoFallido(5);
        assertThat(usuario.isBloqueado()).isTrue();
    }

    @Test
    void desbloquearReiniciaLosIntentos() {
        Usuario usuario = new Usuario("ana@movilgo.pe", "hash", Rol.CLIENTE);
        for (int i = 0; i < 5; i++) {
            usuario.registrarIntentoFallido(5);
        }
        usuario.desbloquear();
        assertThat(usuario.isBloqueado()).isFalse();
        assertThat(usuario.getIntentosFallidos()).isZero();
    }
}
