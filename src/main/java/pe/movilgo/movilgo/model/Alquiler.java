package pe.movilgo.movilgo.model;

import java.time.LocalDateTime;

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

/** Entrega y devolución de un vehículo reservado y pagado. */
@Entity
@Table(name = "alquiler")
public class Alquiler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlquiler;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_reserva", nullable = false, unique = true)
    private Reserva reserva;

    @Column(nullable = false)
    private LocalDateTime entregadoEn;

    @Column(nullable = false)
    private LocalDateTime fechaLimite;

    private LocalDateTime devueltoEn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoAlquiler estado = EstadoAlquiler.EN_CURSO;

    @Column(length = 500)
    private String observaciones;

    protected Alquiler() {
    }

    public Alquiler(Reserva reserva, LocalDateTime entregadoEn, LocalDateTime fechaLimite) {
        this.reserva = reserva;
        this.entregadoEn = entregadoEn;
        this.fechaLimite = fechaLimite;
    }

    public void registrarDevolucion(LocalDateTime ahora, String observaciones) {
        this.devueltoEn = ahora;
        this.observaciones = observaciones;
        this.estado = EstadoAlquiler.DEVUELTO;
    }

    public Long getIdAlquiler() {
        return idAlquiler;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public LocalDateTime getEntregadoEn() {
        return entregadoEn;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public LocalDateTime getDevueltoEn() {
        return devueltoEn;
    }

    public EstadoAlquiler getEstado() {
        return estado;
    }

    public void setEstado(EstadoAlquiler estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }
}
