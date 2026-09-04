/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucr.ac.cr.cementazo.modelo;

import javax.swing.ImageIcon;

/**
 *
 * @author Admin
 */
public class Cemento extends  Principal{
    public int direc;
    public Cemento(ImageIcon imagen, int x, int y, int direc) {
        super(imagen, x, y);
        this.direc = direc;
    }
    
    public void movimiento(){
        switch (direc) {
        case 0:
            setX(getX() + 20);
            break;
        case 1:
            setX(getX() - 20);
            break;
        case 2:
            setY(getY() + 20);
            break;
        case 3:
            setY(getY() - 20);
            break;
    }
    }
}
