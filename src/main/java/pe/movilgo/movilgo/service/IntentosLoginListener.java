package pe.movilgo.movilgo.service;

import java.util.Locale;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pe.movilgo.movilgo.repository.UsuarioRepository;

/** Bloquea la cuenta tras 5 intentos fallidos seguidos (patrón Observer con eventos de Spring). */
@Component
public class IntentosLoginListener {

    public static final int MAXIMO_INTENTOS = 5;

    private final UsuarioRepository usuarioRepo;

    public IntentosLoginListener(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @EventListener
    @Transactional
    public void alFallar(AuthenticationFailureBadCredentialsEvent evento) {
        String correo = String.valueOf(evento.getAuthentication().getPrincipal()).trim().toLowerCase(Locale.ROOT);
        usuarioRepo.findByCorreo(correo).ifPresent(usuario -> {
            usuario.registrarIntentoFallido(MAXIMO_INTENTOS);
            usuarioRepo.save(usuario);
        });
    }

    @EventListener
    @Transactional
    public void alIngresar(AuthenticationSuccessEvent evento) {
        usuarioRepo.findByCorreo(evento.getAuthentication().getName()).ifPresent(usuario -> {
            if (usuario.getIntentosFallidos() > 0) {
                usuario.reiniciarIntentos();
                usuarioRepo.save(usuario);
            }
        });
    }
}
