package pe.movilgo.movilgo.controller;

import java.security.Principal;
import java.time.Clock;
import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import pe.movilgo.movilgo.dto.ReservaForm;
import pe.movilgo.movilgo.exception.ReglaNegocioException;
import pe.movilgo.movilgo.service.ReservaService;
import pe.movilgo.movilgo.service.VehiculoService;

/** Reservas del cliente: crear, listar y cancelar. */
@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private static final String VISTA_NUEVA = "reservas/nueva";

    private final ReservaService reservas;
    private final VehiculoService vehiculos;
    private final Clock reloj;

    public ReservaController(ReservaService reservas, VehiculoService vehiculos, Clock reloj) {
        this.reservas = reservas;
        this.vehiculos = vehiculos;
        this.reloj = reloj;
    }

    @GetMapping
    public String misReservas(Principal principal, Model model) {
        model.addAttribute("reservas", reservas.deCliente(principal.getName()));
        return "reservas/mis-reservas";
    }

    @GetMapping("/nueva")
    public String nueva(@RequestParam("vehiculo") Long idVehiculo, Model model) {
        LocalDate hoy = LocalDate.now(reloj);
        model.addAttribute("vehiculo", vehiculos.buscar(idVehiculo));
        model.addAttribute("reservaForm", new ReservaForm(idVehiculo, hoy, hoy.plusDays(1)));
        model.addAttribute("hoy", hoy);
        return VISTA_NUEVA;
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("reservaForm") ReservaForm form, BindingResult errores,
            Principal principal, Model model, RedirectAttributes redireccion) {
        if (!errores.hasErrors()) {
            try {
                reservas.crear(principal.getName(), form);
                redireccion.addFlashAttribute("mensaje", "Reserva creada. Tienes 15 minutos para confirmarla.");
                return "redirect:/reservas";
            } catch (ReglaNegocioException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        model.addAttribute("vehiculo", vehiculos.buscar(form.getIdVehiculo()));
        model.addAttribute("hoy", LocalDate.now(reloj));
        return VISTA_NUEVA;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable("id") Long idReserva, Principal principal, RedirectAttributes redireccion) {
        reservas.cancelar(principal.getName(), idReserva);
        redireccion.addFlashAttribute("mensaje", "Reserva cancelada.");
        return "redirect:/reservas";
    }
}
