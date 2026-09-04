package ucr.ac.cr.cementazo.controlador;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import ucr.ac.cr.cementazo.modelo.Area;
import ucr.ac.cr.cementazo.modelo.ObjetoCaida;
import ucr.ac.cr.cementazo.modelo.Personaje;
import ucr.ac.cr.cementazo.vista.GuiJuego;
import ucr.ac.cr.cementazo.vista.PanelJuego;

public class ControlJuego implements KeyListener {

    private static final Font FUENTE_MARCADOR = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FUENTE_AVISO = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FUENTE_FIN = new Font("SansSerif", Font.BOLD, 36);
    private static final Font FUENTE_PUNTOS = new Font("SansSerif", Font.BOLD, 24);
    private static final Font FUENTE_AYUDA = new Font("SansSerif", Font.PLAIN, 18);

    private GuiJuego guiJuego;
    private PanelJuego panelJuego;
    private Area area;
    private Personaje bolanos;
    private controlCementos cementos;

    public ControlJuego(GuiJuego guiJuego) {
        this.guiJuego = guiJuego;
        area = new Area();
        bolanos = area.getBolanos();
        panelJuego = guiJuego.getPanelJuego();
        if (panelJuego != null) {
            panelJuego.setControlador(this);
        }
        cementos = new controlCementos(area, panelJuego);
        if (panelJuego != null) {
            cementos.start();
        }
        guiJuego.setFocusable(true);
        guiJuego.requestFocusInWindow();
    }

    public void dibujar(Graphics g) {
        bolanos = area.getBolanos();
        bolanos.show(g);
        for (int i = 0; i < area.getCaidas().size(); i++) {
            ObjetoCaida objeto = area.getCaidas().get(i);
            objeto.show(g);
        }
        dibujarMarcador(g);
        if (area.estaTerminado()) {
            dibujarFin(g);
        }
    }

    private void dibujarMarcador(Graphics g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(12, 12, 360, 78);
        g.setColor(Color.WHITE);
        g.setFont(FUENTE_MARCADOR);
        g.drawString("Vidas: " + area.getBolanos().getVidas(), 24, 40);
        g.drawString("Puntos: " + area.getPuntaje(), 24, 68);
        g.setFont(FUENTE_AVISO);
        g.drawString("Dificultad", 200, 40);
    }

    private void dibujarFin(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(220, 220, 560, 200);
        g.setColor(Color.WHITE);
        g.setFont(FUENTE_FIN);
        g.drawString("Fin de la partida", 320, 290);
        g.setFont(FUENTE_PUNTOS);
        g.drawString("Puntos: " + area.getPuntaje(), 400, 335);
        g.setFont(FUENTE_AYUDA);
        g.drawString("ENTER para jugar de nuevo   |   ESC para salir", 270, 380);
    }

    public void iniciar(Graphics g) {
        dibujar(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER && area.estaTerminado()) {
            area.iniciarPartida();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            area.moverIzquierda();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            area.moverDerecha();
        }
        if (panelJuego != null) {
            panelJuego.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }
}
