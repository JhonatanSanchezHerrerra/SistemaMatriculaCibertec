package pe.cibertec.matricula.modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Curso implements Serializable, Comparable<Curso> {
    private static final long serialVersionUID = 1L;
    private static final int CODIGO_MINIMO = 1000;
    private static final int CODIGO_MAXIMO = 9999;
    private static final int CICLO_MAXIMO = 5;

    private final int codCurso;
    private String asignatura;
    private int ciclo;
    private int creditos;
    private int horas;

    public Curso(int codCurso, String asignatura, int ciclo, int creditos, int horas) {
        validarCodigo(codCurso);
        this.codCurso = codCurso;
        actualizar(asignatura, ciclo, creditos, horas);
    }

    public Curso(Curso otro) {
        this(otro.codCurso, otro.asignatura, otro.ciclo, otro.creditos, otro.horas);
    }

    public int getCodCurso() { return codCurso; }
    public String getAsignatura() { return asignatura; }
    public int getCiclo() { return ciclo; }
    public int getCreditos() { return creditos; }
    public int getHoras() { return horas; }

    public void actualizar(String asignatura, int ciclo, int creditos, int horas) {
        if (asignatura == null || asignatura.isBlank()) {
            throw new IllegalArgumentException("La asignatura es obligatoria.");
        }
        if (ciclo < 0 || ciclo > CICLO_MAXIMO || creditos <= 0 || horas <= 0) {
            throw new IllegalArgumentException("Ciclo, créditos u horas fuera de rango.");
        }
        this.asignatura = asignatura.trim();
        this.ciclo = ciclo;
        this.creditos = creditos;
        this.horas = horas;
    }

    private static void validarCodigo(int codigo) {
        if (codigo < CODIGO_MINIMO || codigo > CODIGO_MAXIMO) {
            throw new IllegalArgumentException("El código del curso debe tener exactamente cuatro dígitos.");
        }
    }

    private void readObject(ObjectInputStream entrada) throws IOException, ClassNotFoundException {
        entrada.defaultReadObject();
        try {
            validarCodigo(codCurso);
            if (asignatura == null || asignatura.isBlank() || ciclo < 0 || ciclo > CICLO_MAXIMO
                    || creditos <= 0 || horas <= 0) {
                throw new IllegalArgumentException("Los datos del curso no son válidos.");
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("El registro del curso no es válido.", e);
        }
    }

    @Override
    public int compareTo(Curso otro) {
        return Integer.compare(codCurso, otro.codCurso);
    }
}
