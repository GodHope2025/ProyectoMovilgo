package pe.movilgo.movilgo.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Pone en todas las vistas los datos de la sesión que usa el menú. */
@ControllerAdvice
public class SesionAdvice {

    @ModelAttribute
    public void datosSesion(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean autenticado = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
        model.addAttribute("autenticado", autenticado);
        model.addAttribute("usuarioActual", autenticado ? auth.getName() : null);
        model.addAttribute("esCliente", autenticado && tieneRol(auth, "ROLE_CLIENTE"));
        model.addAttribute("esPersonal", autenticado && (tieneRol(auth, "ROLE_ADMIN") || tieneRol(auth, "ROLE_VENDEDOR")));
    }

    private boolean tieneRol(Authentication auth, String rol) {
        return auth.getAuthorities().stream().anyMatch(a -> rol.equals(a.getAuthority()));
    }
}
