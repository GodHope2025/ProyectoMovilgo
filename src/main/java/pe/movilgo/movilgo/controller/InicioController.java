package pe.movilgo.movilgo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.movilgo.movilgo.model.Vehiculo;
import pe.movilgo.movilgo.service.VehiculoService;

/** Página de inicio (portada) con los vehículos destacados. */
@Controller
public class InicioController {

    private static final int DESTACADOS = 3;

    private final VehiculoService vehiculos;

    public InicioController(VehiculoService vehiculos) {
        this.vehiculos = vehiculos;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        List<Vehiculo> catalogo = vehiculos.catalogo();
        model.addAttribute("destacados", catalogo.stream().filter(v -> v.getImagen() != null).limit(DESTACADOS).toList());
        model.addAttribute("totalDisponibles", catalogo.size());
        return "inicio/index";
    }
}
