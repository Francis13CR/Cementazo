package ucr.ac.cr.cementazo.modelo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.ImageIcon;

public class Area {

    public static final int ANCHO = 1005;
    public static final int ALTO = 687;
    public static final int FPS = 60;
    public static final double PASO = 1000.0 / FPS;

    private ArrayList<ObjetoCaida> caidas;
    private ArrayList<Perico> pericos;
    private Personaje jugador;
    private int puntaje;
    private boolean terminado;
    private double tiempoMs;
    private double invulnerableMs;
    private double esperaSpawnMs;
    private Random azar;
    private ImageIcon imgCemento;
    private ImageIcon imgDinero;
    private ImageIcon imgEsposas;
    private ImageIcon imgDerecha;
    private ImageIcon imgIzquierda;
    private ImageIcon imgPericoVuelo;
    private ImageIcon imgPericoRobando;
    private ImageIcon imgPericoCaida;
    private int indiceFondo;
    private String nombreFondo;

    public Area() {
        azar = new Random();
        imgCemento = Recursos.cargarOColor(Recursos.SACO, Recursos.COLOR_SACO, 70, 97);
        imgDinero = Recursos.cargarOColor(Recursos.BILLETES, Recursos.COLOR_BILLETES, 56, 40);
        imgEsposas = Recursos.cargarOColor(Recursos.GRILLETES, Recursos.COLOR_GRILLETES, 64, 40);
        imgPericoVuelo = Recursos.cargarOColor(Recursos.PERICO_VUELO, Recursos.COLOR_PERICO, 78, 62);
        imgPericoRobando = Recursos.cargarOColor(Recursos.PERICO_ROBANDO, Recursos.COLOR_PERICO, 78, 62);
        imgPericoCaida = Recursos.cargarOColor(Recursos.PERICO_CAIDA, Recursos.COLOR_PERICO, 78, 62);
        imgDerecha = Recursos.cargarOColor(Recursos.PERSONAJE_DER, Recursos.COLOR_PERSONAJE, 0, 0);
        if (Recursos.esValida(imgDerecha) && imgDerecha.getIconWidth() > 220) {
            int nuevoAncho = 198;
            int nuevoAlto = Math.max(1, imgDerecha.getIconHeight() * nuevoAncho / imgDerecha.getIconWidth());
            imgDerecha = Recursos.escalar(imgDerecha, nuevoAncho, nuevoAlto);
        }
        imgIzquierda = Recursos.voltearHorizontal(imgDerecha);
        iniciarPartida();
    }

    public void iniciarPartida() {
        caidas = new ArrayList<ObjetoCaida>();
        pericos = new ArrayList<Perico>();
        int altoPers = 122;
        if (imgDerecha != null && imgDerecha.getIconHeight() > 0) {
            altoPers = imgDerecha.getIconHeight();
        }
        jugador = new Personaje(imgDerecha, 400, ALTO - altoPers - 12, 3);
        puntaje = 0;
        terminado = false;
        tiempoMs = 0;
        invulnerableMs = 0;
        esperaSpawnMs = 400;
        indiceFondo = 0;
        nombreFondo = "San José";
    }

    public String getNombreFondo() {
        return nombreFondo;
    }

    public String getArchivoFondo() {
        return Recursos.FONDO;
    }

    public Personaje getJugador() {
        return jugador;
    }

    public ArrayList<ObjetoCaida> getCaidas() {
        return new ArrayList<ObjetoCaida>(caidas);
    }

    public ArrayList<Perico> getPericos() {
        return new ArrayList<Perico>(pericos);
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
        int nuevoX = jugador.getX() - 24;
        if (nuevoX < 0) {
            nuevoX = 0;
        }
        jugador.setX(nuevoX);
        jugador.setImagen(imgIzquierda);
    }

    public void moverDerecha() {
        if (terminado) {
            return;
        }
        int limite = ANCHO - jugador.getAncho();
        int nuevoX = jugador.getX() + 24;
        if (nuevoX > limite) {
            nuevoX = limite;
        }
        jugador.setX(nuevoX);
        jugador.setImagen(imgDerecha);
    }

    public void actualizar() {
        if (terminado) {
            return;
        }
        int segundoAnterior = (int) (tiempoMs / 1000);
        tiempoMs += PASO;
        if (invulnerableMs > 0) {
            invulnerableMs -= PASO;
        }
        if ((int) (tiempoMs / 1000) > segundoAnterior) {
            puntaje += 1;
        }

        Iterator<ObjetoCaida> recorrido = caidas.iterator();
        while (recorrido.hasNext()) {
            ObjetoCaida objeto = recorrido.next();
            if (objeto.estaRobado()) {
                continue;
            }
            objeto.caer();
            int holgura = (int) Math.ceil(objeto.getVelocidadY()) + 8;
            if (objeto.getY() > ALTO) {
                recorrido.remove();
                if (objeto.getTipo() == TipoCaida.CEMENTO) {
                    perderVida();
                }
            } else if (objeto.tocaLaTablaDe(jugador, holgura)) {
                recorrido.remove();
                atrapar(objeto);
            }
        }

        actualizarPericos();

        esperaSpawnMs -= PASO;
        if (esperaSpawnMs <= 0 && caidas.size() < 7) {
            soltarObjeto();
            esperaSpawnMs = intervaloSpawn();
        }
    }

    private double intervaloSpawn() {
        return Math.max(420, 1200 - tiempoMs / 45.0);
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
        ObjetoCaida creado = new ObjetoCaida(imagen, x, -imagenAlto(imagen), tipo, getVelocidadReal());
        caidas.add(creado);
        if (tipo == TipoCaida.DINERO && azar.nextDouble() > 0.5) {
            int xPerico = azar.nextInt(Math.max(1, ANCHO - 120)) + 60;
            pericos.add(new Perico(imgPericoVuelo, imgPericoRobando, imgPericoCaida,
                    xPerico, -50, creado));
        }
    }

    private void actualizarPericos() {
        Iterator<Perico> recorrido = pericos.iterator();
        while (recorrido.hasNext()) {
            Perico perico = recorrido.next();
            if (perico.estaVivo() && perico.getEstado() == Perico.Estado.BUSCANDO_DINERO) {
                if (perico.getObjetivo() == null || !caidas.contains(perico.getObjetivo())) {
                    perico.perderObjetivo();
                }
            }
            perico.actualizar();
            if (perico.estaVivo()) {
                for (int i = 0; i < caidas.size(); i++) {
                    ObjetoCaida objeto = caidas.get(i);
                    if (objeto.getTipo() == TipoCaida.CEMENTO && perico.colisionaCon(objeto)) {
                        perico.matar();
                        break;
                    }
                }
            }
            if (perico.debeDesaparecer(ALTO)) {
                if (perico.getEstado() == Perico.Estado.ESCAPANDO_CON_DINERO
                        && perico.getObjetivo() != null
                        && perico.getObjetivo().estaRobado()) {
                    caidas.remove(perico.getObjetivo());
                }
                recorrido.remove();
            }
        }
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
        jugador.setVidas(jugador.getVidas() - 1);
        invulnerableMs = 1000;
        if (jugador.getVidas() <= 0) {
            jugador.setVidas(0);
            terminado = true;
        }
    }
}
