package pe.movilgo.movilgo.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehiculo;

    @Column(nullable = false, length = 40)
    private String marca;

    @Column(nullable = false, length = 40)
    private String modelo;

    @Column(nullable = false, length = 40)
    private String categoria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioDia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoVehiculo estado = EstadoVehiculo.DISPONIBLE;

    @Version            // bloqueo optimista: detecta modificaciones simultáneas (RN-02)
    private Integer version;

    protected Vehiculo() {
    }

    public Vehiculo(String marca, String modelo, String categoria, BigDecimal precioDia) {
        this.marca = marca;
        this.modelo = modelo;
        this.categoria = categoria;
        this.precioDia = precioDia;
    }

    public Long getIdVehiculo() { return idVehiculo; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getCategoria() { return categoria; }
    public BigDecimal getPrecioDia() { return precioDia; }
    public EstadoVehiculo getEstado() { return estado; }
    public void setEstado(EstadoVehiculo estado) { this.estado = estado; }
    public Integer getVersion() { return version; }

    /** Foto del catálogo (static/img/vehiculos). Si no hay foto, la vista muestra el ícono de la categoría. */
    public String getImagen() {
        String m = modelo == null ? "" : modelo.toLowerCase();
        if (m.contains("hilux")) {
            return "/img/vehiculos/hilux.webp";
        }
        if (m.contains("coaster")) {
            return "/img/vehiculos/coaster.webp";
        }
        if (m.contains("h-1")) {
            return "/img/vehiculos/h1.webp";
        }
        if (m.contains("scooter")) {
            return "/img/vehiculos/scooter.webp";
        }
        return null;
    }

    /** Ícono de Bootstrap Icons según la categoría. */
    public String getIcono() {
        String c = categoria == null ? "" : categoria.toLowerCase();
        if (c.contains("bus")) {
            return "bi-bus-front-fill";
        }
        if (c.contains("scooter")) {
            return "bi-scooter";
        }
        if (c.contains("auto")) {
            return "bi-car-front-fill";
        }
        return "bi-truck-front-fill";
    }
}
