package pe.cibertec.matricula.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pe.cibertec.matricula.modelo.*;
import pe.cibertec.matricula.servicio.SistemaMatricula;

/** Aqui presento los cuatro modulos solicitados y delego las reglas a SistemaMatricula. */
public class VentanaPrincipal extends JFrame {
    private final SistemaMatricula sistema = new SistemaMatricula();
    private final JTable tabla = new JTable();
    private final JLabel titulo = new JLabel("Sistema de Registro y Matricula de Alumnos");
    public VentanaPrincipal() {
        setTitle("Sistema de Matricula"); setSize(1000, 620); setLocationRelativeTo(null); setDefaultCloseOperation(EXIT_ON_CLOSE);
        titulo.setFont(new Font("Arial", Font.BOLD, 20)); titulo.setBorder(BorderFactory.createEmptyBorder(12,12,12,12)); add(titulo, BorderLayout.NORTH);
        JTabbedPane pestañas = new JTabbedPane();
        pestañas.add("Mantenimiento", crearMantenimiento()); pestañas.add("Registro", crearRegistro()); pestañas.add("Consulta", crearConsulta()); pestañas.add("Reportes", crearReportes());
        add(pestañas, BorderLayout.CENTER); mostrarAlumnos();
    }
    private JPanel panelBotones(String... botones) { JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT)); for(String b:botones) { JButton x=new JButton(b); x.addActionListener(e->accion(b)); p.add(x); } return p; }
    private JPanel crearMantenimiento() { JPanel p=new JPanel(new BorderLayout()); p.add(panelBotones("Ver alumnos","Nuevo alumno","Editar alumno","Eliminar alumno","Ver cursos","Nuevo curso","Editar curso","Eliminar curso"),BorderLayout.NORTH); p.add(new JScrollPane(tabla),BorderLayout.CENTER); return p; }
    private JPanel crearRegistro() { JPanel p=new JPanel(new BorderLayout()); p.add(panelBotones("Ver matriculas","Matricular","Cambiar curso","Cancelar matricula","Ver retiros","Registrar retiro","Cambiar curso retirado","Cancelar retiro"),BorderLayout.NORTH); p.add(new JScrollPane(tabla),BorderLayout.CENTER); return p; }
    private JPanel crearConsulta() { JPanel p=new JPanel(new BorderLayout()); p.add(panelBotones("Buscar alumno","Buscar curso","Buscar matricula","Buscar retiro"),BorderLayout.NORTH); p.add(new JScrollPane(tabla),BorderLayout.CENTER); return p; }
    private JPanel crearReportes() { JPanel p=new JPanel(new BorderLayout()); p.add(panelBotones("Pendientes","Matricula vigente","Matriculados por curso"),BorderLayout.NORTH); p.add(new JScrollPane(tabla),BorderLayout.CENTER); return p; }
    private void accion(String opcion) { try { switch(opcion) {
        case "Ver alumnos": mostrarAlumnos(); break; case "Nuevo alumno": nuevoAlumno(); break; case "Editar alumno": editarAlumno(); break; case "Eliminar alumno": eliminarAlumno(); break;
        case "Ver cursos": mostrarCursos(); break; case "Nuevo curso": nuevoCurso(); break; case "Editar curso": editarCurso(); break; case "Eliminar curso": eliminarCurso(); break;
        case "Ver matriculas": mostrarMatriculas(); break; case "Matricular": matricular(); break; case "Cambiar curso": cambiarCurso(); break; case "Cancelar matricula": cancelarMatricula(); break;
        case "Ver retiros": mostrarRetiros(); break; case "Registrar retiro": retirar(); break; case "Cambiar curso retirado": cambiarCursoRetirado(); break; case "Cancelar retiro": cancelarRetiro(); break;
        case "Buscar alumno": buscarAlumno(); break; case "Buscar curso": buscarCurso(); break; case "Buscar matricula": buscarMatricula(); break; case "Buscar retiro": buscarRetiro(); break;
        case "Pendientes": reporteEstado(Alumno.REGISTRADO,"Alumnos con matricula pendiente"); break; case "Matricula vigente": reporteEstado(Alumno.MATRICULADO,"Alumnos con matricula vigente"); break; case "Matriculados por curso": reporteCurso(); break;
    }} catch (NumberFormatException e) { error("Ingrese numeros validos en los campos numericos."); } catch (IllegalArgumentException e) { error(e.getMessage()); } }
    private String pedir(String texto) { String r=JOptionPane.showInputDialog(this,texto); if(r==null) throw new IllegalArgumentException("Operacion cancelada."); if(r.trim().isEmpty()) throw new IllegalArgumentException("Complete todos los datos."); return r.trim(); }
    private int numero(String texto) { return Integer.parseInt(pedir(texto)); }
    private boolean confirmar(String texto) { return JOptionPane.showConfirmDialog(this,texto,"Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION; }
    private void nuevoAlumno() { sistema.agregarAlumno(pedir("Nombres:"),pedir("Apellidos:"),pedir("DNI:"),numero("Edad:"),numero("Celular:")); mostrarAlumnos(); }
    private void editarAlumno() { int c=numero("Codigo de alumno:"); Alumno a=obligatorio(sistema.alumno(c)); sistema.editarAlumno(c,pedir("Nombres:"),pedir("Apellidos:"),numero("Edad:"),numero("Celular:")); mostrarAlumnos(); }
    private void eliminarAlumno() { int c=numero("Codigo de alumno:"); if(confirmar("Se eliminara el alumno. Desea continuar?")) { sistema.eliminarAlumno(c); mostrarAlumnos(); } }
    private void nuevoCurso() { sistema.agregarCurso(numero("Codigo de 4 digitos:"),pedir("Asignatura:"),numero("Ciclo (0 a 5):"),numero("Creditos:"),numero("Horas:")); mostrarCursos(); }
    private void editarCurso() { int c=numero("Codigo de curso:"); obligatorioCurso(sistema.curso(c)); sistema.editarCurso(c,pedir("Asignatura:"),numero("Ciclo (0 a 5):"),numero("Creditos:"),numero("Horas:")); mostrarCursos(); }
    private void eliminarCurso() { int c=numero("Codigo de curso:"); if(confirmar("Se eliminara el curso. Desea continuar?")) { sistema.eliminarCurso(c); mostrarCursos(); } }
    private void matricular() { sistema.matricular(numero("Codigo de alumno:"),numero("Codigo de curso:")); mostrarMatriculas(); }
    private void cambiarCurso() { sistema.cambiarCurso(numero("Numero de matricula:"),numero("Nuevo codigo de curso:")); mostrarMatriculas(); }
    private void cancelarMatricula() { int n=numero("Numero de matricula:"); if(confirmar("Se cancelara la matricula. Desea continuar?")) { sistema.cancelarMatricula(n); mostrarMatriculas(); } }
    private void retirar() { sistema.retirar(numero("Numero de matricula:")); mostrarRetiros(); }
    private void cambiarCursoRetirado() { sistema.cambiarCursoRetirado(numero("Numero de retiro:"),numero("Nuevo codigo de curso:")); mostrarRetiros(); }
    private void cancelarRetiro() { int n=numero("Numero de retiro:"); if(confirmar("Se cancelara el retiro. Desea continuar?")) { sistema.cancelarRetiro(n); mostrarRetiros(); } }
    private Alumno obligatorio(Alumno a) { if(a==null) throw new IllegalArgumentException("Alumno no encontrado."); return a; }
    private Curso obligatorioCurso(Curso c) { if(c==null) throw new IllegalArgumentException("Curso no encontrado."); return c; }
    private void buscarAlumno() { Alumno a=obligatorio(sistema.alumno(numero("Codigo de alumno:"))); String curso="Sin matricula"; for(Matricula m:sistema.matriculas()) if(m.getCodAlumno()==a.getCodAlumno()) { Curso c=sistema.curso(m.getCodCurso()); curso=c.getCodCurso()+" - "+c.getAsignatura(); } modelo(new String[]{"Codigo","Nombres","Apellidos","DNI","Edad","Celular","Estado","Curso"},new Object[][]{{a.getCodAlumno(),a.getNombres(),a.getApellidos(),a.getDni(),a.getEdad(),a.getCelular(),a.estadoTexto(),curso}}); }
    private void buscarCurso() { Curso c=sistema.curso(numero("Codigo de curso:")); if(c==null) throw new IllegalArgumentException("Curso no encontrado."); modelo(new String[]{"Codigo","Asignatura","Ciclo","Creditos","Horas"},new Object[][]{{c.getCodCurso(),c.getAsignatura(),c.getCiclo(),c.getCreditos(),c.getHoras()}}); }
    private void buscarMatricula() { Matricula m=sistema.matricula(numero("Numero de matricula:")); if(m==null) throw new IllegalArgumentException("Matricula no encontrada."); Alumno a=sistema.alumno(m.getCodAlumno()); Curso c=sistema.curso(m.getCodCurso()); modelo(new String[]{"Matricula","Alumno","DNI","Curso","Fecha","Hora"},new Object[][]{{m.getNumMatricula(),a.nombreCompleto(),a.getDni(),c.getAsignatura(),m.getFecha(),m.getHora()}}); }
    private void buscarRetiro() { Retiro r=sistema.retiro(numero("Numero de retiro:")); if(r==null) throw new IllegalArgumentException("Retiro no encontrado."); Matricula m=sistema.matricula(r.getNumMatricula()); Alumno a=sistema.alumno(m.getCodAlumno()); Curso c=sistema.curso(m.getCodCurso()); modelo(new String[]{"Retiro","Matricula","Alumno","Curso","Fecha","Hora"},new Object[][]{{r.getNumRetiro(),m.getNumMatricula(),a.nombreCompleto(),c.getAsignatura(),r.getFecha(),r.getHora()}}); }
    private void mostrarAlumnos() { Object[][] d=sistema.alumnos().stream().map(a->new Object[]{a.getCodAlumno(),a.getNombres(),a.getApellidos(),a.getDni(),a.getEdad(),a.getCelular(),a.estadoTexto()}).toArray(Object[][]::new); modelo(new String[]{"Codigo","Nombres","Apellidos","DNI","Edad","Celular","Estado"},d); }
    private void mostrarCursos() { Object[][] d=sistema.cursos().stream().map(c->new Object[]{c.getCodCurso(),c.getAsignatura(),c.getCiclo(),c.getCreditos(),c.getHoras()}).toArray(Object[][]::new); modelo(new String[]{"Codigo","Asignatura","Ciclo","Creditos","Horas"},d); }
    private void mostrarMatriculas() { Object[][] d=sistema.matriculas().stream().map(m->new Object[]{m.getNumMatricula(),m.getCodAlumno(),m.getCodCurso(),m.getFecha(),m.getHora()}).toArray(Object[][]::new); modelo(new String[]{"Matricula","Cod. alumno","Cod. curso","Fecha","Hora"},d); }
    private void mostrarRetiros() { Object[][] d=sistema.retiros().stream().map(r->new Object[]{r.getNumRetiro(),r.getNumMatricula(),r.getFecha(),r.getHora()}).toArray(Object[][]::new); modelo(new String[]{"Retiro","Matricula","Fecha","Hora"},d); }
    private void reporteEstado(int estado,String nombre) { List<Alumno> lista=sistema.alumnos().stream().filter(a->a.getEstado()==estado).toList(); Object[][] d=lista.stream().map(a->new Object[]{a.getCodAlumno(),a.nombreCompleto(),a.getDni(),a.getCelular(),a.estadoTexto()}).toArray(Object[][]::new); titulo.setText(nombre); modelo(new String[]{"Codigo","Alumno","DNI","Celular","Estado"},d); }
    private void reporteCurso() { java.util.ArrayList<Object[]> filas=new java.util.ArrayList<>(); for(Curso c:sistema.cursos()) { boolean tiene=false; for(Matricula m:sistema.matriculas()) if(m.getCodCurso()==c.getCodCurso() && sistema.alumno(m.getCodAlumno()).getEstado()==Alumno.MATRICULADO) { filas.add(new Object[]{c.getCodCurso(),c.getAsignatura(),sistema.alumno(m.getCodAlumno()).nombreCompleto()}); tiene=true; } if(!tiene) filas.add(new Object[]{c.getCodCurso(),c.getAsignatura(),"Sin alumnos matriculados"}); } titulo.setText("Alumnos matriculados por curso"); modelo(new String[]{"Codigo curso","Asignatura","Alumno"},filas.toArray(Object[][]::new)); }
    private void modelo(String[] columnas,Object[][] datos) { tabla.setModel(new DefaultTableModel(datos,columnas) { public boolean isCellEditable(int r,int c) { return false; }}); }
    private void error(String mensaje) { JOptionPane.showMessageDialog(this,mensaje,"Aviso",JOptionPane.WARNING_MESSAGE); }
}
