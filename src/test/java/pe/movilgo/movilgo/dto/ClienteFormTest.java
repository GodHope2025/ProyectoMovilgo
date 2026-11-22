package pe.movilgo.movilgo.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/** Política de contraseñas del registro (HU-11). */
class ClienteFormTest {

    private static final Pattern POLITICA = Pattern.compile(ClienteForm.PATRON_CLAVE);

    @Test
    void aceptaClaveConMayusculaMinusculaNumeroYSimbolo() {
        assertThat(POLITICA.matcher("MovilGo2026*").matches()).isTrue();
        assertThat(POLITICA.matcher("Arequipa#54").matches()).isTrue();
    }

    @Test
    void rechazaClavesDebiles() {
        assertThat(POLITICA.matcher("Ab1*").matches()).as("muy corta").isFalse();
        assertThat(POLITICA.matcher("movilgo2026*").matches()).as("sin mayúscula").isFalse();
        assertThat(POLITICA.matcher("MOVILGO2026*").matches()).as("sin minúscula").isFalse();
        assertThat(POLITICA.matcher("MovilGoPeru*").matches()).as("sin número").isFalse();
        assertThat(POLITICA.matcher("MovilGo2026").matches()).as("sin símbolo").isFalse();
        assertThat(POLITICA.matcher("Movil Go2026*").matches()).as("con espacio").isFalse();
    }

    @Test
    void detectaQueLasClavesNoCoinciden() {
        ClienteForm form = new ClienteForm();
        form.setClave("MovilGo2026*");
        form.setConfirmarClave("MovilGo2026*");
        assertThat(form.isClavesCoinciden()).isTrue();
        form.setConfirmarClave("MovilGo2025*");
        assertThat(form.isClavesCoinciden()).isFalse();
    }
}
