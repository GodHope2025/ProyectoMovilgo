package pe.movilgo.movilgo.exception;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Manejo centralizado de errores para las vistas Thymeleaf. */
@ControllerAdvice
public class ManejadorGlobal {

    /** Bloqueo optimista (@Version): dos usuarios modificaron lo mismo a la vez (RN-02). */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public String concurrencia(Model model) {
        model.addAttribute("titulo", "Otro usuario se adelantó");
        model.addAttribute("detalle", "Alguien reservó este vehículo al mismo tiempo que tú. "
                + "Vuelve al catálogo e intenta con otras fechas u otro vehículo.");
        return "error/aviso";
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public String reglaNegocio(ReglaNegocioException ex, Model model) {
        model.addAttribute("titulo", "No se pudo completar la operación");
        model.addAttribute("detalle", ex.getMessage());
        return "error/aviso";
    }
}
