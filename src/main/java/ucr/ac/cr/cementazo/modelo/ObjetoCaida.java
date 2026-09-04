package ucr.ac.cr.cementazo.modelo;

import javax.swing.ImageIcon;

public class ObjetoCaida extends Principal {

    private TipoCaida tipo;
    private double yContinua;
    private double velocidadY;
    private boolean robado;

    public ObjetoCaida(ImageIcon imagen, int x, int y, TipoCaida tipo, double velocidadY) {
        super(imagen, x, y);
        this.tipo = tipo;
        this.yContinua = y;
        this.velocidadY = velocidadY;
        this.robado = false;
    }

    public TipoCaida getTipo() {
        return tipo;
    }

    public double getVelocidadY() {
        return velocidadY;
    }

    public void setVelocidadY(double velocidadY) {
        this.velocidadY = velocidadY;
    }

    public boolean estaRobado() {
        return robado;
    }

    public void setRobado(boolean robado) {
        this.robado = robado;
    }

    public void setPosicion(int x, int y) {
        setX(x);
        setY(y);
        this.yContinua = y;
    }

    public void caer() {
        yContinua += velocidadY;
        setY((int) yContinua);
    }
}
