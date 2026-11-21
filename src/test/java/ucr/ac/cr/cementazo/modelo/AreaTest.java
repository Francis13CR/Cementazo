package ucr.ac.cr.cementazo.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AreaTest {

    @Test
    void elFondoInicialSeLlamaCostaRica() {
        Area area = new Area();

        assertEquals("Costa Rica", area.getNombreFondo());
        assertEquals(Recursos.NOMBRES_FONDO[0], area.getNombreFondo());
    }

    @Test
    void elArchivoDelFondoInicialEstaDisponible() {
        Area area = new Area();

        assertEquals(Recursos.FONDO, area.getArchivoFondo());
        assertTrue(Recursos.esValida(Recursos.buscar(area.getArchivoFondo())));
    }
}
