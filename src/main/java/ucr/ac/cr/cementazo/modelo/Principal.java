/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucr.ac.cr.cementazo.modelo;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author Admin
 */
public class Principal {
    private ImageIcon imagen;
    private int x;
    private int y;

    public Principal(ImageIcon imagen, int x, int y) {
        this.imagen = imagen;
        this.x = x;
        this.y = y;
    }

    public ImageIcon getImagen() {
        return imagen;
    }

    public void setImagen(ImageIcon imagen) {
        this.imagen = imagen;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    
    public int getAncho() {
        if (imagen == null || imagen.getIconWidth() <= 0) {
            return 50;
        }
        return imagen.getIconWidth();
    }

    public int getAlto() {
        if (imagen == null || imagen.getIconHeight() <= 0) {
            return 50;
        }
        return imagen.getIconHeight();
    }

    public boolean tocaLaTablaDe(Principal personaje) {
        return tocaLaTablaDe(personaje, 8);
    }

    public boolean tocaLaTablaDe(Principal personaje, int holgura) {
        int tablaX = personaje.getX() + 18;
        int tablaY = personaje.getY();
        int tablaAncho = Math.max(40, personaje.getAncho() - 36);
        int tablaAlto = 36 + Math.max(0, holgura);
        return getX() < tablaX + tablaAncho
                && getX() + getAncho() > tablaX
                && getY() < tablaY + tablaAlto
                && getY() + getAlto() > tablaY;
    }

    public void show(Graphics g){
        if (imagen != null) {
            imagen.paintIcon(null, g, getX(), getY());
        }
    }
   
    
}
