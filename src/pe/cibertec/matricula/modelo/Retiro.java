package pe.cibertec.matricula.modelo;

import java.io.Serializable;
import java.util.Objects;

public class Retiro implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int numRetiro;
    private final int numMatricula;
    private final String fecha;
    private final String hora;

    public Retiro(int numRetiro, int numMatricula, String fecha, String hora) {
        this.numRetiro = numRetiro;
        this.numMatricula = numMatricula;
        this.fecha = Objects.requireNonNull(fecha, "fecha");
        this.hora = Objects.requireNonNull(hora, "hora");
    }

    public Retiro(Retiro otro) {
        this(otro.numRetiro, otro.numMatricula, otro.fecha, otro.hora);
    }

    public int getNumRetiro() { return numRetiro; }
    public int getNumMatricula() { return numMatricula; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
}
