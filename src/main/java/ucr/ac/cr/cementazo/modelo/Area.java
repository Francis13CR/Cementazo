package ucr.ac.cr.cementazo.modelo;

import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;

public class Area {

    public static final int ANCHO = 1005;
    public static final int ALTO = 687;
    public static final int PASO = 16;

    private ArrayList<ObjetoCaida> caidas;
    private Personaje bolanos;
    private int puntaje;
    private boolean terminado;
    private int tiempoMs;
    private int invulnerableMs;
    private int esperaSpawnMs;
    private Random azar;
    private ImageIcon imgCemento;
    private ImageIcon imgDinero;
    private ImageIcon imgEsposas;
    private ImageIcon imgDerecha;
    private ImageIcon imgIzquierda;

    public Area() {
        azar = new Random();
        imgCemento = Recursos.cargar("cemento.png");
        imgDinero = Recursos.cargar("dinero.png");
        imgEsposas = Recursos.cargar("esposas.png");
        imgDerecha = Recursos.cargar("pDerecha.png");
        imgIzquierda = Recursos.cargar("pIzquierda.png");
        iniciarPartida();
    }

    public void iniciarPartida() {
        caidas = new ArrayList<ObjetoCaida>();
        bolanos = new Personaje(imgDerecha, 400, 540, 3);
        puntaje = 0;
        terminado = false;
        tiempoMs = 0;
        invulnerableMs = 0;
        esperaSpawnMs = 400;
    }

    public Personaje getBolanos() {
        return bolanos;
    }

    public ArrayList<ObjetoCaida> getCaidas() {
        return new ArrayList<ObjetoCaida>(caidas);
    }

    public int getPuntaje() {
        return puntaje;
    }

    public boolean estaTerminado() {
        return terminado;
    }

    public int getVelocidadCaida() {
        return (int) Math.round(getVelocidadReal());
    }

    public double getVelocidadReal() {
        return Math.min(8.4, 2.8 + (tiempoMs / 8500.0));
    }

    public void moverIzquierda() {
        if (terminado) {
            return;
        }
        int nuevoX = bolanos.getX() - 24;
        if (nuevoX < 0) {
            nuevoX = 0;
        }
        bolanos.setX(nuevoX);
        bolanos.setImagen(imgIzquierda);
    }

    public void moverDerecha() {
        if (terminado) {
            return;
        }
        int limite = ANCHO - bolanos.getAncho();
        int nuevoX = bolanos.getX() + 24;
        if (nuevoX > limite) {
            nuevoX = limite;
        }
        bolanos.setX(nuevoX);
        bolanos.setImagen(imgDerecha);
    }

    public void actualizar() {
        if (terminado) {
            return;
        }
        int segundoAnterior = tiempoMs / 1000;
        tiempoMs += PASO;
        if (invulnerableMs > 0) {
            invulnerableMs -= PASO;
        }
        if (tiempoMs / 1000 > segundoAnterior) {
            puntaje += 1;
        }

        double velocidad = getVelocidadReal();
        int holgura = (int) Math.ceil(velocidad) + 8;
        ArrayList<ObjetoCaida> fuera = new ArrayList<ObjetoCaida>();
        for (int i = 0; i < caidas.size(); i++) {
            ObjetoCaida objeto = caidas.get(i);
            objeto.caer(velocidad);
            if (objeto.getY() > ALTO) {
                fuera.add(objeto);
                if (objeto.getTipo() == TipoCaida.CEMENTO) {
                    perderVida();
                }
            } else if (objeto.tocaLaTablaDe(bolanos, holgura)) {
                fuera.add(objeto);
                atrapar(objeto);
            }
        }
        caidas.removeAll(fuera);

        esperaSpawnMs -= PASO;
        if (esperaSpawnMs <= 0 && caidas.size() < 7) {
            soltarObjeto();
            esperaSpawnMs = intervaloSpawn();
        }
    }

    private int intervaloSpawn() {
        return (int) Math.max(420, 1200 - tiempoMs / 45.0);
    }

    private void soltarObjeto() {
        int suerte = azar.nextInt(100);
        TipoCaida tipo = TipoCaida.CEMENTO;
        ImageIcon imagen = imgCemento;
        if (tiempoMs > 4000 && suerte < 10) {
            tipo = TipoCaida.DINERO;
            imagen = imgDinero;
        } else if (tiempoMs > 4000 && suerte < 18) {
            tipo = TipoCaida.ESPOSAS;
            imagen = imgEsposas;
        }
        int anchoObjeto = 50;
        if (imagen != null && imagen.getIconWidth() > 0) {
            anchoObjeto = imagen.getIconWidth();
        }
        int x = azar.nextInt(Math.max(1, ANCHO - anchoObjeto - 40)) + 20;
        caidas.add(new ObjetoCaida(imagen, x, -imagenAlto(imagen), tipo));
    }

    private int imagenAlto(ImageIcon imagen) {
        if (imagen == null || imagen.getIconHeight() <= 0) {
            return 40;
        }
        return imagen.getIconHeight();
    }

    private void atrapar(ObjetoCaida objeto) {
        if (objeto.getTipo() == TipoCaida.CEMENTO) {
            puntaje += 15;
        } else if (objeto.getTipo() == TipoCaida.DINERO) {
            if (puntaje <= 0) {
                puntaje = 20;
            } else {
                puntaje = puntaje * 2;
            }
        } else if (objeto.getTipo() == TipoCaida.ESPOSAS) {
            puntaje = puntaje / 2;
        }
    }

    private void perderVida() {
        if (invulnerableMs > 0 || terminado) {
            return;
        }
        bolanos.setVidas(bolanos.getVidas() - 1);
        invulnerableMs = 1000;
        if (bolanos.getVidas() <= 0) {
            bolanos.setVidas(0);
            terminado = true;
        }
    }
}
