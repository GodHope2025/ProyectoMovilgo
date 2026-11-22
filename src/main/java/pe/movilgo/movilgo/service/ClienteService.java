package pe.movilgo.movilgo.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.movilgo.movilgo.dto.ClienteForm;
import pe.movilgo.movilgo.exception.ReglaNegocioException;
import pe.movilgo.movilgo.model.Cliente;
import pe.movilgo.movilgo.model.Rol;
import pe.movilgo.movilgo.model.TipoDocumento;
import pe.movilgo.movilgo.model.Usuario;
import pe.movilgo.movilgo.repository.ClienteRepository;
import pe.movilgo.movilgo.repository.UsuarioRepository;

/** Registro de clientes con validación de DNI/RUC y licencia (medidas antidelito de la Evidencia 1). */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepo;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder codificador;

    public ClienteService(ClienteRepository clienteRepo, UsuarioRepository usuarioRepo, PasswordEncoder codificador) {
        this.clienteRepo = clienteRepo;
        this.usuarioRepo = usuarioRepo;
        this.codificador = codificador;
    }

    @Transactional
    public Cliente registrar(ClienteForm form) {
        String documento = form.getNumeroDocumento().trim();
        validarDocumento(form.getTipoDocumento(), documento);
        String licencia = form.getLicencia() == null ? "" : form.getLicencia().trim();
        if (form.getTipoDocumento() == TipoDocumento.DNI && licencia.isEmpty()) {
            throw new ReglaNegocioException("Para alquilar como persona necesitas registrar tu licencia de conducir");
        }
        String correo = form.getCorreo().trim().toLowerCase(Locale.ROOT);
        if (usuarioRepo.existsByCorreo(correo)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese correo");
        }
        if (clienteRepo.existsByNumeroDocumento(documento)) {
            throw new ReglaNegocioException("Ya existe un cliente con ese documento");
        }
        Usuario usuario = usuarioRepo.save(new Usuario(correo, codificador.encode(form.getClave()), Rol.CLIENTE));
        return clienteRepo.save(new Cliente(form.getTipoDocumento(), documento, form.getNombre().trim(),
                form.getTelefono().trim(), licencia.isEmpty() ? null : licencia, usuario));
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorCorreo(String correo) {
        return clienteRepo.findByUsuarioCorreo(correo)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró el cliente de la sesión"));
    }

    static void validarDocumento(TipoDocumento tipo, String numero) {
        if (tipo == TipoDocumento.DNI && !numero.matches("\\d{8}")) {
            throw new ReglaNegocioException("El DNI debe tener 8 dígitos");
        }
        if (tipo == TipoDocumento.RUC && !numero.matches("(10|20)\\d{9}")) {
            throw new ReglaNegocioException("El RUC debe tener 11 dígitos y empezar con 10 o 20");
        }
    }
}
