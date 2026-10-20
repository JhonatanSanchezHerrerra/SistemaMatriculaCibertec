package pe.cibertec.matricula.servicio;

import java.util.Objects;
import java.util.Optional;

public record AlumnoEstadoResumen(AlumnoResumen alumno, Optional<MatriculaResumen> matricula) {
    public AlumnoEstadoResumen {
        Objects.requireNonNull(alumno, "alumno");
        Objects.requireNonNull(matricula, "matricula");
    }
}
