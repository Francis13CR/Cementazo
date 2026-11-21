# Cementazo: Una sátira

Juego arcade en Swing donde el jugador atrapa sacos de cemento, esquiva
esposas y lidia con un perico ladrón. Es una sátira sobre la corrupción, la
burocracia y la obra pública.

> **Obra de ficción satírica.** Los personajes, empresas e instituciones son
> arquetipos ficticios. La historia exagera temas de corrupción, burocracia y
> obra pública con fines de parodia. No afirma hechos sobre personas reales ni
> está afiliada a instituciones, partidos o empresas reales.

## Estado

Prototipo jugable en mejora activa. El plan detallado está en
[`PLAN_MEJORAS.md`](PLAN_MEJORAS.md).

## Controles

| Tecla | Acción |
|---|---|
| ← / → | Mover al personaje |
| Enter | Empezar |
| ESC | Salir desde el menú |
| R | Reiniciar al terminar |

## Requisitos

- JDK 21 (probado con OpenJDK 21).
- Maven 3.8+.
- Un entorno gráfico con `DISPLAY` o Wayland; Swing no funciona en modo headless.

## Compilar y ejecutar

Desde la raíz del workspace:

```bash
./run.sh
```

Desde la carpeta del proyecto:

```bash
cd Cementazo
mvn -q clean package
java -jar target/Cementazo-1.0.jar
```

El jar incluye `Main-Class: ucr.ac.cr.cementazo.vista.GuiJuego`, así que también
funciona con `java -jar`. El script `run.sh` hace el build y ejecuta el juego.

## Tests

```bash
cd Cementazo
mvn test
```

Hay tests JUnit 5 de la lógica del modelo (por ahora `AreaTest`). La Ronda 3 del
plan amplía la cobertura y añade CI.

## Assets y licencias

- Código: **MIT**, ver [`LICENSE`](LICENSE).
- Imágenes y sprites: autoría declarada por el dueño, todos los derechos
  reservados; ver [`LICENSE-ASSETS`](LICENSE-ASSETS) y [`ASSETS.md`](ASSETS.md).
- Música: obra de un tercero usada con permiso del autor. La constancia escrita
  está pendiente en [`docs/permiso-musica.md`](docs/permiso-musica.md).

## Información legal

La obra es ficción satírica. Antes de publicar comercialmente, revisar
[`LEGAL.md`](LEGAL.md) y la decisión sobre el título en
[`docs/marca.md`](docs/marca.md).

## Estructura

```
src/main/java/ucr/ac/cr/cementazo/
├── modelo/       # reglas, entidades, recursos y audio
├── vista/        # ventana y panel Swing
└── controlador/  # estados, input y bucle de juego
```
