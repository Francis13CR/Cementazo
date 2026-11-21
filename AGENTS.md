# AGENTS.md

## Layout
- The workspace root is a wrapper: the real Maven project and the only git repo live in `Cementazo/`. Maven commands from the root fail with "no POM in this directory".
- `run.sh` (root, outside git) builds and launches the game. `Cementazo/arte_retirado/` is retired art, not referenced by code.
- `PLAN_MEJORAS.md` is the detailed improvement and legal roadmap. Check the current round/phase before starting work.

## Build and run
- JDK 21 and Maven 3.8.7 are installed system-wide; there is no Maven wrapper and no runtime dependencies.
- `pom.xml` targets Java 8 (`maven.compiler.release=8`); `-Xlint:-options` suppresses javac 21's obsolete-target warning. Do not "fix" it by bumping the version without deciding to drop Java 8.
- Build: `cd Cementazo && mvn -q clean package` → `target/Cementazo-1.0.jar` (images and music bundled).
- Run: `java -jar target/Cementazo-1.0.jar` or simply `./run.sh` from the workspace root. The jar has `Main-Class: ucr.ac.cr.cementazo.vista.GuiJuego`.
- Tests: `mvn test` runs the JUnit 5 model tests under `src/test/java` (`AreaTest`, `ReproductorAudioTest`). There is no lint, formatter, or CI yet. UI changes still need a manual GUI launch.
- Launching the game requires a display (`DISPLAY`/Wayland); `mvn test` runs headless.
- Optional smoke test: `timeout 10s ./run.sh`; exit 124 means the window stayed up. It opens a real window on the user's desktop.

## Architecture and quirks
- Swing game in three layers: `modelo/` holds state and rules (`Area`), resources, and audio; `vista/` holds `GuiJuego` (JFrame) and `PanelJuego` (painting); `controlador/ControlJuego` owns the state machine (`GameState`), keyboard input, the 60 FPS loop, and HUD/menu drawing. `PanelJuego.paintComponent` only delegates to `ControlJuego`.
- The loop is fixed-timestep 60 FPS with at most 5 catch-up steps. Spawn, invulnerability, and difficulty timings are frame-based (`Area.PASO`), so changing the FPS constant changes gameplay balance.
- Game coordinates are hardcoded to `Area.ANCHO/ALTO` = 1005x687. `GuiJuego` sizes the panel to that and is non-resizable; entities do not scale if the coordinate space changes. `PanelJuego`'s own generated layout is a 400x300 placeholder.
- `GuiJuego.java` and `PanelJuego.java` are NetBeans Matisse forms paired with `.form` files. Do not hand-edit inside `//GEN-BEGIN` blocks (they are regenerated); add code outside those regions or use NetBeans.
- Missing images silently become solid-color rectangles (`Recursos`); missing audio only logs a warning. If sprites look like colored blocks, check asset names/paths instead of assuming a rendering bug. `Recursos` tries `./src/main/resources/img/...` relative to the current directory first, then the classpath `/img/...`.
- User-facing text is Spanish; keep new messages consistent.

## Content and legal
- Read `LEGAL.md` and `ASSETS.md` before adding characters, dialogue, images, or music. The game is fiction satire: do not add real people's names, likenesses, logos, or unproven factual accusations.
- Code is MIT (`LICENSE`); images are all rights reserved (`LICENSE-ASSETS`); music is third-party with permission documented in `docs/permiso-musica.md`. Record any new asset's provenance in `ASSETS.md` before committing it.
