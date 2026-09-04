package ucr.ac.cr.cementazo.modelo;

import javax.swing.ImageIcon;

public class ObjetoCaida extends Principal {

    private TipoCaida tipo;
    private double yContinua;

    public ObjetoCaida(ImageIcon imagen, int x, int y, TipoCaida tipo) {
        super(imagen, x, y);
        this.tipo = tipo;
        this.yContinua = y;
    }

    public TipoCaida getTipo() {
        return tipo;
    }

    public void caer(double velocidad) {
        yContinua += velocidad;
        setY((int) yContinua);
    }
}
