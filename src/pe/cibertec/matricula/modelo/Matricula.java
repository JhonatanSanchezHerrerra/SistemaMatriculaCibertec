package pe.cibertec.matricula.modelo;

import java.io.Serializable;

/** Aqui relaciono un alumno con el unico curso que tiene matriculado. */
public class Matricula implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int numMatricula, codAlumno;
    private int codCurso;
    private final String fecha, hora;
    public Matricula(int numero, int alumno, int curso, String fecha, String hora) { numMatricula=numero; codAlumno=alumno; codCurso=curso; this.fecha=fecha; this.hora=hora; }
    public int getNumMatricula() { return numMatricula; } public int getCodAlumno() { return codAlumno; } public int getCodCurso() { return codCurso; } public String getFecha() { return fecha; } public String getHora() { return hora; }
    public void setCodCurso(int codCurso) { this.codCurso = codCurso; }
}
