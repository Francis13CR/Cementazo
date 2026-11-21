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
java -cp target/Cementazo-1.0.jar ucr.ac.cr.cementazo.vista.GuiJuego
```

El `pom.xml` todavía no declara `Main-Class`; por eso se ejecuta con `-cp`. La
tarea de build de la Ronda 1 añadirá `java -jar`.

## Tests

Todavía no hay tests automatizados. La Ronda 3 del plan añade JUnit 5 y CI.

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
