package ucr.ac.cr.cementazo.modelo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Recursos {

    public static final String FONDO = "bg_costa_rica.png";
    public static final String[] FONDOS = {
        "bg_costa_rica.png",
        "bg_heredia.png",
        "bg_cartago.png",
        "bg_limon.png",
        "bg_puntarenas.png",
        "bg_guanacaste.png",
        "bg_alajuela.png"
    };
    public static final String[] NOMBRES_FONDO = {
        "Costa Rica",
        "Heredia",
        "Cartago",
        "Limón",
        "Puntarenas",
        "Guanacaste",
        "Alajuela"
    };
    public static final String PERSONAJE_DER = "sprite_personaje_derecha.png";
    public static final String PERSONAJE_IZQ = "sprite_personaje_izquierda.png";
    public static final String SACO = "item_saco_cemento.png";
    public static final String BILLETES = "item_billetes.png";
    public static final String GRILLETES = "item_grilletes.png";
    public static final String PERICO_VUELO = "perico_vuelo.png";
    public static final String PERICO_ROBANDO = "perico_robando.png";
    public static final String PERICO_CAIDA = "perico_caida.png";
    public static final String MUSICA_FONDO = "/music/tema_fondo.wav";

    public static final Color COLOR_FONDO = new Color(18, 36, 72);
    public static final Color COLOR_PERSONAJE = new Color(20, 32, 70);
    public static final Color COLOR_SACO = new Color(196, 168, 120);
    public static final Color COLOR_BILLETES = new Color(46, 140, 72);
    public static final Color COLOR_GRILLETES = new Color(160, 164, 168);
    public static final Color COLOR_PERICO = new Color(64, 168, 78);

    public static ImageIcon cargar(String nombre) {
        return cargarOColor(nombre, new Color(80, 80, 80), 50, 50);
    }

    public static ImageIcon cargarOColor(String nombre, Color color, int ancho, int alto) {
        ImageIcon icono = buscar(nombre);
        if (esValida(icono)) {
            if (ancho > 0 && alto > 0) {
                return escalar(icono, ancho, alto);
            }
            return icono;
        }
        return colorSolido(color, ancho, alto);
    }

    public static ImageIcon voltearHorizontal(ImageIcon icono) {
        if (!esValida(icono)) {
            return icono;
        }
        int w = icono.getIconWidth();
        int h = icono.getIconHeight();
        BufferedImage destino = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = destino.createGraphics();
        g.drawImage(icono.getImage(), w, 0, -w, h, null);
        g.dispose();
        return new ImageIcon(destino);
    }

    public static ImageIcon escalar(ImageIcon icono, int ancho, int alto) {
        int w = ancho < 1 ? 50 : ancho;
        int h = alto < 1 ? 50 : alto;
        BufferedImage destino = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = destino.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(icono.getImage(), 0, 0, w, h, null);
        g.dispose();
        return new ImageIcon(destino);
    }

    public static ImageIcon buscar(String nombre) {
        String[] rutas = {
            "./src/main/resources/img/" + nombre,
            "src/main/resources/img/" + nombre
        };
        for (int i = 0; i < rutas.length; i++) {
            File archivo = new File(rutas[i]);
            ImageIcon desdeDisco = desdeArchivo(archivo);
            if (desdeDisco != null) {
                return desdeDisco;
            }
        }
        URL url = Recursos.class.getResource("/img/" + nombre);
        if (url != null) {
            try {
                BufferedImage imagen = ImageIO.read(url);
                if (imagenUtil(imagen)) {
                    return new ImageIcon(imagen);
                }
            } catch (IOException ignorada) {
                return null;
            }
        }
        return null;
    }

    public static boolean esValida(ImageIcon icono) {
        return icono != null
                && icono.getImage() != null
                && icono.getIconWidth() > 0
                && icono.getIconHeight() > 0;
    }

    public static ImageIcon colorSolido(Color color, int ancho, int alto) {
        int w = ancho < 1 ? 50 : ancho;
        int h = alto < 1 ? 50 : alto;
        Color relleno = color == null ? new Color(80, 80, 80) : color;
        BufferedImage lienzo = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = lienzo.createGraphics();
        g.setColor(relleno);
        g.fillRect(0, 0, w, h);
        g.dispose();
        return new ImageIcon(lienzo);
    }

    private static ImageIcon desdeArchivo(File archivo) {
        if (archivo == null || !archivo.isFile()) {
            return null;
        }
        try {
            BufferedImage imagen = ImageIO.read(archivo);
            if (imagenUtil(imagen)) {
                return new ImageIcon(imagen);
            }
        } catch (IOException ignorada) {
            return null;
        }
        return null;
    }

    private static boolean imagenUtil(BufferedImage imagen) {
        return imagen != null && imagen.getWidth() > 0 && imagen.getHeight() > 0;
    }
}
