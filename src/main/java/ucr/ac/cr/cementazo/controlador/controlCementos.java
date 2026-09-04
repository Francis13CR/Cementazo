package ucr.ac.cr.cementazo.controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import ucr.ac.cr.cementazo.modelo.Area;
import ucr.ac.cr.cementazo.vista.PanelJuego;

public class controlCementos {

    private Area area;
    private PanelJuego panelJuego;
    private Timer reloj;

    public controlCementos(Area area, PanelJuego panelJuego) {
        this.area = area;
        this.panelJuego = panelJuego;
        reloj = new Timer(Area.PASO, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                area.actualizar();
                if (panelJuego != null) {
                    panelJuego.repaint();
                }
            }
        });
    }

    public void start() {
        if (reloj != null && !reloj.isRunning()) {
            reloj.start();
        }
    }

    public void detener() {
        if (reloj != null) {
            reloj.stop();
        }
    }
}
