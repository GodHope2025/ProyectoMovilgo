package pe.movilgo.movilgo.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import pe.movilgo.movilgo.dto.ClienteForm;
import pe.movilgo.movilgo.model.Rol;
import pe.movilgo.movilgo.model.TipoDocumento;
import pe.movilgo.movilgo.model.Usuario;
import pe.movilgo.movilgo.model.Vehiculo;
import pe.movilgo.movilgo.repository.UsuarioRepository;
import pe.movilgo.movilgo.repository.VehiculoRepository;
import pe.movilgo.movilgo.service.ClienteService;

/** Carga datos de muestra la primera vez que se ejecuta el proyecto (local o en la nube). */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final VehiculoRepository vehiculos;
    private final UsuarioRepository usuarios;
    private final ClienteService clientes;
    private final PasswordEncoder codificador;
    private final String claveDemo;

    public DatosIniciales(VehiculoRepository vehiculos, UsuarioRepository usuarios, ClienteService clientes,
            PasswordEncoder codificador, @Value("${movilgo.demo.clave:MovilGo2026*}") String claveDemo) {
        this.vehiculos = vehiculos;
        this.usuarios = usuarios;
        this.clientes = clientes;
        this.codificador = codificador;
        this.claveDemo = claveDemo;
    }

    @Override
    public void run(String... args) {
        if (vehiculos.count() == 0) {
            vehiculos.saveAll(List.of(
                    new Vehiculo("Toyota", "Hilux 4x4", "Camioneta minera", new BigDecimal("320.00")),
                    new Vehiculo("Mitsubishi", "L200 4x4", "Camioneta minera", new BigDecimal("300.00")),
                    new Vehiculo("Toyota", "Coaster", "Bus de personal", new BigDecimal("650.00")),
                    new Vehiculo("Hyundai", "H-1", "Minivan", new BigDecimal("260.00")),
                    new Vehiculo("Hyundai", "Accent", "Auto", new BigDecimal("140.00")),
                    new Vehiculo("Xiaomi", "Scooter 4 Pro", "Scooter", new BigDecimal("45.00"))));
        }
        crearPersonal("admin@movilgo.pe", Rol.ADMIN);
        crearPersonal("vendedor@movilgo.pe", Rol.VENDEDOR);
        crearCliente(TipoDocumento.DNI, "45871236", "Lucía Mendoza Rivas", "cliente@movilgo.pe", "Q45871236");
        crearCliente(TipoDocumento.RUC, "20456789123", "Minera Andina del Sur S.A.C.", "minera@movilgo.pe", null);
    }

    private void crearPersonal(String correo, Rol rol) {
        if (!usuarios.existsByCorreo(correo)) {
            usuarios.save(new Usuario(correo, codificador.encode(claveDemo), rol));
        }
    }

    private void crearCliente(TipoDocumento tipo, String documento, String nombre, String correo, String licencia) {
        if (usuarios.existsByCorreo(correo)) {
            return;
        }
        ClienteForm form = new ClienteForm();
        form.setTipoDocumento(tipo);
        form.setNumeroDocumento(documento);
        form.setNombre(nombre);
        form.setTelefono("987654321");
        form.setLicencia(licencia);
        form.setCorreo(correo);
        form.setClave(claveDemo);
        clientes.registrar(form);
    }
}
