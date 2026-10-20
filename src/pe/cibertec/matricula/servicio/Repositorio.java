package pe.cibertec.matricula.servicio;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import pe.cibertec.matricula.modelo.Alumno;
import pe.cibertec.matricula.modelo.Curso;
import pe.cibertec.matricula.modelo.Matricula;
import pe.cibertec.matricula.modelo.Retiro;

public final class Repositorio {
    private static final String ARCHIVO_SNAPSHOT = "sistema.snapshot";
    private static final long MAX_PROFUNDIDAD = 32;
    private static final long MAX_REFERENCIAS = 1_000_000;
    private static final long MAX_BYTES = 128L * 1024 * 1024;
    private static final long MAX_LONGITUD_ARREGLO = 1_000_000;
    private static final Set<Class<?>> CLASES_PERMITIDAS = Set.of(
            EstadoSistema.class,
            Alumno.class,
            Curso.class,
            Matricula.class,
            Retiro.class,
            ArrayList.class,
            String.class);
    private static final ObjectInputFilter FILTRO_PERSISTENCIA = info -> {
        if (info.depth() > MAX_PROFUNDIDAD
                || info.references() > MAX_REFERENCIAS
                || info.streamBytes() > MAX_BYTES
                || info.arrayLength() > MAX_LONGITUD_ARREGLO) {
            return ObjectInputFilter.Status.REJECTED;
        }

        Class<?> tipo = info.serialClass();
        if (tipo == null) {
            return ObjectInputFilter.Status.UNDECIDED;
        }
        if (tipo.isArray()) {
            return tipo == Object[].class
                    ? ObjectInputFilter.Status.ALLOWED
                    : ObjectInputFilter.Status.REJECTED;
        }
        return CLASES_PERMITIDAS.contains(tipo)
                ? ObjectInputFilter.Status.ALLOWED
                : ObjectInputFilter.Status.REJECTED;
    };

    private final Path carpeta;

    public Repositorio() {
        this(Path.of("data"));
    }

    public Repositorio(Path carpeta) {
        this.carpeta = Objects.requireNonNull(carpeta, "carpeta").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.carpeta);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo crear el directorio de datos.", e);
        }
    }

    EstadoSistema cargar() {
        Path snapshot = carpeta.resolve(ARCHIVO_SNAPSHOT);
        if (Files.exists(snapshot)) {
            return leerSnapshot(snapshot);
        }

        EstadoSistema estadoInicial = cargarArchivosAnteriores();
        guardar(estadoInicial);
        return estadoInicial;
    }

    void guardar(EstadoSistema estado) {
        Path temporal = null;
        try {
            temporal = Files.createTempFile(carpeta, ARCHIVO_SNAPSHOT, ".tmp");
            try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(temporal))) {
                salida.writeObject(estado);
            }
            Files.move(temporal, carpeta.resolve(ARCHIVO_SNAPSHOT),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            temporal = null;
        } catch (IOException e) {
            if (temporal != null) {
                try {
                    Files.deleteIfExists(temporal);
                } catch (IOException errorLimpieza) {
                    e.addSuppressed(errorLimpieza);
                }
            }
            throw new PersistenciaException("No se pudo guardar el snapshot del sistema.", e);
        }
    }

    private EstadoSistema leerSnapshot(Path archivo) {
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(archivo))) {
            entrada.setObjectInputFilter(FILTRO_PERSISTENCIA);
            Object contenido = entrada.readObject();
            if (!(contenido instanceof EstadoSistema estado)) {
                throw new PersistenciaException("El snapshot del sistema tiene un formato no válido.");
            }
            return estado;
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenciaException("No se pudo leer el snapshot del sistema.", e);
        }
    }

    private EstadoSistema cargarArchivosAnteriores() {
        return new EstadoSistema(
                leerArchivoAnterior("alumnos.dat", Alumno.class),
                leerArchivoAnterior("cursos.dat", Curso.class),
                leerArchivoAnterior("matriculas.dat", Matricula.class),
                leerArchivoAnterior("retiros.dat", Retiro.class));
    }

    private <T> ArrayList<T> leerArchivoAnterior(String nombre, Class<T> tipo) {
        Path archivo = carpeta.resolve(nombre);
        if (Files.notExists(archivo)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(archivo))) {
            entrada.setObjectInputFilter(FILTRO_PERSISTENCIA);
            Object contenido = entrada.readObject();
            if (!(contenido instanceof List<?> lista)) {
                throw new PersistenciaException("El archivo " + nombre + " no contiene una lista.");
            }
            ArrayList<T> resultado = new ArrayList<>(lista.size());
            for (Object elemento : lista) {
                if (!tipo.isInstance(elemento)) {
                    throw new PersistenciaException("Tipo de registro no válido en " + nombre + ".");
                }
                resultado.add(tipo.cast(elemento));
            }
            return resultado;
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenciaException("No se pudo migrar el archivo " + nombre + ".", e);
        }
    }
}
