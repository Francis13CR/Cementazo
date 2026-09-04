package ucr.ac.cr.cementazo.modelo;

import javax.swing.ImageIcon;

public class Perico extends Principal {

    public enum Estado {
        BUSCANDO_DINERO,
        ESCAPANDO_CON_DINERO,
        MUERTO
    }

    private static final double VELOCIDAD_VUELO = 6.5;
    private static final double VELOCIDAD_ESCAPE = 8.0;
    private static final double GRAVEDAD = 0.85;
    private static final double VELOCIDAD_CAIDA_MAX = 18.0;

    private Estado estado;
    private ObjetoCaida objetivo;
    private double xContinua;
    private double yContinua;
    private double velocidadX;
    private double velocidadY;
    private ImageIcon imgVuelo;
    private ImageIcon imgRobando;
    private ImageIcon imgCaida;

    public Perico(ImageIcon imgVuelo, ImageIcon imgRobando, ImageIcon imgCaida,
            int x, int y, ObjetoCaida objetivo) {
        super(imgVuelo, x, y);
        this.imgVuelo = imgVuelo;
        this.imgRobando = imgRobando;
        this.imgCaida = imgCaida;
        this.objetivo = objetivo;
        this.estado = Estado.BUSCANDO_DINERO;
        this.xContinua = x;
        this.yContinua = y;
    }

    public void actualizar() {
        if (estado == Estado.BUSCANDO_DINERO) {
            perseguirObjetivo();
            aplicarMovimiento();
            if (objetivo != null && colisionaCon(objetivo)) {
                robar();
            }
        } else if (estado == Estado.ESCAPANDO_CON_DINERO) {
            velocidadY = -VELOCIDAD_ESCAPE;
            velocidadX *= 0.90;
            aplicarMovimiento();
            arrastrarBotin();
        } else if (estado == Estado.MUERTO) {
            velocidadY = Math.min(VELOCIDAD_CAIDA_MAX, velocidadY + GRAVEDAD);
            velocidadX *= 0.92;
            aplicarMovimiento();
        }
    }

    public void matar() {
        if (estado == Estado.MUERTO) {
            return;
        }
        if (objetivo != null && objetivo.estaRobado()) {
            objetivo.setRobado(false);
        }
        estado = Estado.MUERTO;
        setImagen(imgCaida);
        if (velocidadY < 2.0) {
            velocidadY = 2.0;
        }
    }

    public void perderObjetivo() {
        if (estado != Estado.BUSCANDO_DINERO) {
            return;
        }
        objetivo = null;
        estado = Estado.ESCAPANDO_CON_DINERO;
        setImagen(imgVuelo);
        velocidadY = -VELOCIDAD_ESCAPE;
    }

    public Estado getEstado() {
        return estado;
    }

    public ObjetoCaida getObjetivo() {
        return objetivo;
    }

    public boolean estaVivo() {
        return estado != Estado.MUERTO;
    }

    public boolean debeDesaparecer(int altoArea) {
        if (estado == Estado.MUERTO) {
            return getY() > altoArea;
        }
        if (estado == Estado.ESCAPANDO_CON_DINERO) {
            return getY() + getAlto() < 0;
        }
        return false;
    }

    private void perseguirObjetivo() {
        if (objetivo == null) {
            return;
        }
        double dx = centroObjetivoX() - (xContinua + getAncho() / 2.0);
        double dy = centroObjetivoY() - (yContinua + getAlto() / 2.0);
        double distancia = Math.sqrt(dx * dx + dy * dy);
        if (distancia > 0.001) {
            velocidadX = dx / distancia * VELOCIDAD_VUELO;
            velocidadY = dy / distancia * VELOCIDAD_VUELO;
        }
    }

    private void aplicarMovimiento() {
        xContinua += velocidadX;
        yContinua += velocidadY;
        setX((int) xContinua);
        setY((int) yContinua);
    }

    private void robar() {
        if (objetivo == null) {
            return;
        }
        objetivo.setRobado(true);
        estado = Estado.ESCAPANDO_CON_DINERO;
        setImagen(imgRobando);
        arrastrarBotin();
    }

    private void arrastrarBotin() {
        if (objetivo == null || !objetivo.estaRobado()) {
            return;
        }
        int xBotin = getX() + (getAncho() - objetivo.getAncho()) / 2;
        int yBotin = getY() + getAlto() - 12;
        objetivo.setPosicion(xBotin, yBotin);
    }

    private double centroObjetivoX() {
        return objetivo.getX() + objetivo.getAncho() / 2.0;
    }

    private double centroObjetivoY() {
        return objetivo.getY() + objetivo.getAlto() / 2.0;
    }
}
