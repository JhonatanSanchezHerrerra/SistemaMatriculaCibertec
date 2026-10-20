package pe.cibertec.matricula.servicio;

public final class TablaConsulta {
    private final String[] columnas;
    private final Object[][] filas;

    public TablaConsulta(String[] columnas, Object[][] filas) {
        this.columnas = columnas.clone();
        this.filas = copiarFilas(filas);
    }

    public String[] columnas() { return columnas.clone(); }
    public Object[][] filas() { return copiarFilas(filas); }

    private static Object[][] copiarFilas(Object[][] origen) {
        Object[][] copia = new Object[origen.length][];
        for (int indice = 0; indice < origen.length; indice++) {
            copia[indice] = origen[indice].clone();
        }
        return copia;
    }
}
