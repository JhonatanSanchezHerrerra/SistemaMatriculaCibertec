package pe.cibertec.matricula.modelo;

public enum EstadoAlumno {
    REGISTRADO(0, "Registrado"),
    MATRICULADO(1, "Matriculado"),
    RETIRADO(2, "Retirado");

    private final int codigoPersistido;
    private final String etiqueta;

    EstadoAlumno(int codigoPersistido, String etiqueta) {
        this.codigoPersistido = codigoPersistido;
        this.etiqueta = etiqueta;
    }

    public String etiqueta() {
        return etiqueta;
    }

    public static EstadoAlumno desdeCodigoPersistido(int codigo) {
        for (EstadoAlumno estado : values()) {
            if (estado.codigoPersistido == codigo) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de alumno persistido no válido: " + codigo);
    }

    public int codigoPersistido() {
        return codigoPersistido;
    }
}
