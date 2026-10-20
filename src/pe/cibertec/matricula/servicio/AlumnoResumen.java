package pe.cibertec.matricula.servicio;

import pe.cibertec.matricula.modelo.EstadoAlumno;

public record AlumnoResumen(int codigo, String nombres, String apellidos, String dni,
                            int edad, int celular, EstadoAlumno estado) {
    public String nombreCompleto() {
        return nombres + " " + apellidos;
    }

    public String estadoTexto() {
        return estado.etiqueta();
    }
}
