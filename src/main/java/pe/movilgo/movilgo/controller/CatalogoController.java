package pe.movilgo.movilgo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.movilgo.movilgo.service.VehiculoService;

/** Catálogo de vehículos (vista Thymeleaf). */
@Controller
public class CatalogoController {

    private final VehiculoService vehiculos;

    public CatalogoController(VehiculoService vehiculos) {
        this.vehiculos = vehiculos;
    }

    @GetMapping({"/", "/catalogo"})
    public String listar(Model model) {
        model.addAttribute("vehiculos", vehiculos.catalogo());
        return "catalogo/lista";   // templates/catalogo/lista.html
    }
}
