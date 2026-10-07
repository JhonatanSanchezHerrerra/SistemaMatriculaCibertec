package pe.cibertec.matricula.modelo;

import java.io.Serializable;

/** Aqui guardo los datos editables de los cursos disponibles para matricula. */
public class Curso implements Serializable, Comparable<Curso> {
    private static final long serialVersionUID = 1L;
    private final int codCurso;
    private String asignatura;
    private int ciclo, creditos, horas;
    public Curso(int codCurso, String asignatura, int ciclo, int creditos, int horas) { this.codCurso=codCurso; this.asignatura=asignatura; this.ciclo=ciclo; this.creditos=creditos; this.horas=horas; }
    public int getCodCurso() { return codCurso; } public String getAsignatura() { return asignatura; } public int getCiclo() { return ciclo; } public int getCreditos() { return creditos; } public int getHoras() { return horas; }
    public void actualizar(String asignatura, int ciclo, int creditos, int horas) { this.asignatura=asignatura; this.ciclo=ciclo; this.creditos=creditos; this.horas=horas; }
    @Override public int compareTo(Curso otro) { return Integer.compare(codCurso, otro.codCurso); }
}
