package ucr.ac.cr.cementazo.modelo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ReproductorAudioTest {

    @Test
    void elEstadoDeSilencioSeActivaYDesactivaSinAudioCargado() {
        ReproductorAudio audio = new ReproductorAudio();

        assertFalse(audio.estaSilenciado());

        audio.silenciar(true);
        audio.silenciar(true);
        assertTrue(audio.estaSilenciado());

        audio.silenciar(false);
        assertFalse(audio.estaSilenciado());
    }
}
