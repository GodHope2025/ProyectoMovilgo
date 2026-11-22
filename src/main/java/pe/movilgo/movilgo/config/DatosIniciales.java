package pe.movilgo.movilgo.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import pe.movilgo.movilgo.model.Vehiculo;
import pe.movilgo.movilgo.repository.VehiculoRepository;

/** Carga vehículos de muestra la primera vez que se ejecuta el proyecto. */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final VehiculoRepository vehiculos;

    public DatosIniciales(VehiculoRepository vehiculos) {
        this.vehiculos = vehiculos;
    }

    @Override
    public void run(String... args) {
        if (vehiculos.count() == 0) {
            vehiculos.saveAll(List.of(
                    new Vehiculo("Toyota", "Hilux 4x4", "Camioneta minera", new BigDecimal("320.00")),
                    new Vehiculo("Mitsubishi", "L200 4x4", "Camioneta minera", new BigDecimal("300.00")),
                    new Vehiculo("Mercedes-Benz", "Sprinter", "Bus de personal", new BigDecimal("650.00")),
                    new Vehiculo("Hyundai", "Accent", "Auto", new BigDecimal("140.00")),
                    new Vehiculo("Kia", "Sportage", "SUV", new BigDecimal("210.00")),
                    new Vehiculo("Xiaomi", "Scooter 4 Pro", "Scooter", new BigDecimal("45.00"))));
        }
    }
}
