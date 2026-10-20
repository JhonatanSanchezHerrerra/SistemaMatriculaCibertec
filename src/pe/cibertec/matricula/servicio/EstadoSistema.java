package pe.cibertec.matricula.servicio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import pe.cibertec.matricula.modelo.Alumno;
import pe.cibertec.matricula.modelo.Curso;
import pe.cibertec.matricula.modelo.Matricula;
import pe.cibertec.matricula.modelo.Retiro;

final class EstadoSistema implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ArrayList<Alumno> alumnos;
    private final ArrayList<Curso> cursos;
    private final ArrayList<Matricula> matriculas;
    private final ArrayList<Retiro> retiros;

    EstadoSistema(List<Alumno> alumnos, List<Curso> cursos,
                  List<Matricula> matriculas, List<Retiro> retiros) {
        this.alumnos = new ArrayList<>(alumnos);
        this.cursos = new ArrayList<>(cursos);
        this.matriculas = new ArrayList<>(matriculas);
        this.retiros = new ArrayList<>(retiros);
    }

    static EstadoSistema vacio() {
        return new EstadoSistema(List.of(), List.of(), List.of(), List.of());
    }

    EstadoSistema copiar() {
        return new EstadoSistema(
                alumnos.stream().map(Alumno::new).toList(),
                cursos.stream().map(Curso::new).toList(),
                matriculas.stream().map(Matricula::new).toList(),
                retiros.stream().map(Retiro::new).toList());
    }

    ArrayList<Alumno> alumnos() { return alumnos; }
    ArrayList<Curso> cursos() { return cursos; }
    ArrayList<Matricula> matriculas() { return matriculas; }
    ArrayList<Retiro> retiros() { return retiros; }
}
