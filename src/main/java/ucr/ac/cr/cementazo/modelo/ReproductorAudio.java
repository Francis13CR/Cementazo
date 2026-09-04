package ucr.ac.cr.cementazo.modelo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ReproductorAudio {

    private static final Logger REGISTRO = Logger.getLogger(ReproductorAudio.class.getName());

    private Clip clip;
    private long posicionGuardada;
    private boolean reproducirAlCargar;
    private boolean cargando;
    private boolean fallido;

    public synchronized void cargarAudio(String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            fallido = true;
            cargando = false;
            return;
        }
        liberarInterno();
        fallido = false;
        AudioInputStream flujo = null;
        try {
            URL url = localizarAudio(ruta);
            if (url == null) {
                REGISTRO.log(Level.WARNING, "No se encontró el audio: {0}", ruta);
                fallido = true;
                return;
            }
            flujo = AudioSystem.getAudioInputStream(url);
            AudioFormat formato = flujo.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, formato);
            if (!AudioSystem.isLineSupported(info)) {
                AudioFormat pcm = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        formato.getSampleRate(),
                        16,
                        formato.getChannels(),
                        formato.getChannels() * 2,
                        formato.getSampleRate(),
                        false);
                flujo = AudioSystem.getAudioInputStream(pcm, flujo);
                info = new DataLine.Info(Clip.class, flujo.getFormat());
            }
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(flujo);
            posicionGuardada = 0;
        } catch (UnsupportedAudioFileException ex) {
            REGISTRO.log(Level.WARNING, "Formato de audio no soportado: " + ruta, ex);
            fallido = true;
            clip = null;
        } catch (LineUnavailableException ex) {
            REGISTRO.log(Level.WARNING, "Línea de audio no disponible", ex);
            fallido = true;
            clip = null;
        } catch (IllegalArgumentException ex) {
            REGISTRO.log(Level.WARNING, "No hay dispositivo de audio compatible", ex);
            fallido = true;
            clip = null;
        } catch (IOException ex) {
            REGISTRO.log(Level.WARNING, "Error al leer el audio: " + ruta, ex);
            fallido = true;
            clip = null;
        } finally {
            if (flujo != null) {
                try {
                    flujo.close();
                } catch (IOException ignorada) {
                }
            }
            cargando = false;
            if (reproducirAlCargar && clip != null) {
                reproducirAlCargar = false;
                iniciarBucle();
            }
        }
    }

    public void cargarEnSegundoPlano(final String ruta) {
        synchronized (this) {
            if (cargando) {
                return;
            }
            cargando = true;
        }
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                cargarAudio(ruta);
            }
        }, "carga-audio");
        hilo.setDaemon(true);
        hilo.start();
    }

    public synchronized void reproducirEnBucle() {
        if (fallido) {
            return;
        }
        if (clip == null) {
            reproducirAlCargar = true;
            return;
        }
        iniciarBucle();
    }

    public synchronized void pausar() {
        if (clip == null || !clip.isRunning()) {
            return;
        }
        posicionGuardada = clip.getMicrosecondPosition();
        clip.stop();
    }

    public synchronized void reanudar() {
        if (clip == null) {
            if (!fallido) {
                reproducirAlCargar = true;
            }
            return;
        }
        if (clip.isRunning()) {
            return;
        }
        clip.setMicrosecondPosition(posicionGuardada);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public synchronized void detener() {
        reproducirAlCargar = false;
        posicionGuardada = 0;
        if (clip == null) {
            return;
        }
        clip.stop();
        clip.setFramePosition(0);
    }

    public synchronized void liberar() {
        reproducirAlCargar = false;
        liberarInterno();
    }

    public synchronized boolean estaSonando() {
        return clip != null && clip.isRunning();
    }

    private URL localizarAudio(String ruta) {
        URL empaquetado = getClass().getResource(ruta);
        if (empaquetado != null) {
            return empaquetado;
        }
        String nombre = ruta;
        int barra = ruta.lastIndexOf('/');
        if (barra >= 0 && barra < ruta.length() - 1) {
            nombre = ruta.substring(barra + 1);
        }
        String sufijo = ruta.startsWith("/") ? ruta : "/" + ruta;
        String[] candidatos = {
            "src/main/resources" + sufijo,
            "./src/main/resources" + sufijo,
            "src/main/resources/music/" + nombre,
            "./src/main/resources/music/" + nombre
        };
        for (int i = 0; i < candidatos.length; i++) {
            File archivo = new File(candidatos[i]);
            if (archivo.isFile()) {
                try {
                    return archivo.toURI().toURL();
                } catch (MalformedURLException ignorada) {
                }
            }
        }
        return null;
    }

    private void iniciarBucle() {
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        posicionGuardada = 0;
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    private void liberarInterno() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
        posicionGuardada = 0;
    }
}
