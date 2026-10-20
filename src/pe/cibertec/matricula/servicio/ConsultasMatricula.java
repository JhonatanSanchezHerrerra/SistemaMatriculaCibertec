package pe.cibertec.matricula.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import pe.cibertec.matricula.modelo.EstadoAlumno;

public final class ConsultasMatricula {
    private static final String[] COLUMNAS_ALUMNO = {
        "Código alumno", "Nombres", "Apellidos", "DNI", "Edad", "Celular", "Estado"
    };
    private static final String[] COLUMNAS_CURSO = {
        "Código curso", "Asignatura", "Créditos", "Ciclo", "Horas"
    };
    private static final String[] COLUMNAS_DATOS_MATRICULA = {
        "Matrícula", "Fecha matrícula", "Hora matrícula"
    };
    private static final String[] COLUMNAS_ESTADO = combinarColumnas(
            COLUMNAS_ALUMNO, COLUMNAS_DATOS_MATRICULA, COLUMNAS_CURSO);

    private final ISistemaMatricula sistema;

    public ConsultasMatricula(ISistemaMatricula sistema) {
        this.sistema = Objects.requireNonNull(sistema, "sistema");
    }

    public TablaConsulta alumnos() {
        Object[][] filas = sistema.alumnos().stream()
                .map(ConsultasMatricula::filaAlumno)
                .toArray(Object[][]::new);
        return new TablaConsulta(COLUMNAS_ALUMNO, filas);
    }

    public TablaConsulta cursos() {
        Object[][] filas = sistema.cursos().stream()
                .map(ConsultasMatricula::filaCurso)
                .toArray(Object[][]::new);
        return new TablaConsulta(COLUMNAS_CURSO, filas);
    }

    public TablaConsulta matriculas() {
        Object[][] filas = sistema.matriculas().stream()
                .map(ConsultasMatricula::filaMatricula)
                .toArray(Object[][]::new);
        return new TablaConsulta(columnasMatricula(), filas);
    }

    public TablaConsulta retiros() {
        Object[][] filas = sistema.retiros().stream()
                .map(ConsultasMatricula::filaRetiro)
                .toArray(Object[][]::new);
        return new TablaConsulta(columnasRetiro(), filas);
    }

    public TablaConsulta alumno(int codigo) {
        AlumnoResumen alumno = sistema.alumno(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado."));
        MatriculaResumen matricula = sistema.matriculas().stream()
                .filter(m -> m.alumno().codigo() == codigo)
                .findFirst()
                .orElse(null);
        Object[] datosMatricula = matricula == null
                ? new Object[]{"", "", ""}
                : new Object[]{matricula.numero(), matricula.fecha(), matricula.hora()};
        Object[] datosCurso = matricula == null
                ? new Object[]{"", "", "", "", ""}
                : filaCurso(matricula.curso());
        Object[] fila = combinar(filaAlumno(alumno), datosMatricula, datosCurso);
        return new TablaConsulta(columnasConsultaAlumno(), new Object[][]{fila});
    }

    public TablaConsulta curso(int codigo) {
        CursoResumen curso = sistema.curso(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado."));
        return new TablaConsulta(COLUMNAS_CURSO, new Object[][]{filaCurso(curso)});
    }

    public TablaConsulta matricula(int numero) {
        MatriculaResumen matricula = sistema.matricula(numero)
                .orElseThrow(() -> new IllegalArgumentException("Matrícula no encontrada."));
        return new TablaConsulta(columnasMatricula(), new Object[][]{filaMatricula(matricula)});
    }

    public TablaConsulta retiro(int numero) {
        RetiroResumen retiro = sistema.retiro(numero)
                .orElseThrow(() -> new IllegalArgumentException("Retiro no encontrado."));
        return new TablaConsulta(columnasRetiro(), new Object[][]{filaRetiro(retiro)});
    }

    public TablaConsulta porEstado(EstadoAlumno estado) {
        Objects.requireNonNull(estado, "estado");
        Map<Integer, MatriculaResumen> matriculasPorAlumno = sistema.matriculas().stream()
                .collect(Collectors.toMap(m -> m.alumno().codigo(), m -> m));
        Object[][] filas = sistema.alumnos().stream()
                .filter(a -> a.estado() == estado)
                .map(a -> new AlumnoEstadoResumen(a,
                        java.util.Optional.ofNullable(matriculasPorAlumno.get(a.codigo()))))
                .map(ConsultasMatricula::filaEstado)
                .toArray(Object[][]::new);
        return new TablaConsulta(COLUMNAS_ESTADO, filas);
    }

    private static Object[] filaEstado(AlumnoEstadoResumen resumen) {
        Object[] datosMatricula = resumen.matricula()
                .map(m -> new Object[]{m.numero(), m.fecha(), m.hora()})
                .orElseGet(() -> new Object[]{"", "", ""});
        Object[] datosCurso = resumen.matricula()
                .map(m -> filaCurso(m.curso()))
                .orElseGet(() -> new Object[]{"", "", "", "", ""});
        return combinar(filaAlumno(resumen.alumno()), datosMatricula, datosCurso);
    }

    public TablaConsulta matriculadosPorCurso() {
        Map<Integer, List<MatriculaResumen>> matriculasPorCurso = sistema.matriculas().stream()
                .collect(Collectors.groupingBy(m -> m.curso().codigo()));
        List<Object[]> filas = new ArrayList<>();

        for (CursoResumen curso : sistema.cursos()) {
            List<MatriculaResumen> matriculados = matriculasPorCurso
                    .getOrDefault(curso.codigo(), List.of()).stream()
                    .filter(m -> m.alumno().estado() == EstadoAlumno.MATRICULADO)
                    .toList();
            if (matriculados.isEmpty()) {
                filas.add(combinar(filaCurso(curso),
                        new Object[]{"", "", ""}, new Object[]{"", "", "", "", "", "", ""}));
            } else {
                matriculados.forEach(m -> filas.add(combinar(filaCurso(curso),
                        new Object[]{m.numero(), m.fecha(), m.hora()}, filaAlumno(m.alumno()))));
            }
        }
        return new TablaConsulta(columnasReporteCurso(), filas.toArray(Object[][]::new));
    }

    private static Object[] filaAlumno(AlumnoResumen alumno) {
        return new Object[]{alumno.codigo(), alumno.nombres(), alumno.apellidos(), alumno.dni(),
            alumno.edad(), alumno.celular(), alumno.estadoTexto()};
    }

    private static Object[] filaCurso(CursoResumen curso) {
        return new Object[]{curso.codigo(), curso.asignatura(), curso.creditos(), curso.ciclo(), curso.horas()};
    }

    private static Object[] filaMatricula(MatriculaResumen matricula) {
        return combinar(new Object[]{matricula.numero()}, filaAlumno(matricula.alumno()),
                filaCurso(matricula.curso()), new Object[]{matricula.fecha(), matricula.hora()});
    }

    private static Object[] filaRetiro(RetiroResumen retiro) {
        return combinar(new Object[]{retiro.numero()}, filaMatricula(retiro.matricula()),
                new Object[]{retiro.fecha(), retiro.hora()});
    }

    private static Object[] combinar(Object[]... partes) {
        int longitud = 0;
        for (Object[] parte : partes) {
            longitud += parte.length;
        }
        Object[] resultado = new Object[longitud];
        int inicio = 0;
        for (Object[] parte : partes) {
            System.arraycopy(parte, 0, resultado, inicio, parte.length);
            inicio += parte.length;
        }
        return resultado;
    }

    private static String[] combinarColumnas(String[]... partes) {
        int longitud = 0;
        for (String[] parte : partes) {
            longitud += parte.length;
        }
        String[] resultado = new String[longitud];
        int inicio = 0;
        for (String[] parte : partes) {
            System.arraycopy(parte, 0, resultado, inicio, parte.length);
            inicio += parte.length;
        }
        return resultado;
    }

    private static String[] columnasMatricula() {
        return combinarColumnas(new String[]{"Matrícula"}, COLUMNAS_ALUMNO, COLUMNAS_CURSO,
                new String[]{"Fecha", "Hora"});
    }

    private static String[] columnasRetiro() {
        return combinarColumnas(new String[]{"Retiro"}, columnasMatricula(), new String[]{"Fecha retiro", "Hora retiro"});
    }

    private static String[] columnasConsultaAlumno() {
        return combinarColumnas(COLUMNAS_ALUMNO, COLUMNAS_DATOS_MATRICULA, COLUMNAS_CURSO);
    }

    private static String[] columnasReporteCurso() {
        return combinarColumnas(COLUMNAS_CURSO, COLUMNAS_DATOS_MATRICULA, COLUMNAS_ALUMNO);
    }
}


