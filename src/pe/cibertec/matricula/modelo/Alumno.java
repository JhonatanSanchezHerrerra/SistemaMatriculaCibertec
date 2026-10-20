package pe.cibertec.matricula.modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Alumno implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int ESTADO_REGISTRADO = 0;

    private int codAlumno;
    private String nombres;
    private String apellidos;
    private final String dni;
    private int edad;
    private int celular;
    private int estado;

    public Alumno(int codAlumno, String nombres, String apellidos, String dni,
                  int edad, int celular) {
        this.codAlumno = codAlumno;
        this.nombres = textoObligatorio(nombres, "Nombres");
        this.apellidos = textoObligatorio(apellidos, "Apellidos");
        this.dni = validarDni(dni);
        validarEdad(edad);
        this.edad = edad;
        this.celular = celular;
        this.estado = ESTADO_REGISTRADO;
    }

    public Alumno(Alumno otro) {
        this(otro.codAlumno, otro.nombres, otro.apellidos, otro.dni, otro.edad, otro.celular);
        estado = otro.estado;
    }

    public int getCodAlumno() { return codAlumno; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getDni() { return dni; }
    public int getEdad() { return edad; }
    public int getCelular() { return celular; }
    public EstadoAlumno getEstado() { return EstadoAlumno.desdeCodigoPersistido(estado); }

    public void actualizar(String nombres, String apellidos, int edad, int celular) {
        String nombresValidados = textoObligatorio(nombres, "Nombres");
        String apellidosValidados = textoObligatorio(apellidos, "Apellidos");
        validarEdad(edad);

        this.nombres = nombresValidados;
        this.apellidos = apellidosValidados;
        this.edad = edad;
        this.celular = celular;
    }

    public void marcarMatriculado() {
        exigirEstado(EstadoAlumno.REGISTRADO);
        estado = EstadoAlumno.MATRICULADO.codigoPersistido();
    }

    public void marcarRegistrado() {
        if (getEstado() == EstadoAlumno.RETIRADO) {
            throw new IllegalStateException("Debe cancelar el retiro antes de cancelar la matrícula.");
        }
        exigirEstado(EstadoAlumno.MATRICULADO);
        estado = ESTADO_REGISTRADO;
    }

    public void marcarRetirado() {
        exigirEstado(EstadoAlumno.MATRICULADO);
        estado = EstadoAlumno.RETIRADO.codigoPersistido();
    }

    public void cancelarRetiro() {
        exigirEstado(EstadoAlumno.RETIRADO);
        estado = EstadoAlumno.MATRICULADO.codigoPersistido();
    }

    public String estadoTexto() { return getEstado().etiqueta(); }
    public String nombreCompleto() { return nombres + " " + apellidos; }

    private void exigirEstado(EstadoAlumno esperado) {
        if (getEstado() != esperado) {
            throw new IllegalStateException("Transición de estado no permitida: " + getEstado());
        }
    }

    private static String validarDni(String dni) {
        if (dni == null || !dni.matches("[0-9]{8}")) {
            throw new IllegalArgumentException("El DNI debe contener exactamente ocho dígitos.");
        }
        return dni;
    }

    private static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        return valor.trim();
    }

    private static void validarEdad(int edad) {
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa.");
        }
    }

    private void readObject(ObjectInputStream entrada) throws IOException, ClassNotFoundException {
        entrada.defaultReadObject();
        try {
            validarDni(dni);
            validarEdad(edad);
            EstadoAlumno.desdeCodigoPersistido(estado);
        } catch (IllegalArgumentException e) {
            throw new IOException("El registro del alumno no es válido.", e);
        }
        if (nombres == null || apellidos == null) {
            throw new IOException("El registro del alumno está incompleto.");
        }
    }
}
