package pe.cibertec.matricula.servicio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import pe.cibertec.matricula.modelo.Alumno;
import pe.cibertec.matricula.modelo.Curso;
import pe.cibertec.matricula.modelo.EstadoAlumno;
import pe.cibertec.matricula.modelo.Matricula;
import pe.cibertec.matricula.modelo.Retiro;

public class SistemaMatricula implements ISistemaMatricula {
    private static final int PRIMER_CODIGO_ALUMNO = 202010001;
    private static final int PRIMER_NUMERO_MATRICULA = 100001;
    private static final int PRIMER_NUMERO_RETIRO = 200001;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Repositorio repositorio;
    private EstadoSistema estado;

    public SistemaMatricula() {
        this(new Repositorio());
    }

    public SistemaMatricula(Repositorio repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "repositorio");
        estado = repositorio.cargar();
        validarIntegridad(estado);
    }

    public List<AlumnoResumen> alumnos() {
        return estado.alumnos().stream().map(SistemaMatricula::resumen).toList();
    }

    public List<CursoResumen> cursos() {
        return estado.cursos().stream().sorted()
                .map(SistemaMatricula::resumen).toList();
    }

    public List<MatriculaResumen> matriculas() {
        return estado.matriculas().stream().map(m -> resumen(estado, m)).toList();
    }

    public List<RetiroResumen> retiros() {
        return estado.retiros().stream().map(r -> resumen(estado, r)).toList();
    }

    public Optional<AlumnoResumen> alumno(int codigo) {
        return buscarAlumno(estado, codigo).map(SistemaMatricula::resumen);
    }

    public Optional<CursoResumen> curso(int codigo) {
        return buscarCurso(estado, codigo).map(SistemaMatricula::resumen);
    }

    public Optional<MatriculaResumen> matricula(int numero) {
        return buscarMatricula(estado, numero).map(m -> resumen(estado, m));
    }

    public Optional<RetiroResumen> retiro(int numero) {
        return buscarRetiro(estado, numero).map(r -> resumen(estado, r));
    }

    public void agregarAlumno(String nombres, String apellidos, String dni, int edad, int celular) {
        transaccion(candidato -> {
            String dniNormalizado = textoObligatorio(dni, "DNI");
            validarDniUnico(candidato, dniNormalizado);
            candidato.alumnos().add(new Alumno(siguienteCodigoAlumno(candidato), nombres,
                    apellidos, dniNormalizado, edad, celular));
        });
    }

    public void editarAlumno(int codigo, String nombres, String apellidos, int edad, int celular) {
        transaccion(candidato -> requerirAlumno(candidato, codigo)
                .actualizar(nombres, apellidos, edad, celular));
    }

    public void eliminarAlumno(int codigo) {
        transaccion(candidato -> {
            Alumno alumno = requerirAlumno(candidato, codigo);
            if (alumno.getEstado() != EstadoAlumno.REGISTRADO) {
                throw new IllegalArgumentException("Solo se puede eliminar un alumno registrado.");
            }
            candidato.alumnos().remove(alumno);
        });
    }

    public void agregarCurso(int codigo, String asignatura, int ciclo, int creditos, int horas) {
        transaccion(candidato -> {
            if (buscarCurso(candidato, codigo).isPresent()) {
                throw new IllegalArgumentException("El código de curso ya existe.");
            }
            candidato.cursos().add(new Curso(codigo, asignatura, ciclo, creditos, horas));
        });
    }

    public void editarCurso(int codigo, String asignatura, int ciclo, int creditos, int horas) {
        transaccion(candidato -> requerirCurso(candidato, codigo)
                .actualizar(asignatura, ciclo, creditos, horas));
    }

    public void eliminarCurso(int codigo) {
        transaccion(candidato -> {
            if (candidato.matriculas().stream().anyMatch(m -> m.getCodCurso() == codigo)) {
                throw new IllegalArgumentException("No se puede eliminar un curso con matrículas.");
            }
            candidato.cursos().remove(requerirCurso(candidato, codigo));
        });
    }

    public void matricular(int codigoAlumno, int codigoCurso) {
        transaccion(candidato -> {
            Alumno alumno = requerirAlumno(candidato, codigoAlumno);
            requerirCurso(candidato, codigoCurso);
            boolean tieneMatricula = candidato.matriculas().stream()
                    .anyMatch(m -> m.getCodAlumno() == codigoAlumno);
            if (alumno.getEstado() != EstadoAlumno.REGISTRADO || tieneMatricula) {
                throw new IllegalArgumentException("El alumno ya tiene matrícula o no está disponible.");
            }

            LocalDateTime ahora = LocalDateTime.now();
            candidato.matriculas().add(new Matricula(siguienteNumeroMatricula(candidato),
                    codigoAlumno, codigoCurso, ahora.format(FORMATO_FECHA), ahora.format(FORMATO_HORA)));
            alumno.marcarMatriculado();
        });
    }

    public void cambiarCurso(int numeroMatricula, int codigoCurso) {
        transaccion(candidato -> {
            requerirCurso(candidato, codigoCurso);
            Matricula matricula = requerirMatricula(candidato, numeroMatricula);
            Alumno alumno = requerirAlumno(candidato, matricula.getCodAlumno());
            if (alumno.getEstado() == EstadoAlumno.RETIRADO) {
                throw new IllegalArgumentException("La matrícula retirada no se cambia desde este módulo.");
            }
            matricula.cambiarCurso(codigoCurso);
        });
    }

    public void cancelarMatricula(int numeroMatricula) {
        transaccion(candidato -> {
            Matricula matricula = requerirMatricula(candidato, numeroMatricula);
            Alumno alumno = requerirAlumno(candidato, matricula.getCodAlumno());
            if (alumno.getEstado() == EstadoAlumno.RETIRADO) {
                throw new IllegalArgumentException("No se puede cancelar una matrícula retirada.");
            }
            alumno.marcarRegistrado();
            candidato.matriculas().remove(matricula);
        });
    }

    public void retirar(int numeroMatricula) {
        transaccion(candidato -> {
            Matricula matricula = requerirMatricula(candidato, numeroMatricula);
            Alumno alumno = requerirAlumno(candidato, matricula.getCodAlumno());
            if (alumno.getEstado() != EstadoAlumno.MATRICULADO) {
                throw new IllegalArgumentException("El alumno debe estar matriculado.");
            }

            LocalDateTime ahora = LocalDateTime.now();
            candidato.retiros().add(new Retiro(siguienteNumeroRetiro(candidato), numeroMatricula,
                    ahora.format(FORMATO_FECHA), ahora.format(FORMATO_HORA)));
            alumno.marcarRetirado();
        });
    }

    public void cambiarCursoRetirado(int numeroRetiro, int codigoCurso) {
        transaccion(candidato -> {
            Retiro retiro = requerirRetiro(candidato, numeroRetiro);
            Matricula matricula = requerirMatricula(candidato, retiro.getNumMatricula());
            Alumno alumno = requerirAlumno(candidato, matricula.getCodAlumno());
            if (alumno.getEstado() != EstadoAlumno.RETIRADO) {
                throw new IllegalArgumentException("El alumno no está retirado.");
            }
            requerirCurso(candidato, codigoCurso);
            matricula.cambiarCurso(codigoCurso);
        });
    }

    public void cancelarRetiro(int numeroRetiro) {
        transaccion(candidato -> {
            Retiro retiro = requerirRetiro(candidato, numeroRetiro);
            Matricula matricula = requerirMatricula(candidato, retiro.getNumMatricula());
            Alumno alumno = requerirAlumno(candidato, matricula.getCodAlumno());
            if (alumno.getEstado() != EstadoAlumno.RETIRADO) {
                throw new IllegalArgumentException("El alumno no está retirado.");
            }
            alumno.cancelarRetiro();
            candidato.retiros().remove(retiro);
        });
    }

    private void transaccion(Consumer<EstadoSistema> operacion) {
        EstadoSistema candidato = estado.copiar();
        operacion.accept(candidato);
        validarIntegridad(candidato);
        repositorio.guardar(candidato);
        estado = candidato;
    }

    private static void validarIntegridad(EstadoSistema estado) {
        validarUnicos(estado.alumnos().stream().map(Alumno::getCodAlumno).toList(), "códigos de alumno");
        validarUnicos(estado.cursos().stream().map(Curso::getCodCurso).toList(), "códigos de curso");
        validarUnicos(estado.matriculas().stream().map(Matricula::getNumMatricula).toList(), "matrículas");
        validarUnicos(estado.retiros().stream().map(Retiro::getNumRetiro).toList(), "retiros");

        for (Matricula matricula : estado.matriculas()) {
            if (buscarAlumno(estado, matricula.getCodAlumno()).isEmpty()
                    || buscarCurso(estado, matricula.getCodCurso()).isEmpty()) {
                throw new PersistenciaException("Existe una matrícula con referencias inexistentes.");
            }
        }
        for (Retiro retiro : estado.retiros()) {
            if (buscarMatricula(estado, retiro.getNumMatricula()).isEmpty()) {
                throw new PersistenciaException("Existe un retiro sin matrícula asociada.");
            }
        }
        for (Alumno alumno : estado.alumnos()) {
            long cantidadMatriculas = estado.matriculas().stream()
                    .filter(m -> m.getCodAlumno() == alumno.getCodAlumno()).count();
            long cantidadRetiros = estado.retiros().stream()
                    .filter(r -> estado.matriculas().stream().anyMatch(m ->
                            m.getNumMatricula() == r.getNumMatricula()
                                    && m.getCodAlumno() == alumno.getCodAlumno()))
                    .count();
            boolean estadoInvalido = switch (alumno.getEstado()) {
                case REGISTRADO -> cantidadMatriculas != 0;
                case MATRICULADO -> cantidadMatriculas != 1 || cantidadRetiros != 0;
                case RETIRADO -> cantidadMatriculas != 1 || cantidadRetiros != 1;
            };
            if (estadoInvalido) {
                throw new PersistenciaException("El estado del alumno no coincide con sus matrículas.");
            }
        }
    }

    private static void validarUnicos(List<Integer> valores, String nombre) {
        if (new HashSet<>(valores).size() != valores.size()) {
            throw new PersistenciaException("Hay " + nombre + " duplicados en los datos.");
        }
    }

    private static void validarDniUnico(EstadoSistema estado, String dni) {
        boolean duplicado = estado.alumnos().stream()
                .anyMatch(alumno -> alumno.getDni().equals(dni));
        if (duplicado) {
            throw new IllegalArgumentException("El DNI ya está registrado.");
        }
    }

    private static int siguienteCodigoAlumno(EstadoSistema estado) {
        return estado.alumnos().stream().mapToInt(Alumno::getCodAlumno).max()
                .orElse(PRIMER_CODIGO_ALUMNO - 1) + 1;
    }

    private static int siguienteNumeroMatricula(EstadoSistema estado) {
        return estado.matriculas().stream().mapToInt(Matricula::getNumMatricula).max()
                .orElse(PRIMER_NUMERO_MATRICULA - 1) + 1;
    }

    private static int siguienteNumeroRetiro(EstadoSistema estado) {
        return estado.retiros().stream().mapToInt(Retiro::getNumRetiro).max()
                .orElse(PRIMER_NUMERO_RETIRO - 1) + 1;
    }

    private static Optional<Alumno> buscarAlumno(EstadoSistema estado, int codigo) {
        return estado.alumnos().stream().filter(a -> a.getCodAlumno() == codigo).findFirst();
    }

    private static Optional<Curso> buscarCurso(EstadoSistema estado, int codigo) {
        return estado.cursos().stream().filter(c -> c.getCodCurso() == codigo).findFirst();
    }

    private static Optional<Matricula> buscarMatricula(EstadoSistema estado, int numero) {
        return estado.matriculas().stream().filter(m -> m.getNumMatricula() == numero).findFirst();
    }

    private static Optional<Retiro> buscarRetiro(EstadoSistema estado, int numero) {
        return estado.retiros().stream().filter(r -> r.getNumRetiro() == numero).findFirst();
    }

    private static Alumno requerirAlumno(EstadoSistema estado, int codigo) {
        return buscarAlumno(estado, codigo).orElseThrow(() ->
                new IllegalArgumentException("Alumno no encontrado."));
    }

    private static Curso requerirCurso(EstadoSistema estado, int codigo) {
        return buscarCurso(estado, codigo).orElseThrow(() ->
                new IllegalArgumentException("Curso no encontrado."));
    }

    private static Matricula requerirMatricula(EstadoSistema estado, int numero) {
        return buscarMatricula(estado, numero).orElseThrow(() ->
                new IllegalArgumentException("Matrícula no encontrada."));
    }

    private static Retiro requerirRetiro(EstadoSistema estado, int numero) {
        return buscarRetiro(estado, numero).orElseThrow(() ->
                new IllegalArgumentException("Retiro no encontrado."));
    }

    private static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        return valor.trim();
    }

    private static AlumnoResumen resumen(Alumno alumno) {
        return new AlumnoResumen(alumno.getCodAlumno(), alumno.getNombres(), alumno.getApellidos(),
                alumno.getDni(), alumno.getEdad(), alumno.getCelular(), alumno.getEstado());
    }

    private static CursoResumen resumen(Curso curso) {
        return new CursoResumen(curso.getCodCurso(), curso.getAsignatura(), curso.getCiclo(),
                curso.getCreditos(), curso.getHoras());
    }

    private static MatriculaResumen resumen(EstadoSistema estado, Matricula matricula) {
        AlumnoResumen alumno = resumen(requerirAlumno(estado, matricula.getCodAlumno()));
        CursoResumen curso = resumen(requerirCurso(estado, matricula.getCodCurso()));
        return new MatriculaResumen(matricula.getNumMatricula(), alumno, curso,
                matricula.getFecha(), matricula.getHora());
    }

    private static RetiroResumen resumen(EstadoSistema estado, Retiro retiro) {
        Matricula matricula = requerirMatricula(estado, retiro.getNumMatricula());
        return new RetiroResumen(retiro.getNumRetiro(), resumen(estado, matricula),
                retiro.getFecha(), retiro.getHora());
    }
}


