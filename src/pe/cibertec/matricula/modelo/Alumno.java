package pe.cibertec.matricula.modelo;

import java.io.Serializable;

/** Aqui represento la informacion que el sistema necesita conservar de cada alumno. */
public class Alumno implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int REGISTRADO = 0, MATRICULADO = 1, RETIRADO = 2;
    private final int codAlumno;
    private String nombres, apellidos, dni;
    private int edad, celular, estado;

    public Alumno(int codAlumno, String nombres, String apellidos, String dni, int edad, int celular) {
        this.codAlumno = codAlumno; this.nombres = nombres; this.apellidos = apellidos;
        this.dni = dni; this.edad = edad; this.celular = celular; this.estado = REGISTRADO;
    }
    public int getCodAlumno() { return codAlumno; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getDni() { return dni; }
    public int getEdad() { return edad; }
    public int getCelular() { return celular; }
    public int getEstado() { return estado; }
    public void actualizar(String nombres, String apellidos, int edad, int celular) { this.nombres=nombres; this.apellidos=apellidos; this.edad=edad; this.celular=celular; }
    public void setEstado(int estado) { this.estado = estado; }
    public String estadoTexto() { return estado == REGISTRADO ? "Registrado" : estado == MATRICULADO ? "Matriculado" : "Retirado"; }
    public String nombreCompleto() { return nombres + " " + apellidos; }
}
