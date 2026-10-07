package pe.cibertec.matricula;

import javax.swing.SwingUtilities;
import pe.cibertec.matricula.ui.VentanaPrincipal;

/** Aqui inicio la aplicacion; esta es la clase principal que se ejecuta en Eclipse. */
public class Principal {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
