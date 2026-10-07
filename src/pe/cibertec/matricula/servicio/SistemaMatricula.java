package pe.cibertec.matricula.servicio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import pe.cibertec.matricula.modelo.*;

/** Aqui concentro las reglas del negocio; la GUI solo pide acciones a esta clase. */
public class SistemaMatricula {
    private final Repositorio repo = new Repositorio();
    private final ArrayList<Alumno> alumnos = repo.leer("alumnos.dat");
    private final ArrayList<Curso> cursos = repo.leer("cursos.dat");
    private final ArrayList<Matricula> matriculas = repo.leer("matriculas.dat");
    private final ArrayList<Retiro> retiros = repo.leer("retiros.dat");
    private String fecha() { return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
    private String hora() { return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")); }
    private void guardar() { repo.grabar("alumnos.dat", alumnos); repo.grabar("cursos.dat", cursos); repo.grabar("matriculas.dat", matriculas); repo.grabar("retiros.dat", retiros); }
    private int siguienteAlumno() { return alumnos.stream().mapToInt(Alumno::getCodAlumno).max().orElse(202010000) + 1; }
    private int siguienteMatricula() { return matriculas.stream().mapToInt(Matricula::getNumMatricula).max().orElse(100000) + 1; }
    private int siguienteRetiro() { return retiros.stream().mapToInt(Retiro::getNumRetiro).max().orElse(200000) + 1; }
    public List<Alumno> alumnos() { return new ArrayList<>(alumnos); }
    public List<Curso> cursos() { ArrayList<Curso> r=new ArrayList<>(cursos); Collections.sort(r); return r; }
    public List<Matricula> matriculas() { return new ArrayList<>(matriculas); }
    public List<Retiro> retiros() { return new ArrayList<>(retiros); }
    public Alumno alumno(int codigo) { return alumnos.stream().filter(a->a.getCodAlumno()==codigo).findFirst().orElse(null); }
    public Curso curso(int codigo) { return cursos.stream().filter(c->c.getCodCurso()==codigo).findFirst().orElse(null); }
    public Matricula matricula(int numero) { return matriculas.stream().filter(m->m.getNumMatricula()==numero).findFirst().orElse(null); }
    public Retiro retiro(int numero) { return retiros.stream().filter(r->r.getNumRetiro()==numero).findFirst().orElse(null); }
    public void agregarAlumno(String n,String a,String dni,int edad,int cel) { if (alumnos.stream().anyMatch(x->x.getDni().equals(dni))) throw new IllegalArgumentException("El DNI ya esta registrado."); alumnos.add(new Alumno(siguienteAlumno(),n,a,dni,edad,cel)); guardar(); }
    public void editarAlumno(int cod,String n,String a,int edad,int cel) { Alumno x=obligatorio(alumno(cod),"Alumno"); x.actualizar(n,a,edad,cel); guardar(); }
    public void eliminarAlumno(int cod) { Alumno x=obligatorio(alumno(cod),"Alumno"); if(x.getEstado()!=Alumno.REGISTRADO) throw new IllegalArgumentException("Solo se elimina un alumno registrado."); alumnos.remove(x); guardar(); }
    public void agregarCurso(int cod,String asig,int ciclo,int cred,int horas) { if(curso(cod)!=null) throw new IllegalArgumentException("El codigo de curso ya existe."); cursos.add(new Curso(cod,asig,ciclo,cred,horas)); guardar(); }
    public void editarCurso(int cod,String asig,int ciclo,int cred,int horas) { obligatorio(curso(cod),"Curso").actualizar(asig,ciclo,cred,horas); guardar(); }
    public void eliminarCurso(int cod) { if(matriculas.stream().anyMatch(m->m.getCodCurso()==cod)) throw new IllegalArgumentException("No se elimina un curso con matriculas."); cursos.remove(obligatorio(curso(cod),"Curso")); guardar(); }
    private <T> T obligatorio(T valor,String nombre) { if(valor==null) throw new IllegalArgumentException(nombre+" no encontrado."); return valor; }
}
