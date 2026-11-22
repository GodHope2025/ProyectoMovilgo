package pe.movilgo.movilgo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import pe.movilgo.movilgo.model.TipoDocumento;

/** Datos del formulario de registro de clientes, validados con Bean Validation. */
public class ClienteForm {

    @NotNull(message = "Elige el tipo de documento")
    private TipoDocumento tipoDocumento = TipoDocumento.DNI;

    @NotBlank(message = "Ingresa tu número de documento")
    @Pattern(regexp = "\\d{8}|\\d{11}", message = "El DNI tiene 8 dígitos y el RUC 11")
    private String numeroDocumento;

    @NotBlank(message = "Ingresa tu nombre o razón social")
    @Size(max = 120, message = "Máximo 120 caracteres")
    private String nombre;

    @NotBlank(message = "Ingresa tu celular")
    @Pattern(regexp = "9\\d{8}", message = "El celular tiene 9 dígitos y empieza con 9")
    private String telefono;

    @Size(max = 20, message = "Máximo 20 caracteres")
    private String licencia;

    @NotBlank(message = "Ingresa tu correo")
    @Email(message = "Correo no válido")
    private String correo;

    @NotBlank(message = "Ingresa una contraseña")
    @Size(min = 8, max = 64, message = "La contraseña debe tener entre 8 y 64 caracteres")
    private String clave;

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
