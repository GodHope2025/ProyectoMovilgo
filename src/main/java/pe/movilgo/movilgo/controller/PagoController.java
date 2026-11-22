package pe.movilgo.movilgo.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.movilgo.movilgo.exception.ReglaNegocioException;
import pe.movilgo.movilgo.model.MetodoPago;
import pe.movilgo.movilgo.model.Pago;
import pe.movilgo.movilgo.service.PagoService;
import pe.movilgo.movilgo.service.ReservaService;

/** Pantalla de pago de una reserva. */
@Controller
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagos;
    private final ReservaService reservas;

    public PagoController(PagoService pagos, ReservaService reservas) {
        this.pagos = pagos;
        this.reservas = reservas;
    }

    @GetMapping("/{id}")
    public String formulario(@PathVariable("id") Long idReserva, Principal principal, Model model) {
        model.addAttribute("reserva", reservas.buscarDeCliente(principal.getName(), idReserva));
        model.addAttribute("metodos", MetodoPago.values());
        return "pagos/pagar";
    }

    @PostMapping("/{id}")
    public String pagar(@PathVariable("id") Long idReserva, @RequestParam(name = "metodo", required = false) MetodoPago metodo,
            Principal principal, Model model, RedirectAttributes redireccion) {
        try {
            Pago pago = pagos.pagar(principal.getName(), idReserva, metodo);
            redireccion.addFlashAttribute("mensaje", "Pago registrado. Código de operación: " + pago.getCodigoOperacion());
            return "redirect:/reservas";
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("reserva", reservas.buscarDeCliente(principal.getName(), idReserva));
            model.addAttribute("metodos", MetodoPago.values());
            return "pagos/pagar";
        }
    }
}
