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
}
