package pe.movilgo.movilgo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/** Rutas públicas y protegidas por rol de MovilGo (Spring Security 7). */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder codificadorClaves() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/catalogo/**", "/registro", "/login", "/privacidad", "/terminos",
                        "/css/**", "/js/**", "/img/**", "/error", "/api/v1/vehiculos/**", "/actuator/health").permitAll()
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers("/reservas/**", "/pagos/**").hasRole("CLIENTE")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/catalogo")
                .failureUrl("/login?error")
                .permitAll())
            .logout(salida -> salida
                .logoutSuccessUrl("/catalogo?salida")
                .permitAll());
        return http.build();
    }
}
