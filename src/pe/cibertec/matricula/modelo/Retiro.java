package pe.cibertec.matricula.modelo;

import java.io.Serializable;

/** Aqui registro la desactivacion temporal de una matricula. */
public class Retiro implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int numRetiro, numMatricula;
    private final String fecha, hora;
    public Retiro(int numero, int matricula, String fecha, String hora) { numRetiro=numero; numMatricula=matricula; this.fecha=fecha; this.hora=hora; }
    public int getNumRetiro() { return numRetiro; } public int getNumMatricula() { return numMatricula; } public String getFecha() { return fecha; } public String getHora() { return hora; }
}
