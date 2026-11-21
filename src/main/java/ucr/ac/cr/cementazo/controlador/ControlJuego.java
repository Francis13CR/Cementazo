package ucr.ac.cr.cementazo.controlador;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.Timer;
import ucr.ac.cr.cementazo.modelo.Area;
import ucr.ac.cr.cementazo.modelo.ObjetoCaida;
import ucr.ac.cr.cementazo.modelo.Perico;
import ucr.ac.cr.cementazo.modelo.Personaje;
import ucr.ac.cr.cementazo.modelo.Recursos;
import ucr.ac.cr.cementazo.modelo.ReproductorAudio;
import ucr.ac.cr.cementazo.vista.GuiJuego;
import ucr.ac.cr.cementazo.vista.PanelJuego;
import ucr.ac.cr.cementazo.vista.TextosLegales;

public class ControlJuego implements KeyListener {

    private static final Font FUENTE_MARCADOR = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FUENTE_AVISO = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 56);
    private static final Font FUENTE_SUBTITULO = new Font("SansSerif", Font.PLAIN, 22);
    private static final Font FUENTE_FIN = new Font("SansSerif", Font.BOLD, 36);
    private static final Font FUENTE_PUNTOS = new Font("SansSerif", Font.BOLD, 24);
    private static final Font FUENTE_AYUDA = new Font("SansSerif", Font.PLAIN, 18);
    private static final Font FUENTE_DESCARGO = new Font("SansSerif", Font.PLAIN, 12);

    private static final Color COLOR_FONDO_LEGAL = new Color(0, 0, 0, 190);
    private static final Color COLOR_TEXTO_LEGAL = new Color(225, 225, 225);

    private static final int FPS_OBJETIVO = 60;
    private static final long NS_POR_FRAME = 1000000000L / FPS_OBJETIVO;
    private static final int MAX_PASOS_ATRASO = 5;

    private GuiJuego guiJuego;
    private PanelJuego panelJuego;
    private Area area;
    private Personaje jugador;
    private GameState currentState;
    private ReproductorAudio musica;

    private Timer bucle;
    private long ultimoTickNs;
    private long acumuladorNs;
    private long ultimoConteoFpsNs;
    private int framesContados;
    private int fpsActuales;

    public ControlJuego(GuiJuego guiJuego) {
        this.guiJuego = guiJuego;
        currentState = GameState.MENU_PRINCIPAL;
        musica = new ReproductorAudio();
        musica.cargarEnSegundoPlano(Recursos.MUSICA_FONDO);
        area = new Area();
        jugador = area.getJugador();
        panelJuego = guiJuego.getPanelJuego();
        if (panelJuego != null) {
            panelJuego.setControlador(this);
            panelJuego.setFondo(Recursos.FONDO);
            iniciarBucle();
        }
        guiJuego.setFocusable(true);
        guiJuego.requestFocusInWindow();
    }

    private void iniciarBucle() {
        ultimoTickNs = System.nanoTime();
        ultimoConteoFpsNs = ultimoTickNs;
        acumuladorNs = 0;
        framesContados = 0;
        fpsActuales = 0;
        bucle = new Timer(1000 / FPS_OBJETIVO, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });
        bucle.start();
    }

    private void tick() {
        long ahoraNs = System.nanoTime();
        acumuladorNs += ahoraNs - ultimoTickNs;
        ultimoTickNs = ahoraNs;
        if (acumuladorNs > NS_POR_FRAME * MAX_PASOS_ATRASO) {
            acumuladorNs = NS_POR_FRAME * MAX_PASOS_ATRASO;
        }
        int pasos = 0;
        while (acumuladorNs >= NS_POR_FRAME) {
            actualizarLogica();
            acumuladorNs -= NS_POR_FRAME;
            pasos++;
        }
        if (pasos > 0 && panelJuego != null) {
            panelJuego.repaint();
            framesContados++;
        }
        if (ahoraNs - ultimoConteoFpsNs >= 1000000000L) {
            fpsActuales = framesContados;
            framesContados = 0;
            ultimoConteoFpsNs = ahoraNs;
        }
    }

    private void actualizarLogica() {
        if (currentState != GameState.JUGANDO) {
            return;
        }
        area.actualizar();
        revisarFinDePartida();
    }

    public int getFpsActuales() {
        return fpsActuales;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado == currentState) {
            return;
        }
        currentState = nuevoEstado;
        aplicarAudioDeEstado(nuevoEstado);
    }

    private void aplicarAudioDeEstado(GameState estado) {
        if (musica == null) {
            return;
        }
        switch (estado) {
            case JUGANDO:
                musica.reproducirEnBucle();
                break;
            case MENU_PRINCIPAL:
            case FIN_PARTIDA:
                musica.detener();
                break;
        }
    }

    public void empezarPartida() {
        area.iniciarPartida();
        if (panelJuego != null) {
            panelJuego.setFondo(Recursos.FONDO);
        }
        setCurrentState(GameState.JUGANDO);
    }

    public void volverAlMenu() {
        setCurrentState(GameState.MENU_PRINCIPAL);
    }

    public void revisarFinDePartida() {
        if (currentState == GameState.JUGANDO && area.estaTerminado()) {
            setCurrentState(GameState.FIN_PARTIDA);
        }
    }

    public void dibujarMenu(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 170, Area.ANCHO, 310);
        g.setColor(Color.WHITE);
        dibujarCentrado(g, TextosLegales.TITULO, FUENTE_TITULO, 250);
        dibujarCentrado(g, TextosLegales.SUBTITULO, FUENTE_SUBTITULO, 300);
        dibujarCentrado(g, "Presione ENTER para empezar", FUENTE_SUBTITULO, 370);
        dibujarCentrado(g, "Flechas para moverse   |   ESC para salir", FUENTE_AYUDA, 415);
        dibujarDescargo(g);
    }

    public void dibujarJugando(Graphics g) {
        jugador = area.getJugador();
        jugador.show(g);
        ArrayList<ObjetoCaida> objetos = area.getCaidas();
        for (int i = 0; i < objetos.size(); i++) {
            objetos.get(i).show(g);
        }
        ArrayList<Perico> pericos = area.getPericos();
        for (int i = 0; i < pericos.size(); i++) {
            pericos.get(i).show(g);
        }
        dibujarMarcador(g);
    }

    public void dibujarFinPartida(Graphics g) {
        dibujarJugando(g);
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 220, Area.ANCHO, 210);
        g.setColor(Color.WHITE);
        dibujarCentrado(g, "GAME OVER - La burocracia ganó", FUENTE_FIN, 285);
        dibujarCentrado(g, "Puntos: " + area.getPuntaje(), FUENTE_PUNTOS, 335);
        dibujarCentrado(g, "Presione R para reiniciar", FUENTE_AYUDA, 375);
        dibujarCentrado(g, "ENTER para el menú   |   ESC para salir", FUENTE_AYUDA, 405);
        dibujarDescargo(g);
    }

    private void dibujarDescargo(Graphics g) {
        g.setColor(COLOR_FONDO_LEGAL);
        g.fillRect(0, 595, Area.ANCHO, Area.ALTO - 595);
        g.setColor(COLOR_TEXTO_LEGAL);
        g.setFont(FUENTE_DESCARGO);
        FontMetrics medidas = g.getFontMetrics();
        int y = 617;
        for (int i = 0; i < TextosLegales.DESCARGO.length; i++) {
            String linea = TextosLegales.DESCARGO[i];
            g.drawString(linea, (Area.ANCHO - medidas.stringWidth(linea)) / 2, y);
            y += medidas.getHeight();
        }
    }

    private void dibujarCentrado(Graphics g, String texto, Font fuente, int y) {
        g.setFont(fuente);
        FontMetrics medidas = g.getFontMetrics(fuente);
        int x = (Area.ANCHO - medidas.stringWidth(texto)) / 2;
        g.drawString(texto, x, y);
    }

    private void dibujarMarcador(Graphics g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(12, 12, 360, 78);
        g.setColor(Color.WHITE);
        g.setFont(FUENTE_MARCADOR);
        g.drawString("Vidas: " + area.getJugador().getVidas(), 24, 40);
        g.drawString("FPS: " + fpsActuales, 150, 40);
        g.drawString("Puntos: " + area.getPuntaje(), 24, 68);
        g.setFont(FUENTE_AVISO);
        g.drawString(area.getNombreFondo(), 270, 40);
        g.drawString("Dificultad", 200, 68);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (musica != null) {
                musica.liberar();
            }
            System.exit(0);
        }
        switch (currentState) {
            case MENU_PRINCIPAL:
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    empezarPartida();
                }
                break;
            case JUGANDO:
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    area.moverIzquierda();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    area.moverDerecha();
                }
                break;
            case FIN_PARTIDA:
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    empezarPartida();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    volverAlMenu();
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }
}
