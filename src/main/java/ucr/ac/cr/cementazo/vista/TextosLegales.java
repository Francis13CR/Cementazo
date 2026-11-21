package ucr.ac.cr.cementazo.vista;

/**
 * Textos legales y de identidad visibles para el jugador.
 *
 * Mantener el descargo en un solo lugar para que el menú, el fin de partida
 * y la documentación usen exactamente el mismo mensaje.
 */
public final class TextosLegales {

    public static final String TITULO = "Cementazo: Una sátira";
    public static final String SUBTITULO = "Una sátira sobre cemento, burocracia y obra pública";

    public static final String[] DESCARGO = {
        "Obra de ficción satírica. Los personajes, empresas e instituciones son arquetipos ficticios.",
        "La historia exagera temas de corrupción, burocracia y obra pública con fines de parodia.",
        "No afirma hechos sobre personas reales ni está afiliada a instituciones, partidos o empresas reales."
    };

    private TextosLegales() {
    }
}
