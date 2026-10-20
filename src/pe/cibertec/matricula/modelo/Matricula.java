package pe.cibertec.matricula.modelo;

import java.io.Serializable;
import java.util.Objects;

public class Matricula implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int numMatricula;
    private final int codAlumno;
    private int codCurso;
    private final String fecha;
    private final String hora;

    public Matricula(int numMatricula, int codAlumno, int codCurso, String fecha, String hora) {
        this.numMatricula = numMatricula;
        this.codAlumno = codAlumno;
        this.codCurso = codCurso;
        this.fecha = Objects.requireNonNull(fecha, "fecha");
        this.hora = Objects.requireNonNull(hora, "hora");
    }

    public Matricula(Matricula otra) {
        this(otra.numMatricula, otra.codAlumno, otra.codCurso, otra.fecha, otra.hora);
    }

    public int getNumMatricula() { return numMatricula; }
    public int getCodAlumno() { return codAlumno; }
    public int getCodCurso() { return codCurso; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }

    public void cambiarCurso(int codCurso) {
        this.codCurso = codCurso;
    }
}
