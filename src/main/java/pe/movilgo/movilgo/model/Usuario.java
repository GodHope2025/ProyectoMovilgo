package pe.movilgo.movilgo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Cuenta de acceso al sistema. La contraseña se guarda cifrada con BCrypt. */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    @Column(nullable = false, length = 100)
    private String claveHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Rol rol;

    @Column(nullable = false)
    private int intentosFallidos;

    @Column(nullable = false)
    private boolean bloqueado;

    protected Usuario() {
    }

    public Usuario(String correo, String claveHash, Rol rol) {
        this.correo = correo;
        this.claveHash = claveHash;
        this.rol = rol;
    }

    /** Suma un intento fallido y bloquea la cuenta al llegar al máximo (prevención de fuerza bruta). */
    public void registrarIntentoFallido(int maximo) {
        intentosFallidos++;
        if (intentosFallidos >= maximo) {
            bloqueado = true;
        }
    }

    public void reiniciarIntentos() {
        intentosFallidos = 0;
    }

    public void desbloquear() {
        bloqueado = false;
        intentosFallidos = 0;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public String getClaveHash() {
        return claveHash;
    }

    public Rol getRol() {
        return rol;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }
}
