package pe.cibertec.matricula.servicio;

import java.util.List;
import java.util.Optional;
import pe.cibertec.matricula.modelo.EstadoAlumno;

public interface ISistemaMatricula {
    List<AlumnoResumen> alumnos();
    List<CursoResumen> cursos();
    List<MatriculaResumen> matriculas();
    List<RetiroResumen> retiros();

    Optional<AlumnoResumen> alumno(int codigo);
    Optional<CursoResumen> curso(int codigo);
    Optional<MatriculaResumen> matricula(int numero);
    Optional<RetiroResumen> retiro(int numero);

    void agregarAlumno(String nombres, String apellidos, String dni, int edad, int celular);
    void editarAlumno(int codigo, String nombres, String apellidos, int edad, int celular);
    void eliminarAlumno(int codigo);
    void agregarCurso(int codigo, String asignatura, int ciclo, int creditos, int horas);
    void editarCurso(int codigo, String asignatura, int ciclo, int creditos, int horas);
    void eliminarCurso(int codigo);
    void matricular(int codigoAlumno, int codigoCurso);
    void cambiarCurso(int numeroMatricula, int codigoCurso);
    void cancelarMatricula(int numeroMatricula);
    void retirar(int numeroMatricula);
    void cambiarCursoRetirado(int numeroRetiro, int codigoCurso);
    void cancelarRetiro(int numeroRetiro);
}
