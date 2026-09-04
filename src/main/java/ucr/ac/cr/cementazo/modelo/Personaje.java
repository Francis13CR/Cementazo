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
public class Personaje  extends  Principal{
    
    private  int vidas;
    
    public Personaje(ImageIcon imagen, int x, int y, int vidas) {
        super(imagen, x, y);
        this.vidas=vidas;
    }

 
  

    public int getVidas() {
        return vidas;
    }

    public void setVidas(int vidas) {
        this.vidas = vidas;
    }
    
    
    
}
