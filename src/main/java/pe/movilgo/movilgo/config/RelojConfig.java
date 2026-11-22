package pe.movilgo.movilgo.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Reloj único de la aplicación (hora de Lima). Permite controlar el tiempo en las pruebas. */
@Configuration
public class RelojConfig {

    public static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");

    @Bean
    public Clock reloj() {
        return Clock.system(ZONA_LIMA);
    }
}
