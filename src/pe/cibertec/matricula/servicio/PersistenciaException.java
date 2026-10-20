package pe.cibertec.matricula.servicio;

public class PersistenciaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }
}
