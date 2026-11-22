package pe.movilgo.movilgo.exception;

/** Se lanza cuando una operación incumple una regla del negocio (RN). */
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
