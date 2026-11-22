package pe.movilgo.movilgo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.movilgo.movilgo.exception.ReglaNegocioException;
import pe.movilgo.movilgo.model.EstadoVehiculo;
import pe.movilgo.movilgo.model.Vehiculo;
import pe.movilgo.movilgo.repository.VehiculoRepository;

/** Reglas de negocio de los vehículos (patrón Service Layer). */
@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepo;

    public VehiculoService(VehiculoRepository vehiculoRepo) {
        this.vehiculoRepo = vehiculoRepo;
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> catalogo() {
        return vehiculoRepo.findByEstadoOrderByPrecioDiaDesc(EstadoVehiculo.DISPONIBLE);
    }

    @Transactional(readOnly = true)
    public Vehiculo buscar(Long idVehiculo) {
        return vehiculoRepo.findById(idVehiculo)
                .orElseThrow(() -> new ReglaNegocioException("El vehículo no existe"));
    }
}
