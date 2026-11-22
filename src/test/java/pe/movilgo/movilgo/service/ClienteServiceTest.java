package pe.movilgo.movilgo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import pe.movilgo.movilgo.dto.ClienteForm;
import pe.movilgo.movilgo.exception.ReglaNegocioException;
import pe.movilgo.movilgo.model.Cliente;
import pe.movilgo.movilgo.model.Rol;
import pe.movilgo.movilgo.model.TipoDocumento;
import pe.movilgo.movilgo.model.Usuario;
import pe.movilgo.movilgo.repository.ClienteRepository;
import pe.movilgo.movilgo.repository.UsuarioRepository;

class ClienteServiceTest {

    private ClienteRepository clienteRepo;
    private UsuarioRepository usuarioRepo;
    private ClienteService servicio;

    @BeforeEach
    void preparar() {
        clienteRepo = mock(ClienteRepository.class);
        usuarioRepo = mock(UsuarioRepository.class);
        PasswordEncoder codificador = mock(PasswordEncoder.class);
        when(codificador.encode(any())).thenReturn("$2a$10$hash");
        when(usuarioRepo.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(clienteRepo.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));
        servicio = new ClienteService(clienteRepo, usuarioRepo, codificador);
    }

    private ClienteForm formulario(TipoDocumento tipo, String documento, String licencia) {
        ClienteForm form = new ClienteForm();
        form.setTipoDocumento(tipo);
        form.setNumeroDocumento(documento);
        form.setNombre("Ana Quispe");
        form.setTelefono("987654321");
        form.setLicencia(licencia);
        form.setCorreo("Ana@Correo.pe");
        form.setClave("ClaveSegura1");
        return form;
    }

    @Test
    void registraPersonaConClaveCifrada() {
        Cliente cliente = servicio.registrar(formulario(TipoDocumento.DNI, "45871236", "Q45871236"));
        assertThat(cliente.getUsuario().getCorreo()).isEqualTo("ana@correo.pe");
        assertThat(cliente.getUsuario().getClaveHash()).isEqualTo("$2a$10$hash");
        assertThat(cliente.getUsuario().getRol()).isEqualTo(Rol.CLIENTE);
        assertThat(cliente.esEmpresa()).isFalse();
    }

    @Test
    void personaSinLicenciaEsRechazada() {
        assertThatThrownBy(() -> servicio.registrar(formulario(TipoDocumento.DNI, "45871236", " ")))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("licencia");
    }

    @Test
    void rucDebeEmpezarCon10o20() {
        assertThatThrownBy(() -> servicio.registrar(formulario(TipoDocumento.RUC, "30456789123", null)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("RUC");
    }

    @Test
    void empresaConRucValidoNoNecesitaLicencia() {
        Cliente cliente = servicio.registrar(formulario(TipoDocumento.RUC, "20456789123", null));
        assertThat(cliente.esEmpresa()).isTrue();
    }

    @Test
    void correoRepetidoEsRechazado() {
        when(usuarioRepo.existsByCorreo("ana@correo.pe")).thenReturn(true);
        assertThatThrownBy(() -> servicio.registrar(formulario(TipoDocumento.DNI, "45871236", "Q45871236")))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("correo");
    }
}
