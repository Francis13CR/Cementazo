package ucr.ac.cr.cementazo.modelo;

import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;

public class Recursos {

    public static ImageIcon cargar(String nombre) {
        String[] rutas = {
            "./src/main/resources/img/" + nombre,
            "src/main/resources/img/" + nombre
        };
        for (int i = 0; i < rutas.length; i++) {
            File archivo = new File(rutas[i]);
            if (archivo.exists()) {
                return listo(new ImageIcon(archivo.getAbsolutePath()));
            }
        }
        URL url = Recursos.class.getResource("/img/" + nombre);
        if (url != null) {
            return listo(new ImageIcon(url));
        }
        return listo(new ImageIcon(rutas[0]));
    }

    private static ImageIcon listo(ImageIcon icono) {
        if (icono != null && icono.getImage() != null) {
            icono.getImage().getWidth(null);
        }
        return icono;
    }
}
