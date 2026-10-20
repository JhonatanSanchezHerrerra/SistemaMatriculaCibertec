package pe.cibertec.matricula.servicio;

public record MatriculaResumen(int numero, AlumnoResumen alumno,
                               CursoResumen curso, String fecha, String hora) {
}
