package pe.cibertec.matricula.servicio;

import java.io.*;
import java.util.ArrayList;

/** Aqui centralizo la lectura y grabacion de las listas para no repetir codigo en la interfaz. */
public class Repositorio {
    private final File carpeta = new File("data");
    public Repositorio() { if (!carpeta.exists()) carpeta.mkdirs(); }
    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> leer(String nombre) {
        File archivo = new File(carpeta, nombre);
        if (!archivo.exists()) return new ArrayList<>();
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))) { return (ArrayList<T>) entrada.readObject(); }
        catch (IOException | ClassNotFoundException e) { return new ArrayList<>(); }
    }
    public void grabar(String nombre, ArrayList<?> datos) {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(new File(carpeta, nombre)))) { salida.writeObject(datos); }
        catch (IOException e) { throw new IllegalStateException("No se pudo grabar el archivo: " + nombre, e); }
    }
}
