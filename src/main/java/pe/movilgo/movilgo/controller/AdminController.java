package pe.movilgo.movilgo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.movilgo.movilgo.service.AlquilerService;

/** Panel del personal (administrador y vendedor): entregas, devoluciones y alertas. */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AlquilerService alquileres;

    public AdminController(AlquilerService alquileres) {
        this.alquileres = alquileres;
    }

    @GetMapping
    public String panel(Model model) {
        model.addAttribute("porEntregar", alquileres.porEntregar());
        model.addAttribute("enCurso", alquileres.enCurso());
        model.addAttribute("noDevueltos", alquileres.noDevueltos());
        model.addAttribute("enRevision", alquileres.enRevision());
        return "admin/panel";
    }

    @PostMapping("/reservas/{id}/entregar")
    public String entregar(@PathVariable("id") Long idReserva, RedirectAttributes redireccion) {
        alquileres.entregar(idReserva);
        redireccion.addFlashAttribute("mensaje", "Vehículo entregado. El alquiler está en curso.");
        return "redirect:/admin";
    }

    @PostMapping("/alquileres/{id}/devolver")
    public String devolver(@PathVariable("id") Long idAlquiler,
            @RequestParam(name = "observaciones", required = false) String observaciones, RedirectAttributes redireccion) {
        alquileres.devolver(idAlquiler, observaciones);
        redireccion.addFlashAttribute("mensaje", "Devolución registrada. El vehículo pasa a revisión.");
        return "redirect:/admin";
    }

    @PostMapping("/vehiculos/{id}/aprobar")
    public String aprobar(@PathVariable("id") Long idVehiculo, RedirectAttributes redireccion) {
        alquileres.aprobarRevision(idVehiculo);
        redireccion.addFlashAttribute("mensaje", "Revisión aprobada. El vehículo vuelve al catálogo.");
        return "redirect:/admin";
    }
}
