package pe.movilgo.movilgo.model;

/** Ciclo de la reserva: 15 minutos para pagar, luego se confirma o expira. */
public enum EstadoReserva {
    PENDIENTE_PAGO, CONFIRMADA, EN_CURSO, FINALIZADA, EXPIRADA, CANCELADA
}
