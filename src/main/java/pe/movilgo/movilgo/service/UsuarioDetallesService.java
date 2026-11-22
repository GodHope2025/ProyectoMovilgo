package pe.movilgo.movilgo.service;

import java.util.Locale;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.movilgo.movilgo.model.Usuario;
import pe.movilgo.movilgo.repository.UsuarioRepository;

/** Spring Security consulta aquí los usuarios al iniciar sesión. */
@Service
public class UsuarioDetallesService implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;

    public UsuarioDetallesService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) {
        String normalizado = correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarioRepo.findByCorreo(normalizado)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return User.withUsername(usuario.getCorreo())
                .password(usuario.getClaveHash())
                .roles(usuario.getRol().name())
                .accountLocked(usuario.isBloqueado())
                .build();
    }
}
