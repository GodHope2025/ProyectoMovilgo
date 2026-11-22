package pe.movilgo.movilgo.model;

import java.math.BigDecimal;
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

/** Pago de una reserva. En esta versión la pasarela es simulada. */
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_reserva", nullable = false, unique = true)
    private Reserva reserva;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private MetodoPago metodo;

    @Column(nullable = false)
    private LocalDateTime pagadoEn;

    @Column(nullable = false, unique = true, length = 20)
    private String codigoOperacion;

    protected Pago() {
    }

    public Pago(Reserva reserva, BigDecimal monto, MetodoPago metodo, LocalDateTime pagadoEn, String codigoOperacion) {
        this.reserva = reserva;
        this.monto = monto;
        this.metodo = metodo;
        this.pagadoEn = pagadoEn;
        this.codigoOperacion = codigoOperacion;
    }

    public Long getIdPago() {
        return idPago;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }

    public LocalDateTime getPagadoEn() {
        return pagadoEn;
    }

    public String getCodigoOperacion() {
        return codigoOperacion;
    }
}
