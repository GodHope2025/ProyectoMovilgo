package pe.movilgo.movilgo.controller.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.movilgo.movilgo.dto.VehiculoDTO;
import pe.movilgo.movilgo.service.VehiculoService;

/** API REST: el mismo catálogo en JSON (/api/v1/vehiculos). */
@RestController
@RequestMapping("/api/v1/vehiculos")
public class VehiculoApiController {

    private final VehiculoService vehiculos;

    public VehiculoApiController(VehiculoService vehiculos) {
        this.vehiculos = vehiculos;
    }

    @GetMapping
    public List<VehiculoDTO> listar() {
        return vehiculos.catalogo().stream().map(VehiculoDTO::desde).toList();
    }
}
