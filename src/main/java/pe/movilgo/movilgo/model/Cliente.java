package pe.movilgo.movilgo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/** Persona (DNI) o empresa (RUC) que alquila vehículos. */
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private TipoDocumento tipoDocumento;

    @Column(nullable = false, unique = true, length = 11)
    private String numeroDocumento;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 15)
    private String telefono;

    @Column(length = 20)
    private String licencia;

    @Column(nullable = false)
    private boolean enListaNegra;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    protected Cliente() {
    }

    public Cliente(TipoDocumento tipoDocumento, String numeroDocumento, String nombre, String telefono,
            String licencia, Usuario usuario) {
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.licencia = licencia;
        this.usuario = usuario;
    }

    public boolean esEmpresa() {
        return tipoDocumento == TipoDocumento.RUC;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getLicencia() {
        return licencia;
    }

    public boolean isEnListaNegra() {
        return enListaNegra;
    }

    public void setEnListaNegra(boolean enListaNegra) {
        this.enListaNegra = enListaNegra;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
