package pe.cibertec.matricula.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import pe.cibertec.matricula.modelo.EstadoAlumno;
import pe.cibertec.matricula.servicio.ConsultasMatricula;
import pe.cibertec.matricula.servicio.ISistemaMatricula;
import pe.cibertec.matricula.servicio.PersistenciaException;
import pe.cibertec.matricula.servicio.TablaConsulta;

public class VentanaPrincipal extends JFrame {
    private static final int ANCHO_VENTANA = 1000;
    private static final int ALTO_VENTANA = 620;
    private static final int TAMANO_TITULO = 20;

    private final ISistemaMatricula sistema;
    private final ConsultasMatricula consultas;
    private final List<JTable> tablas = new ArrayList<>();
    private final JLabel titulo = new JLabel("Sistema de Registro y Matrícula de Alumnos");
    private JTable tablaActiva;

    public VentanaPrincipal(ISistemaMatricula sistema) {
        this.sistema = Objects.requireNonNull(sistema, "sistema");
        consultas = new ConsultasMatricula(sistema);
        configurarVentana();
        mostrar(consultas.alumnos());
    }

    private void configurarVentana() {
        setTitle("Sistema de Matrícula");
        setSize(ANCHO_VENTANA, ALTO_VENTANA);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        titulo.setFont(new Font("Arial", Font.BOLD, TAMANO_TITULO));
        titulo.setHorizontalAlignment(SwingConstants.LEFT);
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(titulo, BorderLayout.NORTH);

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.add("Mantenimiento", crearPanel("Ver alumnos", "Nuevo alumno", "Editar alumno",
                "Eliminar alumno", "Ver cursos", "Nuevo curso", "Editar curso", "Eliminar curso"));
        pestañas.add("Registro", crearPanel("Ver matrículas", "Matricular", "Cambiar curso",
                "Cancelar matrícula", "Ver retiros", "Registrar retiro", "Cambiar curso retirado",
                "Cancelar retiro"));
        pestañas.add("Consulta", crearPanel("Buscar alumno", "Buscar curso", "Buscar matrícula", "Buscar retiro"));
        pestañas.add("Reportes", crearPanel("Pendientes", "Matrícula vigente", "Matriculados por curso"));
        pestañas.addChangeListener(evento -> tablaActiva = tablas.get(pestañas.getSelectedIndex()));
        tablaActiva = tablas.get(pestañas.getSelectedIndex());
        add(pestañas, BorderLayout.CENTER);
    }

    private JPanel crearPanel(String... acciones) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTable tablaPanel = new JTable();
        tablaPanel.setCellSelectionEnabled(true);
        tablas.add(tablaPanel);
        for (String accion : acciones) {
            JButton boton = new JButton(accion);
            boton.addActionListener(evento -> ejecutar(accion));
            botones.add(boton);
        }
        JButton copiarCodigo = new JButton("Copiar código");
        copiarCodigo.setToolTipText("Seleccione la celda de un código y pulse para copiarlo.");
        copiarCodigo.addActionListener(evento -> copiarCodigo(tablaPanel));
        botones.add(copiarCodigo);
        panel.add(botones, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaPanel), BorderLayout.CENTER);
        return panel;
    }

    private void ejecutar(String accion) {
        try {
            switch (accion) {
                case "Ver alumnos" -> mostrar(consultas.alumnos());
                case "Nuevo alumno" -> nuevoAlumno();
                case "Editar alumno" -> editarAlumno();
                case "Eliminar alumno" -> eliminarAlumno();
                case "Ver cursos" -> mostrar(consultas.cursos());
                case "Nuevo curso" -> nuevoCurso();
                case "Editar curso" -> editarCurso();
                case "Eliminar curso" -> eliminarCurso();
                case "Ver matrículas" -> mostrar(consultas.matriculas());
                case "Matricular" -> matricular();
                case "Cambiar curso" -> cambiarCurso();
                case "Cancelar matrícula" -> cancelarMatricula();
                case "Ver retiros" -> mostrar(consultas.retiros());
                case "Registrar retiro" -> retirar();
                case "Cambiar curso retirado" -> cambiarCursoRetirado();
                case "Cancelar retiro" -> cancelarRetiro();
                case "Buscar alumno" -> mostrar(consultas.alumno(numero("Código de alumno:")));
                case "Buscar curso" -> mostrar(consultas.curso(numero("Código de curso:")));
                case "Buscar matrícula" -> mostrar(consultas.matricula(numero("Número de matrícula:")));
                case "Buscar retiro" -> mostrar(consultas.retiro(numero("Número de retiro:")));
                case "Pendientes" -> {
                    titulo.setText("Alumnos con matrícula pendiente");
                    mostrar(consultas.porEstado(EstadoAlumno.REGISTRADO));
                }
                case "Matrícula vigente" -> {
                    titulo.setText("Alumnos con matrícula vigente");
                    mostrar(consultas.porEstado(EstadoAlumno.MATRICULADO));
                }
                case "Matriculados por curso" -> {
                    titulo.setText("Alumnos matriculados por curso");
                    mostrar(consultas.matriculadosPorCurso());
                }
                default -> throw new IllegalArgumentException("Acción no reconocida: " + accion);
            }
        } catch (OperacionCancelada e) {
            return;
        } catch (NumberFormatException e) {
            error("Ingrese números válidos en los campos numéricos.");
        } catch (PersistenciaException e) {
            error("No se pudo guardar o leer la información: " + e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            error(e.getMessage());
        }
    }

    private void nuevoAlumno() {
        sistema.agregarAlumno(pedir("Nombres:"), pedir("Apellidos:"), pedir("DNI:"),
                numero("Edad:"), numero("Celular:"));
        mostrar(consultas.alumnos());
    }

    private void editarAlumno() {
        int codigo = numero("Código de alumno:");
        sistema.editarAlumno(codigo, pedir("Nombres:"), pedir("Apellidos:"),
                numero("Edad:"), numero("Celular:"));
        mostrar(consultas.alumnos());
    }

    private void eliminarAlumno() {
        int codigo = numero("Código de alumno:");
        if (confirmar("Se eliminará el alumno. ¿Desea continuar?")) {
            sistema.eliminarAlumno(codigo);
            mostrar(consultas.alumnos());
        }
    }

    private void nuevoCurso() {
        sistema.agregarCurso(numero("Código de curso:"), pedir("Asignatura:"),
                numero("Ciclo (0 a 5):"), numero("Créditos:"), numero("Horas:"));
        mostrar(consultas.cursos());
    }

    private void editarCurso() {
        int codigo = numero("Código de curso:");
        sistema.editarCurso(codigo, pedir("Asignatura:"), numero("Ciclo (0 a 5):"),
                numero("Créditos:"), numero("Horas:"));
        mostrar(consultas.cursos());
    }

    private void eliminarCurso() {
        int codigo = numero("Código de curso:");
        if (confirmar("Se eliminará el curso. ¿Desea continuar?")) {
            sistema.eliminarCurso(codigo);
            mostrar(consultas.cursos());
        }
    }

    private void matricular() {
        sistema.matricular(numero("Código de alumno:"), numero("Código de curso:"));
        mostrar(consultas.matriculas());
    }

    private void cambiarCurso() {
        sistema.cambiarCurso(numero("Número de matrícula:"), numero("Nuevo código de curso:"));
        mostrar(consultas.matriculas());
    }

    private void cancelarMatricula() {
        int numero = numero("Número de matrícula:");
        if (confirmar("Se cancelará la matrícula. ¿Desea continuar?")) {
            sistema.cancelarMatricula(numero);
            mostrar(consultas.matriculas());
        }
    }

    private void retirar() {
        sistema.retirar(numero("Número de matrícula:"));
        mostrar(consultas.retiros());
    }

    private void cambiarCursoRetirado() {
        sistema.cambiarCursoRetirado(numero("Número de retiro:"), numero("Nuevo código de curso:"));
        mostrar(consultas.retiros());
    }

    private void cancelarRetiro() {
        int numero = numero("Número de retiro:");
        if (confirmar("Se cancelará el retiro. ¿Desea continuar?")) {
            sistema.cancelarRetiro(numero);
            mostrar(consultas.retiros());
        }
    }

    private String pedir(String mensaje) {
        String valor = JOptionPane.showInputDialog(this, mensaje);
        if (valor == null) {
            throw new OperacionCancelada();
        }
        if (valor.isBlank()) {
            throw new IllegalArgumentException("Complete todos los datos.");
        }
        return valor.trim();
    }

    private int numero(String mensaje) {
        return Integer.parseInt(pedir(mensaje));
    }

    private boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(this, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    private void mostrar(TablaConsulta resultado) {
        tablaActiva.setModel(new DefaultTableModel(resultado.filas(), resultado.columnas()) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        });
    }

    private void copiarCodigo(JTable tabla) {
        int fila = tabla.getSelectedRow();
        int columna = tabla.getSelectedColumn();
        if (fila < 0 || columna < 0 || !tabla.getColumnName(columna).startsWith("Código ")) {
            error("Seleccione una celda de código para copiarla.");
            return;
        }

        Object valor = tabla.getValueAt(fila, columna);
        if (valor == null || valor.toString().isBlank()) {
            error("La celda seleccionada no contiene un código.");
            return;
        }

        try {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(valor.toString()), null);
            JOptionPane.showMessageDialog(this, "Código copiado: " + valor,
                    "Copiado", JOptionPane.INFORMATION_MESSAGE);
        } catch (HeadlessException | IllegalStateException e) {
            error("No se pudo acceder al portapapeles.");
        }
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private static final class OperacionCancelada extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}





