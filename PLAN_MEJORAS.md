# Plan de mejoras de Cementazo

> **Versión:** 1.0  
> **Fecha:** 2026-11-21  
> **Ámbito:** juego Swing `Cementazo/` (Java 8 objetivo, Maven, JDK 21 instalado)  
> **Uso:** este documento está pensado para que lo ejecute una persona o una IA **sin contexto previo**. Cada tarea indica archivos, pasos, criterios de aceptación y verificación.
>
> **Aviso:** la sección legal es información general, no asesoría jurídica. Antes de publicar comercialmente, consulta a un abogado costarricense.

---

## 0. Cómo usar este plan

### 0.1 Reglas de oro

1. **No tocar** el código dentro de los bloques `//GEN-BEGIN:initComponents` de `GuiJuego.java` y `PanelJuego.java`: NetBeans los regenera.
2. **No cambiar** `Area.ANCHO`/`Area.ALTO` (1005x687) salvo que la tarea lo pida explícitamente; muchas posiciones y colisiones dependen de esos valores.
3. **No añadir dependencias de runtime** sin justificarlo. La única dependencia nueva prevista es JUnit 5, con scope `test`.
4. Mantener compatibilidad con **Java 8** mientras el `pom.xml` declare `release 8`.
5. Todo texto visible al jugador va en **español**.
6. Un commit por tarea o por fase pequeña; el mensaje debe describir el cambio.
7. Antes de cada commit: `mvn -B clean verify` y prueba de arranque.
8. No commitear `target/`, el WAV original de 27 MB, ni datos personales.
9. Si una tarea cambia comportamiento, actualizar `README.md`/`LEGAL.md`/`ASSETS.md` en el mismo commit.
10. Si algo no está claro, **no inventar nombres reales, licencias ni hechos**: dejar un `TODO` y consultar al dueño.
11. Al terminar cada ronda, actualizar `AGENTS.md` si cambiaron los comandos, los tests o el CI (por ejemplo, la línea “no hay tests/CI” deja de ser cierta en la Ronda 3).

### 0.2 Decisiones que requieren al dueño antes de publicar

| Decisión | Opciones | Valor por defecto del plan |
|---|---|---|
| Título | Mantener “Cementazo” con subtítulo “Una sátira” / renombrar a un nombre ficticio | Mantener título + subtítulo + descargo; personajes e instituciones ficticios |
| Licencia del código | MIT / Apache-2.0 / propietaria | MIT para código, pendiente de confirmar |
| Licencia de arte y música | CC BY 4.0 / CC BY-NC 4.0 / todos los derechos reservados | No publicar hasta documentar autoría y licencia |
| Identidad del autor | Nombre real / seudónimo | No reescribir el historial sin autorización; ver §7.8 |
| Monetización | Gratis / donaciones / venta | Asumir gratis para las 3 rondas; si hay venta, gate legal §3.6 |
| ContraJuego del perico | Lanzar sacos / solo penalización de puntos | Implementar lanzamiento de sacos; si el tiempo no alcanza, penalización |

### 0.3 Formato de cada tarea

Cada tarea tiene:

- **Objetivo**
- **Archivos**
- **Pasos**
- **Aceptación**
- **Verificación**

Los IDs (`R1-F2-T3`, etc.) sirven para dividir el trabajo y para que una IA sepa qué está haciendo.

---

## 1. Línea base verificada

### 1.1 Estructura

```
/home/francis/Documentos/cementazo/        # workspace raíz
├── AGENTS.md                              # enlace a Cementazo/AGENTS.md
├── run.sh                                 # compila y ejecuta
└── Cementazo/                             # repo git y proyecto Maven real
    ├── pom.xml
    ├── src/main/java/ucr/ac/cr/cementazo/
    │   ├── modelo/                        # Area, Personaje, Perico, ObjetoCaida, Recursos, audio
    │   ├── vista/                         # GuiJuego (JFrame), PanelJuego (pintado)
    │   └── controlador/                   # ControlJuego (estado, input, bucle, HUD), GameState
    ├── src/main/resources/img/            # sprites y fondos
    ├── src/main/resources/music/          # tema_fondo.wav (27 MB)
    └── arte_retirado/                     # arte viejo, no referenciado
```

### 1.2 Comandos

Desde la raíz del workspace:

```bash
cd ~/Documentos/cementazo
./run.sh
```

Equivalente manual:

```bash
cd ~/Documentos/cementazo/Cementazo
mvn -q clean package
java -cp target/Cementazo-1.0.jar ucr.ac.cr.cementazo.vista.GuiJuego
```

Prueba de humo (abre una ventana real; se cierra sola a los 10 s):

```bash
cd ~/Documentos/cementazo
timeout 10s ./run.sh
# exit 124 = la ventana siguió viva; cualquier otro código inesperado = revisar
```

Estado actual conocido:

- JDK 21 y Maven 3.8.7 instalados.
- `pom.xml` con `source/target 1.8`, sin dependencias, sin `Main-Class`.
- Sin tests, sin lint, sin CI.
- Jar actual: ~30 MB; `tema_fondo.wav` = 27 MB.
- 7 fondos provinciales incluidos, pero solo se usa `bg_costa_rica.png`.
- 6 PNG duplicados/no referenciados: `cemento.png`, `dinero.png`, `esposas.png`, `fondo.png`, `pDerecha.png`, `pIzquierda.png`.
- Estado de juego: `MENU_PRINCIPAL`, `JUGANDO`, `FIN_PARTIDA`.
- Puntaje: `int`; +1/s; cemento +15; dinero duplica; esposas dividen entre 2.
- 3 vidas; invulnerabilidad de 1000 ms; máximo 7 objetos cayendo.
- Movimiento: 24 px por evento `keyPressed` (depende del auto-repeat).
- Ventana de 1005x687, redimensionable, no centrada.
- `GuiJuego` usa `KeyListener` sobre el `JFrame`.

### 1.3 Mapa de archivos que se va a tocar

| Archivo | Rol actual | Cambios previstos |
|---|---|---|
| `pom.xml` | Build | Main-Class, release 8, JUnit, plugins, CI-friendly |
| `Area.java` | Reglas, entidades, puntaje, fondos | dt, long, niveles, estadísticas, proyectiles, caché |
| `ControlJuego.java` | Bucle, input, estados, dibujo HUD/menús | pausa, menú, récord, feedback, estados |
| `GuiJuego.java` | JFrame + form NetBeans | foco, centrado, cierre, tamaño |
| `PanelJuego.java` | Pintado + form NetBeans | fondo, escalado, feedback visual |
| `Perico.java` | Ladrón | contraJuego, estados claros |
| `ObjetoCaida.java` | Caída | velocidad en px/s, dt |
| `Personaje.java` | Vidas | invulnerabilidad expuesta |
| `Principal.java` | Base de entidades | hitboxes/constantes |
| `Recursos.java` | Carga de assets | caché, escalado pixel art, fondos |
| `ReproductorAudio.java` | Música | pausa/reanudación, mute, SFX |
| `GameState.java` | Estados | PAUSA, COMO_JUGAR, OPCIONES |
| `src/test/java/...` | No existe | tests de `Area` y reglas |
| `.github/workflows/build.yml` | No existe | CI |
| `README.md`, `LEGAL.md`, `ASSETS.md`, `LICENSE*` | No existen | documentación y licencias |

---

## 2. RONDA 1 — Contención legal, base y quick wins

**Objetivo de la ronda:** reducir el riesgo legal, arreglar lo obvio, añadir pausa/récord/menú mínimo y bajar el peso del jar. Al final, el juego debe poder publicarse como **demo gratuita** sin nombres reales, sin assets sin licencia y sin errores evidentes.

**Duración orientativa:** 1-2 días de trabajo efectivo.

### Fase 1.1 — Legal y contenido (hacer primero)

> **Estado (2026-11-21):** T1-T4 implementadas en el repositorio. Pendientes
> antes de un release público/comercial: completar `docs/permiso-musica.md` con
> la constancia del autor y hacer la búsqueda formal de marca si se monetiza.

#### R1-F1-T1: Documento legal y descargo

**Objetivo:** dejar por escrito que es una obra de ficción satírica y fijar las reglas de contenido.

**Archivos:** `LEGAL.md` (nuevo), `README.md` (nuevo o sección), `ControlJuego.java` (menú).

**Pasos:**

1. Crear `LEGAL.md` con:
   - Aviso de ficción satírica.
   - Explicación de que personajes, empresas e instituciones son ficticios o arquetípicos.
   - Declaración de no afiliación.
   - Aviso de que no se afirma que personas reales hayan cometido los hechos representados.
   - Fuentes legales consultadas (§7.9) y fecha de revisión.
   - Nota: “No es asesoría legal”.
2. Texto base para el menú y el README:

   > **Obra de ficción satírica.** Los personajes, empresas e instituciones de este juego son arquetipos ficticios. La historia exagera temas de corrupción, burocracia y obra pública con fines de parodia. No afirma que ninguna persona real haya cometido los hechos representados y no está afiliada a instituciones, partidos ni empresas reales.

3. Añadir el descargo en el menú principal, en letra pequeña y con contraste suficiente. No debe tapar las opciones.
4. Añadir el descargo al final de la pantalla de fin de partida o en “Créditos”.
5. Añadir el descargo al README y a la futura página de tienda.

**Aceptación:**
- El descargo aparece en el menú y en `LEGAL.md`.
- No se usan nombres, caras, logos ni símbolos reales en el texto visible.
- El README no afirma “basado en hechos reales”.

**Verificación:** buscar en el código y docs: `grep -RniE "basado en hechos reales|Sinocem|Juan Carlos|Bolaños|BCR|Gobierno de" .` y revisar cada coincidencia.

#### R1-F1-T2: Ficcionalizar nombres e identificadores

**Objetivo:** que ni el código público ni la UI identifiquen a personas reales.

**Archivos:** `Area.java`, `Personaje.java`, `ControlJuego.java`, `PanelJuego.java`, `README.md`.

**Pasos:**

1. Renombrar el campo `bolanos` y el método `getBolanos()` a `jugador`/`getJugador()` en todo el proyecto. Usar el refactor del IDE o `sed` con cuidado; compilar después.
2. Revisar si `Cementazo` aparece como referencia al caso real en README/código. Mantenerlo solo como título de la obra, con subtítulo “Una sátira”.
3. Si el dueño decide renombrar el juego, usar un nombre ficticio y actualizar:
   - `GuiJuego.setTitle(...)`
   - Título del menú
   - README/LEGAL
   - Nombre del jar/artefacto (`artifactId` si se desea)
4. No renombrar paquetes Java ni clases públicas en esta tarea; solo el dominio del jugador y textos.

**Aceptación:** `grep -Rni "bolanos\|bolaños" src README.md` no devuelve nada. El build sigue verde.

#### R1-F1-T3: Auditoría de assets y licencias

**Objetivo:** saber qué se puede distribuir y qué no.

**Archivos:** `ASSETS.md` (nuevo), `LICENSE`, `LICENSE-ASSETS`.

**Pasos:**

1. Crear `ASSETS.md` con una tabla:

   | Archivo | Tipo | Autor | Fuente/URL | Licencia | Modificaciones | ¿Se puede distribuir? |
   |---|---|---|---|---|---|---|
   | `tema_fondo.wav` | música | desconocido | desconocido | desconocido | convertido | **BLOQUEADO hasta aclarar** |
   | `sprite_personaje_derecha.png` | arte | ... | ... | ... | ... | ... |

2. Verificar el origen de `tema_fondo.wav` y de cada PNG. Si no hay autor/licencia, **no publicar** hasta reemplazarlos por:
   - Arte/música propios.
   - Assets CC0/CC BY con atribución.
   - Música generada específicamente para el juego.
3. Recordar que, en Costa Rica, la Ley 6683 art. 8 exige autorización para parodiar una obra de dominio privado. **No usar personajes, música o arte de terceros “porque es parodia”.**
4. Añadir `LICENSE` para el código (MIT recomendado, pendiente de confirmar) y `LICENSE-ASSETS` para arte/música.
5. Si se usa música CC BY, incluir atribución exacta y enlace en “Créditos”.

**Aceptación:**
- Todos los assets del jar tienen fila en `ASSETS.md`.
- Ningún asset queda como “desconocido” antes de un release público.
- `LICENSE` y `LICENSE-ASSETS` existen o hay un `TODO` explícito.

#### R1-F1-T4: Búsqueda de marca y título

**Objetivo:** reducir el riesgo de conflicto de marca si se va a vender.

**Pasos:**

1. Antes de monetizar, buscar “Cementazo” y el nombre elegido en el Registro de Propiedad Industrial de Costa Rica (clases de juegos/entretenimiento, software).
2. Revisar la Ley 7978, arts. 7(i), 8(f) y 8(g): no registrar como marca signos que ridiculicen personas o símbolos, ni que afecten nombre/imagen de terceros. La norma regula el registro; además, evita usar logos o nombres reales en el juego por riesgo de confusión o asociación.
3. Si hay conflicto o duda, renombrar el juego antes de la tienda.
4. Guardar el resultado de la búsqueda en `docs/marca.md` con fecha y captura.

**Aceptación:** decisión documentada; si no hay búsqueda, no hay release comercial.

> **Gate legal de la Ronda 1:** no se publica nada hasta completar R1-F1-T1 a T4 y resolver la licencia del audio/arte.

### Fase 1.2 — Base técnica y bugs obvios

#### R1-F2-T1: `Main-Class`, release 8 y plugins

**Objetivo:** que `java -jar` funcione y que el build sea reproducible.

**Archivos:** `pom.xml`.

**Pasos:**

1. Añadir `maven-jar-plugin` con `mainClass`:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-jar-plugin</artifactId>
      <version>3.4.2</version>
      <configuration>
        <archive>
          <manifest>
            <mainClass>ucr.ac.cr.cementazo.vista.GuiJuego</mainClass>
          </manifest>
        </archive>
      </configuration>
    </plugin>
  </plugins>
</build>
```

2. Reemplazar `source/target 1.8` por:

```xml
<properties>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <maven.compiler.release>8</maven.compiler.release>
</properties>
```

3. Fijar `maven-compiler-plugin` (por ejemplo 3.13.0) y `maven-surefire-plugin` cuando se añadan tests.
4. Verificar que no se usan APIs posteriores a Java 8.

**Aceptación:**
- `mvn -B clean package` sin warnings de “source 8 is obsolete”.
- `java -jar target/Cementazo-1.0.jar` abre el juego.
- `./run.sh` sigue funcionando.

#### R1-F2-T2: Arreglar HUD “Dificultad” y etiqueta de fondo

**Archivos:** `ControlJuego.java`, `Area.java`, `Recursos.java`.

**Pasos:**

1. En `dibujarMarcador`, quitar el texto “Dificultad” suelto o mostrar un valor real. Para la Ronda 1, lo más simple es:
   - Eliminar esa línea, o
   - Mostrar `Nivel 1` usando un campo `nivel` que se implementará en Ronda 2.
2. Cambiar `nombreFondo` inicial de `"San José"` a `"Costa Rica"` mientras el asset sea `bg_costa_rica.png`. En Ronda 2 se implementará la rotación real.
3. Revisar que el HUD no muestre etiquetas sin valor.

**Aceptación:** el HUD no tiene textos huérfanos y la etiqueta coincide con el fondo visible.

#### R1-F2-T3: Limpieza de código muerto

**Archivos:** los indicados.

**Pasos:**

1. Eliminar o conectar:
   - `GuiJuego.getControladorGUIJuego()`
   - `GuiJuego.actualizar(Graphics)`
   - `PanelJuego.actualizar(Graphics)`
   - `PanelJuego.setPersonaje(...)` y su campo `bolanos`
   - `Area.getArchivoFondo()` (se usará en Ronda 2; si no, eliminar)
   - `Area.getVelocidadCaida()` (si no se usa)
   - `ObjetoCaida.setVelocidadY(...)` (si no se usa)
   - `ControlJuego.getFpsActuales()` (si no se usa)
2. No eliminar `ReproductorAudio.pausar()`, `reanudar()` ni `estaSonando()`: se usarán en la pausa.
3. Compilar después de cada bloque de borrado.

**Aceptación:** build verde; `grep` de los métodos eliminados no devuelve referencias.

#### R1-F2-T4: Ventana centrada y tamaño fijo

**Archivos:** `GuiJuego.java`.

**Pasos:**

1. En el constructor o en `main`, después de `pack()`:
   - `setLocationRelativeTo(null);`
   - `setResizable(false);` (solución temporal hasta implementar escalado).
   - `setMinimumSize(getSize());`
2. Añadir icono de ventana si hay un PNG cuadrado adecuado (opcional).
3. Verificar en 1280x800 y 1920x1080.

**Aceptación:** la ventana aparece centrada, no se puede redimensionar y cabe en una pantalla de 1280x800.

### Fase 1.3 — Pausa, foco y audio

#### R1-F3-T1: Estado `PAUSA`

**Archivos:** `GameState.java`, `ControlJuego.java`, `PanelJuego.java`.

**Pasos:**

1. Añadir `PAUSA` al enum.
2. Añadir `dibujarPausa(Graphics)` en `ControlJuego`:
   - Fondo semitransparente.
   - “PAUSA”.
   - “P: continuar”.
   - “ESC: volver al menú”.
   - “M: silenciar”.
3. En `PanelJuego.paintComponent`, añadir el caso `PAUSA`.
4. Definir transiciones exactas:
   - `MENU_PRINCIPAL` + Enter → `JUGANDO`.
   - `JUGANDO` + P o ESC → `PAUSA`.
   - `PAUSA` + P → `JUGANDO`.
   - `PAUSA` + ESC → `MENU_PRINCIPAL`.
   - `MENU_PRINCIPAL` + ESC → cerrar la ventana.
   - `FIN_PARTIDA` + R → `JUGANDO`; Enter → `MENU_PRINCIPAL`.
5. Al pausar: `musica.pausar()`. Al reanudar: `musica.reanudar()`. Al volver al menú: `musica.detener()`.
6. No usar `System.exit(0)` desde `ControlJuego`; cerrar con `guiJuego.dispatchEvent(new WindowEvent(guiJuego, WindowEvent.WINDOW_CLOSING))` o `guiJuego.dispose()`.

**Aceptación:** se puede pausar y reanudar sin perder la partida; la música se detiene y continúa; ESC no cierra el juego durante una partida.

#### R1-F3-T2: Pausa automática al perder foco/minimizar

**Archivos:** `GuiJuego.java` o `ControlJuego.java`.

**Pasos:**

1. Añadir un `WindowFocusListener` al `JFrame`:
   - `windowLostFocus`: si `estado == JUGANDO`, pasar a `PAUSA`.
   - `windowGainedFocus`: no reanudar automáticamente; esperar a que el jugador presione P.
2. Añadir `ComponentListener.componentIconified`: pausar si estaba jugando.
3. Evitar pausar cuando el foco se pierde por un diálogo interno que no existe; si se añade, manejar explícitamente.

**Aceptación:** cambiar de ventana durante la partida pausa el juego; no se pierden vidas mientras el juego no está visible.

#### R1-F3-T3: Mute con M

**Archivos:** `ReproductorAudio.java`, `ControlJuego.java`.

**Pasos:**

1. Añadir `silenciar(boolean)` a `ReproductorAudio`:
   - Obtener `FloatControl` `MASTER_GAIN` del `Clip`.
   - Si no está soportado, guardar el estado y usar `clip.stop()`/`clip.loop()` como fallback.
2. Añadir `M` para alternar mute en cualquier estado.
3. Mostrar “Silencio: ON/OFF” en el menú/pausa.
4. Persistir la preferencia con `Preferences` (opcional en Ronda 1).

**Aceptación:** M silencia y restaura sin reiniciar la pista; el estado se refleja en la UI.

### Fase 1.4 — Récord y menú mínimo

#### R1-F4-T1: `long` para el puntaje

**Archivos:** `Area.java`, `ControlJuego.java`.

**Pasos:**

1. Cambiar `private int puntaje` a `private long puntaje`.
2. Cambiar getters y operaciones a `long`.
3. En el HUD, formatear con `String.format("%,d", area.getPuntaje())` (locale español).
4. Revisar `puntaje * 2`; con `long` no hay desbordamiento práctico.
5. Añadir un test unitario simple cuando exista JUnit (Ronda 3) o una comprobación manual.

**Aceptación:** el puntaje no desborda tras muchas partidas; se muestra con separador de miles.

#### R1-F4-T2: Récord persistente

**Archivos:** `modelo/Record.java` (nuevo), `ControlJuego.java`, `Area.java`.

**Pasos:**

1. Crear `Record` con `Preferences.userNodeForPackage(Record.class)`:
   - `long cargar()`.
   - `void guardar(long valor)`.
   - Clave `"cementazo.record"`.
2. Al terminar la partida, comparar `area.getPuntaje()` con el récord y guardar si es mayor.
3. Mostrar “Récord: X” en el menú y en la pantalla de fin de partida.
4. Mostrar “¡Nuevo récord!” si se supera.
5. Añadir una forma de resetear el récord para pruebas (opcional: variable de entorno o método de test).

**Aceptación:** cerrar y abrir el juego conserva el récord; el récord se actualiza solo al superarlo.

#### R1-F4-T3: Menú mínimo con instrucciones

**Archivos:** `ControlJuego.java`, `GameState.java`, `PanelJuego.java`, `Recursos.java`.

**Pasos:**

1. Añadir `GameState.COMO_JUGAR` (o un booleano `mostrarAyuda` si se prefiere algo mínimo).
2. En el menú:
   - Título: “Cementazo: Una sátira”.
   - “Enter: jugar”.
   - “C: cómo jugar”.
   - “M: silenciar”.
   - “Esc: salir”.
   - “Récord: …”.
   - Descargo en letra pequeña.
3. Pantalla “Cómo jugar”:
   - Objetivo: atrapar cemento, evitar esposas, el dinero duplica.
   - Controles: ←/→ mover; P pausa; M mute; Esc menú/salir.
   - Puntaje: +1/s, cemento +15, dinero x2, esposas /2.
   - “Enter/Esc: volver”.
4. Implementar navegación con teclado, sin depender del mouse.

**Aceptación:** se puede llegar a “Cómo jugar” y volver sin quedar atrapado; el menú cabe en 1005x687.

### Fase 1.5 — Audio comprimido

#### R1-F5-T1: Convertir el WAV

**Archivos:** `src/main/resources/music/tema_fondo.wav`, `ASSETS.md`.

**Pasos:**

1. Guardar una copia del original **fuera del repo** (por ejemplo `~/respaldo/tema_fondo_original.wav`).
2. Instalar `ffmpeg` si no está:

```bash
sudo apt-get install -y ffmpeg
```

3. Convertir a mono 22.05 kHz, PCM 16-bit:

```bash
ffmpeg -y -i Cementazo/src/main/resources/music/tema_fondo.wav \
  -ac 1 -ar 22050 -c:a pcm_s16le \
  /tmp/tema_fondo_mono.wav
mv /tmp/tema_fondo_mono.wav Cementazo/src/main/resources/music/tema_fondo.wav
```

4. Verificar formato y tamaño:

```bash
file Cementazo/src/main/resources/music/tema_fondo.wav
ls -lh Cementazo/src/main/resources/music/tema_fondo.wav
```

5. Escuchar el resultado y comprobar que el loop no tiene clics ni distorsión.
6. Anotar en `ASSETS.md` que se convirtió de estéreo 44.1 kHz a mono 22.05 kHz.

**Aceptación:**
- El WAV baja de ~27 MB a ~6-7 MB.
- `mvn package` produce un jar ≤ ~12 MB.
- La música se reproduce y hace loop en el juego.
- Si el audio no tiene licencia, **no se publica** hasta reemplazarlo.

**Verificación:**

```bash
cd Cementazo
mvn -q clean package
ls -lh target/Cementazo-1.0.jar
timeout 10s java -jar target/Cementazo-1.0.jar
```

### Fase 1.6 — Verificación de la Ronda 1

Checklist manual:

- [ ] Menú principal con título, récord, descargo y opciones.
- [ ] “Cómo jugar” se abre y se cierra.
- [ ] Enter inicia partida.
- [ ] ←/→ mueven al personaje.
- [ ] Atrapar cemento suma; fallar resta vida.
- [ ] Dinero duplica; esposas reducen.
- [ ] P pausa; P reanuda; ESC en pausa vuelve al menú.
- [ ] Cambiar de ventana pausa.
- [ ] M silencia/restaura.
- [ ] Fin de partida muestra puntaje y récord.
- [ ] R reinicia; Enter vuelve al menú.
- [ ] ESC en menú cierra.
- [ ] No hay excepciones en consola.
- [ ] `java -jar` funciona.
- [ ] El jar pesa ≤ 12 MB (si se comprimió el audio).
- [ ] `LEGAL.md` y `ASSETS.md` están completos.

Commits sugeridos:

```
legal: add satire disclaimer and asset/license audit
build: add Main-Class and Java 8 release
fix: remove dead code and repair HUD/background labels
feat: add pause, focus pause, mute and persistent high score
feat: add minimal menu and how-to-play screen
perf: compress background music to mono 22 kHz
```

---

## 3. RONDA 2 — Jugabilidad, feedback y progresión

**Objetivo:** que el juego se sienta bien, se entienda y tenga una curva. Al final, un jugador nuevo debe saber qué hacer sin leer el código, y el perico debe tener contraJuego.

**Duración orientativa:** 2-4 días.

> **Antes de refactorizar:** añadir tests de caracterización para puntaje, vidas e invulnerabilidad. Se pueden escribir ya con JUnit 5 aunque la ronda de tests formal sea la 3. Así se evita romper reglas existentes.

### Fase 2.1 — Input y movimiento continuo

#### R2-F1-T1: Teclas presionadas y movimiento por tick

**Archivos:** `ControlJuego.java`, `Area.java`.

**Pasos:**

1. Sustituir el movimiento directo en `keyPressed` por un `Set<Integer> teclasPresionadas`:
   - `keyPressed`: añadir código.
   - `keyReleased`: quitar código.
   - En cada tick de lógica: si contiene LEFT/RIGHT, mover.
2. Definir `Area.VELOCIDAD_JUGADOR = 420` px/s (ajustar tras probar; el valor actual efectivo es irregular).
3. `Area.moverIzquierda(double dt)` y `moverDerecha(double dt)`:
   - `nuevoX = x ± VELOCIDAD_JUGADOR * dt`.
   - Clamp entre 0 y `ANCHO - ancho`.
   - Cambiar sprite según dirección.
4. Ignorar `keyTyped`; mantener `keyReleased`.
5. Si se migra a Key Bindings (recomendado), usar `InputMap`/`ActionMap` con acciones `izquierda-on`, `izquierda-off`, etc., y `WHEN_IN_FOCUSED_WINDOW`. El `Set` se actualiza igual.

**Aceptación:** el movimiento empieza al instante, es continuo, no depende del auto-repeat y no se sale de la pantalla.

#### R2-F1-T2: Key Bindings en vez de KeyListener

**Archivos:** `GuiJuego.java`, `ControlJuego.java`.

**Pasos:**

1. Instalar las acciones sobre `panelJuego` o `rootPane`.
2. Mapear:
   - `LEFT`, `RIGHT` (pressed/released).
   - `P`, `ESCAPE`, `M`, `ENTER`, `R`, `SPACE` (pressed).
3. Eliminar `addKeyListener` y la implementación de `KeyListener` si ya no se usa.
4. Asegurar que el panel recibe el foco (`requestFocusInWindow`) y que el foco no se pierde al hacer clic.

**Aceptación:** las teclas funcionan aunque el foco esté en el panel; no hay doble movimiento por listeners duplicados.

### Fase 2.2 — Física independiente del framerate

#### R2-F2-T1: Velocidades en px/s

**Archivos:** `Area.java`, `ObjetoCaida.java`, `Perico.java`, `ControlJuego.java`.

**Pasos:**

1. `Area.actualizar(double dtSegundos)` en vez de `actualizar()` sin parámetro.
2. `ObjetoCaida.caer(dt)`: `y += velocidadY * dt`, con `velocidadY` en px/s.
3. `Perico.actualizar(dt)`: multiplicar velocidades por `dt`; gravedad en px/s².
4. Convertir la curva actual:
   - Caída: `2.8 px/frame * 60 = 168 px/s` base; máximo `8.4 * 60 = 504 px/s`.
   - Perico: `6.5 * 60 = 390 px/s`; escape `8 * 60 = 480 px/s`.
   - Gravedad: `0.85 px/frame² * 3600 = 3060 px/s²`; caída máxima `18 * 60 = 1080 px/s`.
5. Mantener el acumulador de `ControlJuego` y pasar `PASO / 1000.0` segundos por paso.
6. Ajustar `getVelocidadReal()` para devolver px/s y que la sensación sea similar.
7. Documentar en `AGENTS.md` que las velocidades ahora son px/s.

**Aceptación:** el juego se siente igual a 60 FPS; si se cambia temporalmente el FPS objetivo, la velocidad real no cambia.

### Fase 2.3 — Feedback de daño y atrapadas

#### R2-F3-T1: Parpadeo de invulnerabilidad

**Archivos:** `Personaje.java`, `Area.java`, `ControlJuego.java`.

**Pasos:**

1. Exponer `Area.estaInvulnerable()` o `getInvulnerabilidadMs()`.
2. En `dibujarJugando`, si está invulnerable, dibujar al personaje intermitentemente:
   - Visible si `(System.currentTimeMillis() / 100) % 2 == 0`.
   - O usar un contador de ticks para no depender del reloj.
3. Mantener la hitbox igual; solo cambia el dibujo.
4. Al recibir daño, sonido + flash.

**Aceptación:** el jugador ve claramente cuándo es invulnerable.

#### R2-F3-T2: Flash, shake y textos flotantes

**Archivos:** `ControlJuego.java`, `PanelJuego.java`.

**Pasos:**

1. Añadir una lista de `TextoFlotante` (texto, x, y, vidaMs) en `ControlJuego`.
2. Al atrapar: “+15” o “x2” o “/2” en la posición del objeto.
3. Al recibir golpe: “¡Golpe!” y flash rojo; shake de 150-250 ms.
4. Aplicar shake con `Graphics.translate` en `PanelJuego.paintComponent` o en el renderer, guardando el offset en `ControlJuego`.
5. Limpiar textos al morir/volver al menú.
6. No crear `Color` nuevos por frame; usar constantes.

**Aceptación:** cada evento importante tiene feedback visual; no hay parpadeos raros ni texto residual entre partidas.

#### R2-F3-T3: Sonidos de efecto

**Archivos:** `ReproductorAudio.java` o `modelo/Sonidos.java`, `src/main/resources/sfx/`, `ASSETS.md`.

**Pasos:**

1. Añadir 4-6 WAV cortos (≤100 KB cada uno): atrapada, dinero, esposas, golpe, robo, fin.
2. Si no hay assets con licencia, generarlos con `ffmpeg`/`sox` a partir de tonos simples o buscar CC0 y documentarlos.
3. Crear un reproductor de SFX que no bloquee el EDT:
   - Cargar clips al inicio.
   - Reproducir con `clip.stop(); clip.setFramePosition(0); clip.start();`
4. Respetar mute global.
5. Evitar reproducir el mismo SFX demasiadas veces por frame.

**Aceptación:** los SFX se oyen y se silencian con M; no hay lag perceptible.

### Fase 2.4 — Perico con contraJuego

#### R2-F4-T1: Lanzar sacos (mecánica principal)

**Archivos:** `modelo/Proyectil.java` (nuevo), `Area.java`, `ControlJuego.java`, `PanelJuego.java`.

**Diseño:**

- Al atrapar un saco de cemento, el jugador gana 1 carga (máximo 3).
- `SPACE` lanza una carga hacia arriba desde el centro del jugador.
- El proyectil sube a 700 px/s y desaparece al salir de pantalla.
- Si toca un perico:
  - El perico muere.
  - Si llevaba dinero, lo suelta.
  - +25 puntos.
- El HUD muestra cargas como “Sacos: ●●●”.

**Pasos:**

1. Crear `Proyectil extends Principal` con `velocidadY`, `actualizar(dt)`, `fueraDePantalla()`.
2. En `Area`:
   - `List<Proyectil> proyectiles`.
   - `int sacosCargados`.
   - `lanzarSaco()`: valida carga, crea proyectil, decrementa.
   - `actualizarProyectiles(dt)`: mueve, colisiona con pericos, elimina.
   - En `atrapar` de cemento: `sacosCargados = min(3, +1)`.
   - En `iniciarPartida`: resetear a 0.
3. En `ControlJuego`:
   - Input SPACE.
   - Dibujar proyectiles y HUD de cargas.
4. En `Perico`: método `morirPorProyectil()` o reutilizar `matar()`; asegurar que suelta el botín.
5. Si el tiempo no alcanza, implementar la variante mínima: al escapar el perico con dinero, restar 30 puntos y mostrar “¡Te robaron 30!”. Dejar el lanzamiento como TODO.

**Aceptación:** el jugador puede detener al perico intencionalmente; el HUD explica las cargas; no hay proyectiles residuales entre partidas.

#### R2-F4-T2: Aclarar el estado del perico

**Archivos:** `Perico.java`, `Area.java`.

**Pasos:**

1. Separar `ESCAPANDO_CON_DINERO` de `HUYENDO_SIN_BOTIN` (o renombrar).
2. `perderObjetivo()` solo debe ir a “con dinero” si `objetivo.estaRobado()`.
3. Si huye sin botín, no debe soltar dinero al morir.
4. Revisar `debeDesaparecer` para ambos estados.
5. Añadir test de que un perico sin botín no descuenta puntos.

**Aceptación:** el estado visual y la lógica coinciden; no hay “robos” sin dinero.

### Fase 2.5 — Progresión, fondos y dificultad

#### R2-F5-T1: Rotación de fondos por nivel

**Archivos:** `Area.java`, `ControlJuego.java`, `PanelJuego.java`, `Recursos.java`.

**Pasos:**

1. Definir `nivel` en `Area`:
   - `nivel = 1 + (int)(tiempoMs / 30000)` (cada 30 s) o por puntaje; elegir una y documentarla.
2. `indiceFondo = (nivel - 1) % Recursos.FONDOS.length`.
3. `nombreFondo = Recursos.NOMBRES_FONDO[indiceFondo]`.
4. `getArchivoFondo()` devuelve `Recursos.FONDOS[indiceFondo]` (ahora sí se usa).
5. `ControlJuego` detecta cambio de nivel y llama `panelJuego.setFondo(...)` solo cuando cambia.
6. Corregir `NOMBRES_FONDO[0]` si el asset es `bg_costa_rica.png` (usar “Costa Rica”).
7. HUD muestra “Nivel N” y el nombre del fondo.

**Aceptación:** los 7 fondos se usan, cambian sin recargar en cada frame y la etiqueta coincide.

#### R2-F5-T2: Curva de dificultad explícita

**Archivos:** `modelo/ReglasJuego.java` (nuevo) o `Area.java`.

**Pasos:**

1. Extraer constantes mágicas:
   - Vidas iniciales, velocidad base/máxima, aceleración, spawn inicial/mínimo, máximo de objetos, probabilidades de dinero/esposas, invulnerabilidad, puntos.
2. Documentar la curva actual y decidir si se mantiene continua o por niveles.
3. Si se usan niveles, tabla:

   | Nivel | Tiempo | Velocidad caída | Spawn | Máx. objetos |
   |---|---|---|---|---|
   | 1 | 0-30 s | 168 px/s | 1200 ms | 7 |
   | 2 | 30-60 s | 210 px/s | 1000 ms | 8 |
   | ... | ... | ... | ... | ... |

4. Mantener los valores actuales como base y ajustar tras jugar 5-10 partidas.
5. Añadir tests de que la velocidad y el spawn respetan los topes.

**Aceptación:** la dificultad es predecible y está documentada; no hay números mágicos dispersos.

#### R2-F5-T3: Selección de dificultad en el menú

**Archivos:** `GameState.java`, `ControlJuego.java`, `Area.java`, `Record.java`.

**Pasos:**

1. Enum `Dificultad { FACIL, NORMAL, DIFICIL }`.
2. Factores:
   - Fácil: 4 vidas, velocidad x0.85, spawn x1.20.
   - Normal: 3 vidas, x1.00, x1.00.
   - Difícil: 2 vidas, x1.15, x0.85.
3. Menú “Opciones” con selección y persistencia en `Preferences` (`cementazo.dificultad`).
4. `Area.iniciarPartida(Dificultad)` aplica los factores.
5. El récord puede ser global o por dificultad; documentar. Recomendado: global para simplificar; si se separa, mostrar “Récord (Normal)”.

**Aceptación:** la dificultad cambia de forma medible; la selección persiste; el récord no se corrompe.

### Fase 2.6 — Puntaje y estadísticas

#### R2-F6-T1: Reglas de puntaje claras

**Archivos:** `Area.java`, `ControlJuego.java`.

**Pasos:**

1. Centralizar constantes:
   - `PUNTOS_POR_SEGUNDO = 1`
   - `PUNTOS_CEMENTO = 15`
   - `PUNTOS_PERICO = 25`
   - `FACTOR_DINERO = 2`
   - `FACTOR_ESPOSAS = 0.5` (división entera)
2. Mantener `long`.
3. Añadir combo opcional:
   - Atrapadas consecutivas dentro de 2 s.
   - Multiplicador 1x, 1.25x, 1.5x, 2x (máximo).
   - Se reinicia al fallar o recibir golpe.
   - Documentar en “Cómo jugar”.
4. Revisar la regla actual de dinero: si el puntaje es 0, dar 20; si no, duplicar. Mantenerla o simplificar a “duplica, mínimo 20”. Documentar.
5. Asegurar que esposas nunca dejan puntaje negativo.

**Aceptación:** el código y la pantalla de ayuda dicen lo mismo; los tests cubren cada caso.

#### R2-F6-T2: Estadísticas de partida

**Archivos:** `Area.java`, `ControlJuego.java`.

**Pasos:**

1. Añadir contadores:
   - `tiempoSobrevividoMs`
   - `cementoAtrapado`
   - `dineroAtrapado`
   - `esposasAtrapadas`
   - `cementoPerdido`
   - `pericosNeutralizados`
2. Mostrar en fin de partida:
   - Puntos, récord.
   - Tiempo, atrapadas, perdidas, pericos.
3. Resetear en `iniciarPartida`.

**Aceptación:** las estadísticas cuadran con la partida y no persisten entre partidas.

### Fase 2.7 — Menú v2 y pulido de pausa

#### R2-F7-T1: Menú con navegación completa

**Archivos:** `ControlJuego.java`, `GameState.java`, `PanelJuego.java`.

**Pasos:**

1. Estados: `MENU_PRINCIPAL`, `COMO_JUGAR`, `OPCIONES`, `JUGANDO`, `PAUSA`, `FIN_PARTIDA`.
2. Menú con selección vertical:
   - Jugar.
   - Cómo jugar.
   - Opciones.
   - Créditos.
   - Salir.
3. Flechas arriba/abajo + Enter; Esc vuelve.
4. “Opciones”: mute, dificultad, borrar récord (con confirmación).
5. “Créditos”: autor, licencias, descargo, versión.
6. No usar componentes Swing nuevos; dibujar en `ControlJuego` para mantener el estilo actual.

**Aceptación:** todas las opciones son accesibles solo con teclado; no hay estados sin salida.

#### R2-F7-T2: Pausa con opciones

**Archivos:** `ControlJuego.java`.

**Pasos:**

1. En `PAUSA`, mostrar:
   - Continuar (P).
   - Reiniciar (R) con confirmación o transición directa.
   - Menú (Esc).
   - Mute (M).
2. Decidir si reiniciar desde pausa pide confirmación; si no, mantener R directo.
3. Pausar música y SFX.

**Aceptación:** se puede reanudar, reiniciar o volver al menú sin perder el estado de forma inconsistente.

### Fase 2.8 — Verificación de la Ronda 2

Checklist manual:

- [ ] Movimiento suave con teclas mantenidas.
- [ ] El personaje no se sale de la pantalla.
- [ ] Pausa/reanudación conserva posición, objetos, puntaje y vidas.
- [ ] Perder foco pausa.
- [ ] Invulnerabilidad visible.
- [ ] Feedback al atrapar/fallar/robar.
- [ ] SFX y mute.
- [ ] Perico se puede detener con sacos.
- [ ] Fondos rotan por nivel.
- [ ] Dificultades cambian vidas/velocidad/spawn.
- [ ] Puntaje y estadísticas coinciden con la ayuda.
- [ ] Récord persiste.
- [ ] Menú completo sin callejones sin salida.

Commits sugeridos:

```
feat: continuous keyboard movement and key bindings
refactor: frame-rate independent physics in px/s
feat: hit feedback, invulnerability blink and floating text
feat: throwable cement sacks to counter the perico
feat: level backgrounds and explicit difficulty curve
feat: long score, stats and difficulty selection
feat: full menu and pause options
```

---

## 4. RONDA 3 — Rendimiento, calidad, pruebas y publicación

**Objetivo:** dejar el proyecto mantenible, probado, documentado y listo para un release público. Al final, un clon limpio debe compilar, testear y ejecutarse sin pasos ocultos.

**Duración orientativa:** 3-5 días.

### Fase 3.1 — Assets y caché

#### R3-F1-T1: Eliminar duplicados y decidir fondos

**Archivos:** `src/main/resources/img/`, `ASSETS.md`, `Recursos.java`.

**Pasos:**

1. Comparar hashes (ya verificado: son idénticos):
   - `cemento.png` = `item_saco_cemento.png`
   - `dinero.png` = `item_billetes.png`
   - `esposas.png` = `item_grilletes.png`
   - `fondo.png` = `bg_costa_rica.png`
   - `pDerecha.png` = `sprite_personaje_derecha.png`
   - `pIzquierda.png` = `sprite_personaje_izquierda.png`
2. Borrar los 6 duplicados **solo si** `grep` confirma que no se referencian y el build sigue verde.
3. Si la rotación de fondos de R2 está implementada, conservar los 7 `bg_*.png`; si no, eliminarlos del jar y de `Recursos.FONDOS`.
4. Actualizar `ASSETS.md`.

**Aceptación:** no hay assets duplicados; el jar baja de tamaño; no hay imágenes rotas.

#### R3-F1-T2: Caché de recursos

**Archivos:** `Recursos.java`, `PanelJuego.java`, `Area.java`.

**Pasos:**

1. Añadir caché estática:

```java
private static final Map<String, ImageIcon> CACHE = new HashMap<>();

public static ImageIcon cargarOColor(String nombre, Color color, int ancho, int alto) {
    String clave = nombre + "|" + ancho + "x" + alto;
    ImageIcon existente = CACHE.get(clave);
    if (existente != null) return existente;
    // ... cargar/escalar ...
    CACHE.put(clave, resultado);
    return resultado;
}
```

2. Cachear también los volteos horizontales (`voltearHorizontal`) si se piden varias veces.
3. En `PanelJuego.setFondo`, no recargar si el nombre y tamaño no cambiaron.
4. Evitar que `ControlJuego` llame `setFondo` en cada frame; solo al cambiar de fondo.

**Aceptación:** el fondo se carga una vez por combinación nombre+tamaño; el inicio y el cambio de nivel son instantáneos.

#### R3-F1-T3: Escalado de pixel art

**Archivos:** `Recursos.java`.

**Pasos:**

1. Añadir un método `escalarPixelArt(...)` con `RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR`.
2. Usarlo para `sprite_personaje_*`, `item_*` y `perico_*`; mantener bilineal para fondos.
3. Comparar visualmente; si el pixel art se ve mejor con bilineal a cierto tamaño, documentarlo.

**Aceptación:** los sprites no se ven borrosos; los fondos no se pixelan innecesariamente.

### Fase 3.2 — Rendimiento y allocations

#### R3-F2-T1: Evitar copias por frame

**Archivos:** `Area.java`, `ControlJuego.java`.

**Pasos:**

1. `getCaidas()` y `getPericos()` devuelven `List` no modificable cacheada, no una copia nueva por llamada.
2. Alternativa: `Area.dibujar(Graphics)` itera internamente; si se hace, mover también el dibujo de proyectiles.
3. Mantener la encapsulación: el controlador no debe modificar las listas.

**Aceptación:** no se crean `ArrayList` nuevos en cada `paintComponent`; el comportamiento es idéntico.

#### R3-F2-T2: Colores, fuentes y métricas

**Archivos:** `ControlJuego.java`, `Principal.java`, `Recursos.java`.

**Pasos:**

1. Mover colores a constantes (`Color` inmutables) en `Recursos` o `Colores`.
2. Reutilizar `Font` (ya están como constantes).
3. Cachear `FontMetrics` por `Font` después del primer pintado; o medir solo cuando cambie el texto.
4. Activar `RenderingHints` de antialiasing para texto y formas.

**Aceptación:** no hay `new Color(...)` en métodos de pintado; el texto se ve suave.

#### R3-F2-T3: Medir FPS real

**Archivos:** `PanelJuego.java`, `ControlJuego.java`.

**Pasos:**

1. Incrementar un contador en `PanelJuego.paintComponent`.
2. Exponer `getFramesPintados()` o un `FpsMeter` que calcule por segundo.
3. Mostrar el FPS real en el HUD o eliminarlo del HUD y dejarlo solo en modo debug (tecla F3).
4. No confundir pasos de lógica con frames pintados.

**Aceptación:** el número del HUD corresponde a pintados reales; no hay confusión.

### Fase 3.3 — Tests

#### R3-F3-T1: Configurar JUnit 5

**Archivos:** `pom.xml`, `src/test/java/...`.

**Pasos:**

1. Añadir dependencia `junit-jupiter` 5.11.x, scope `test`.
2. Añadir `maven-surefire-plugin` 3.5.x.
3. Configurar `java.awt.headless=true` para tests.
4. Crear `src/test/java/ucr/ac/cr/cementazo/modelo/AreaTest.java`.

**Aceptación:** `mvn test` ejecuta tests y no abre ventanas.

#### R3-F3-T2: Inyectar `Random` y helpers de test

**Archivos:** `Area.java`.

**Pasos:**

1. Añadir constructor `Area(Random random)` o `Area(long semilla)`.
2. El constructor sin argumentos llama al anterior con `new Random()`.
3. Añadir métodos package-private para tests:
   - `void agregarObjetoParaPrueba(ObjetoCaida objeto)`
   - `void actualizarParaPrueba(double ms)` o `void avanzar(double segundos)`
   - `List<ObjetoCaida> caidasParaPrueba()`
4. No exponer estos métodos como API pública.

**Aceptación:** los tests pueden colocar objetos y avanzar tiempo de forma determinista.

#### R3-F3-T3: Casos de test obligatorios

**Archivos:** `AreaTest.java`, `PericoTest.java`, `PrincipalTest.java` (si aplica).

Casos:

1. **Inicio:** 3 vidas, puntaje 0, listas vacías, tiempo 0, sacos 0.
2. **Puntaje cemento:** +15.
3. **Puntaje dinero:** con 0 → 20; con 100 → 200.
4. **Puntaje esposas:** con 100 → 50; nunca negativo.
5. **Vida:** cemento que cae fuera resta 1 vida.
6. **Invulnerabilidad:** dos cementos seguidos en <1 s restan solo 1 vida; tras 1 s, el siguiente resta.
7. **Fin:** al llegar a 0 vidas, `estaTerminado()` es true.
8. **Máximo de objetos:** no se superan 7 (o el valor configurado).
9. **Dificultad:** la velocidad y el spawn cambian con el tiempo y respetan topes.
10. **Perico:** roba dinero, escapa y el objeto se elimina; si muere, el dinero se suelta.
11. **Proyectil:** lanzar consume carga; colisionar con perico lo mata y da puntos; sale de pantalla y se elimina.
12. **Puntaje long:** valores grandes no desbordan.
13. **Colisión:** `tocaLaTablaDe` en bordes (izquierda, derecha, arriba, abajo).

**Aceptación:** todos los tests pasan y cubren las reglas críticas.

### Fase 3.4 — CI

#### R3-F4-T1: GitHub Actions

**Archivos:** `.github/workflows/build.yml` (dentro de `Cementazo/`).

**Contenido base:**

```yaml
name: build
on:
  push:
    branches: [main]
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - run: mvn -B verify
      - uses: actions/upload-artifact@v4
        with:
          name: Cementazo-jar
          path: target/Cementazo-*.jar
```

**Pasos:**

1. Asegurar que el workflow está en la raíz del repo (`Cementazo/.github/...`).
2. Ejecutar `mvn -B verify` en local antes de subir.
3. Si se quiere smoke test GUI, usar `xvfb-run` con `timeout` y aceptar 124; no bloquear el CI por una ventana.

**Aceptación:** el CI pasa en un PR de prueba; el artefacto se descarga.

### Fase 3.5 — Documentación, licencias y release

#### R3-F5-T1: README completo

**Archivos:** `README.md`.

**Contenido mínimo:**

- Qué es el juego y aviso de sátira.
- Capturas/GIF.
- Requisitos (JDK 21 para compilar; runtime incluido si se usa jpackage).
- Controles.
- Cómo compilar y ejecutar.
- Cómo correr tests.
- Estructura del proyecto.
- Licencia del código y de los assets.
- Créditos y descargo.
- Estado del proyecto / roadmap.

#### R3-F5-T2: jpackage

**Archivos:** `dist/` (no commitear binarios grandes), `README.md`, `scripts/package.sh` (opcional).

**Pasos:**

1. Crear un directorio de entrada solo con el jar:

```bash
cd Cementazo
mvn -q clean package
rm -rf dist-input dist
mkdir -p dist-input
cp target/Cementazo-1.0.jar dist-input/
```

2. Generar app image:

```bash
jpackage \
  --type app-image \
  --name Cementazo \
  --input dist-input \
  --main-jar Cementazo-1.0.jar \
  --main-class ucr.ac.cr.cementazo.vista.GuiJuego \
  --dest dist \
  --java-options "-Xmx512m"
```

3. En Linux, para `.deb`:

```bash
sudo apt-get install -y fakeroot dpkg-dev
jpackage --type deb --name Cementazo --input dist-input \
  --main-jar Cementazo-1.0.jar \
  --main-class ucr.ac.cr.cementazo.vista.GuiJuego \
  --dest dist --linux-shortcut
```

4. Para Windows/macOS, ejecutar `jpackage` en ese SO (no se puede cruzar desde Linux sin herramientas extra).
5. Añadir icono si existe (`--icon ruta.png` o `.ico`/`.icns`).
6. Probar en una máquina sin Java instalado.

**Aceptación:** un usuario sin JDK puede abrir el juego desde el app image/instalador.

#### R3-F5-T3: Release y tienda

**Pasos:**

1. Subir versión en `pom.xml` (por ejemplo `1.1.0`).
2. Crear `CHANGELOG.md`.
3. Generar zip con:
   - Instalador/app image.
   - `README.md`, `LEGAL.md`, `LICENSE`, `LICENSE-ASSETS`.
   - Checksum SHA-256.
4. Si se publica en itch.io/Steam:
   - Descargo visible en la página.
   - Declarar contenido sensible/edad si aplica.
   - No afirmar hechos reales.
   - Confirmar derechos de todos los assets.
   - Revisar las reglas de la plataforma (§7.7).
5. Guardar una copia del build y de los assets con fecha.

**Aceptación:** release reproducible; página de tienda sin afirmaciones legales riesgosas; assets con licencia.

### Fase 3.6 — Gate legal de release

Antes de pulsar “publicar”, completar:

- [ ] `LEGAL.md` y descargo en menú/README/tienda.
- [ ] Sin nombres, caras, logos ni instituciones reales identificables.
- [ ] Sin afirmaciones de hechos delictivos sobre personas reales.
- [ ] Todos los assets con autor/licencia en `ASSETS.md`.
- [ ] `LICENSE` y `LICENSE-ASSETS`.
- [ ] Búsqueda de marca del título si es comercial.
- [ ] Sin datos personales en el juego, capturas o créditos.
- [ ] Si hay ranking online: política de privacidad y evaluación de Ley 8968/PRODHAB.
- [ ] Revisión de un abogado costarricense si hay monetización o alguien se siente aludido.
- [ ] Reglas de Steam/itch.io revisadas.

---

## 5. Después de las 3 rondas (mencionar, no ejecutar ahora)

Estas ideas quedan fuera del alcance para no inflar el proyecto:

- **Ranking online**: requiere servidor, validación, moderación, política de privacidad y posible inscripción de base de datos ante PRODHAB (Ley 8968).
- **Logros/cloud saves de Steam**: integración con Steamworks; no usar si no se publica ahí.
- **Localización**: inglés y español; centralizar textos.
- **Accesibilidad**: remapeo de teclas, volumen, modo daltónico, texto escalable.
- **Gamepad**: `JInput` o SDL; añade dependencia.
- **Más contenido**: jefes, power-ups, modo infinito, niveles temáticos.
- **Editor de niveles / mods**: gran aumento de alcance.
- **Multijugador**: no recomendado para esta base Swing.
- **Migrar a Java 17/21**: si se quiere dejar Java 8; actualizar `release` y probar.
- **Migrar a libGDX**: solo si el juego crece mucho; la arquitectura actual es suficiente.
- **Firma de código/notarización**: necesario para macOS/Windows si se distribuye fuera de tiendas.
- **Analítica**: evitar o usar una solución privacy-first con consentimiento y aviso.
- **Música/arte original**: ideal para eliminar riesgo de licencias y mejorar identidad.

---

## 6. Apéndices

### 6.1 Mapa de estados

```
MENU_PRINCIPAL
 ├─ Enter ─────────────► JUGANDO
 ├─ C ─────────────────► COMO_JUGAR
 ├─ O ─────────────────► OPCIONES
 ├─ Esc ───────────────► cerrar
 └─ Créditos ──────────► CREDITOS

JUGANDO
 ├─ P / Esc ───────────► PAUSA
 ├─ foco perdido ──────► PAUSA
 └─ vidas = 0 ─────────► FIN_PARTIDA

PAUSA
 ├─ P ─────────────────► JUGANDO
 ├─ R ─────────────────► JUGANDO (reinicio)
 ├─ M ─────────────────► mute on/off
 └─ Esc ───────────────► MENU_PRINCIPAL

FIN_PARTIDA
 ├─ R ─────────────────► JUGANDO
 └─ Enter / Esc ───────► MENU_PRINCIPAL
```

### 6.2 Controles definitivos

| Tecla | Acción |
|---|---|
| ← / → | Mover |
| Enter | Confirmar / empezar |
| P | Pausa / reanudar |
| Esc | Pausa en partida; volver en menús; salir en menú principal |
| M | Silenciar / restaurar |
| R | Reiniciar en fin de partida o pausa |
| Espacio | Lanzar saco (Ronda 2) |
| C | Cómo jugar |
| O | Opciones |
| F3 | Mostrar/ocultar FPS (debug, opcional) |

### 6.3 Fórmulas y valores de referencia

- Paso fijo: `1.0 / 60.0` s.
- Jugador: 420 px/s (ajustar).
- Caída: 168 px/s base, 504 px/s máximo.
- Spawn: 1200 ms inicial, 420 ms mínimo.
- Invulnerabilidad: 1000 ms.
- Vidas: 3 (Normal).
- Puntaje: +1/s; cemento +15; dinero x2 (mínimo 20); esposas /2; perico +25.
- Límite de objetos: 7 (o tabla de dificultad).
- Cargas de saco: 3 máximo.

### 6.4 Checklist de QA manual

- [ ] Arranque en frío.
- [ ] Arranque con `java -jar`.
- [ ] Menú → jugar → pausa → reanudar.
- [ ] Perder foco → pausa.
- [ ] Mute on/off.
- [ ] Atrapar cada tipo de objeto.
- [ ] Fallar cemento.
- [ ] Perder las 3 vidas.
- [ ] Reiniciar.
- [ ] Volver al menú.
- [ ] Récord tras cerrar y abrir.
- [ ] Cambio de fondo por nivel.
- [ ] Perico roba y escapa.
- [ ] Perico muere por saco.
- [ ] Ventana en 1280x800.
- [ ] Sin excepciones en consola.

### 6.5 Registro de riesgos

| Riesgo | Probabilidad | Impacto | Mitigación |
|---|---|---|---|
| Demanda por difamación/calumnia | Media si se nombran personas | Alto | Ficcionalizar, descargo, no afirmar hechos, abogado |
| Reclamo por imagen | Media si se usa rostro real | Alto | No usar imágenes reales; arte genérico |
| Reclamo de copyright/música | Alta si el WAV no es propio | Alto | Auditar `ASSETS.md`; reemplazar |
| Conflicto de marca | Baja/media | Medio | Búsqueda en Registro; renombrar si hace falta |
| Plataforma rechaza el juego | Media | Medio | Cumplir reglas; descargo; no difamación |
| Pérdida de datos/identidad | Baja | Medio | Seudónimo, noreply, no datos personales |
| Rendimiento pobre | Baja | Bajo | Medir, cachear, no sobreoptimizar |
| Scope creep | Alta | Medio | Timebox por fase; posponer ideas del §5 |

### 6.6 Registro de decisiones (ADR breve)

Crear `docs/decisiones.md` y anotar:

1. ¿Se mantiene el título “Cementazo”? ¿Por qué?
2. ¿Qué licencia tiene el código?
3. ¿Qué licencia tienen arte/música?
4. ¿Se publica con identidad real o seudónimo?
5. ¿Se monetiza?
6. ¿Cómo se resuelve el perico (sacos vs penalización)?
7. ¿Récord global o por dificultad?

Cada decisión: fecha, contexto, opción elegida, consecuencias.

---

## 7. Legal: información general y checklist

> **No es asesoría legal.** Es un resumen para planificar. La interpretación final corresponde a un abogado costarricense.

### 7.1 Libertad de expresión y sátira

La Constitución Política de Costa Rica protege la manifestación de opiniones y la comunicación de pensamientos sin censura previa, pero establece responsabilidad por abusos:

- **Art. 28:** “Nadie puede ser inquietado ni perseguido por la manifestación de sus opiniones ni por acto alguno que no infrinja la ley.”
- **Art. 29:** “Todos pueden comunicar sus pensamientos de palabra o por escrito, y publicarlos sin previa censura; pero serán responsables de los abusos que cometan en el ejercicio de este derecho…”

La sátira política tiene protección constitucional, pero no es un permiso absoluto para dañar el honor o la imagen de terceros. El contexto, el tono y la condición de figura pública importan. Para reducir riesgo: ficcionalizar, exagerar de forma evidente, no presentar acusaciones como hechos y añadir descargo.

**Fuentes:**
- Constitución, arts. 28-29: <https://www.tse.go.cr/pdf/normativa/constitucion.pdf>
- PGR, art. 28: <http://www.pgrweb.go.cr/scij/busqueda/normativa/normas/nrm_articulo.aspx?nValor1=1&nValor2=871&nValor3=113368&nValor5=4876&param1=NRA>

### 7.2 Honor, difamación y calumnia

El Código Penal tipifica:

- **Art. 145:** injuria (ofender la dignidad o decoro).
- **Art. 146:** difamación (deshonrar o afectar la reputación).
- **Art. 147:** calumnia (atribuir falsamente un delito).
- **Art. 149:** excepción por prueba de la verdad, con condiciones de interés público y ausencia de ánimo de ofender.
- **Art. 151:** no son punibles los juicios desfavorables de crítica literaria, artística, histórica, científica o profesional, ni el concepto desfavorable en cumplimiento de un deber o ejercicio de un derecho, siempre que no haya propósito ofensivo.
- **Art. 153:** difamación de personas jurídicas.

**Implicación práctica:** la sátira sobre el sistema y los arquetipos es más segura que atribuir delitos concretos a personas identificables. Si se menciona un hecho real, documentar la fuente y la verdad; evitar afirmaciones no probadas.

**Fuentes:**
- Código Penal (texto): <https://proledi.ucr.ac.cr/codigo-penal/>
- PGR, art. 145: <https://pgrweb.go.cr/scij/Busqueda/Normativa/Normas/nrm_articulo.aspx?nValor1=1&nValor2=5027&nValor3=96389&nValor5=23925&param1=NRA>

### 7.3 Derecho a la propia imagen

El Código Civil regula la imagen:

- **Art. 47:** la fotografía o imagen de una persona no puede publicarse, reproducirse, exponerse ni venderse sin su consentimiento, salvo que la reproducción esté justificada por la notoriedad de la persona, la función pública, necesidades de justicia/policía, o hechos/acontecimientos/ceremonias de interés público o que tengan lugar en público.
- **Art. 48:** medida cautelar para suspender la publicación/exposición/venta de imágenes no consentidas.

**Implicación práctica:** no usar retratos, caricaturas reconocibles ni voces imitadas de personas reales sin consentimiento o revisión legal. El arte actual es genérico; mantenerlo así. La excepción de “figura pública” no equivale a luz verde para usar su imagen en un producto comercial.

**Fuente:** Código Civil, arts. 47-48: <https://pgrweb.go.cr/scij/Busqueda/Normativa/Normas/nrm_texto_completo.aspx?nValor1=1&nValor2=15437&nValor3=0&param1=NRTC&strTipM=TC>

### 7.4 Derechos de autor y parodia

La Ley 6683 protege programas de cómputo, música, arte, textos y obras audiovisuales:

- **Art. 1:** protege las producciones intelectuales originales; incluye programas de cómputo.
- **Art. 8:** quien adapte, traduzca, modifique, parodie o extracte la sustancia de una obra de dominio privado necesita autorización del titular.
- **Art. 16:** derechos exclusivos de reproducción, adaptación, comunicación pública y distribución.
- **Art. 70:** permite citar pasajes, con límites; no es una excepción general de parodia.
- **Arts. 101-102:** la protección nace con la creación; el registro es declarativo, no obligatorio.

**Implicación práctica:** “es parodia” no habilita usar música, personajes o arte ajenos. Usar assets propios, CC0 o con licencia compatible. Documentar autoría y licencia. Si se usa una cita breve, que sea limitada y con atribución.

**Fuentes:**
- Ley 6683 (WIPO): <https://www.wipo.int/wipolex/es/legislation/details/21963>
- Ley 6683 (PDF): <https://www.tra.go.cr/sites/default/files/2021-09/Ley%206683%20de%20Derechos%20de%20Autor%20y%20Derechos%20Conexos.pdf>

### 7.5 Marcas y título

La Ley 7978 regula marcas y signos distintivos:

- **Art. 7(i):** no se registra como marca un signo que ofenda o ridiculice a personas, ideas, religiones o símbolos nacionales.
- **Art. 8(f):** no se registra un signo que afecte el derecho de personalidad de un tercero (nombre, firma, título, seudónimo, imagen, retrato), salvo consentimiento.
- **Art. 8(g):** protege el nombre, imagen o prestigio de una colectividad.
- **Art. 25:** derechos conferidos por el registro.

**Implicación práctica:** “Cementazo” puede ser un título descriptivo/asociado a un caso real; antes de vender, buscar antecedentes y evaluar renombrar. No usar logos de empresas reales ni nombres de instituciones. Si el título evoca a una persona real, aumenta el riesgo.

**Fuente:** Ley 7978 (WIPO): <https://www.wipo.int/wipolex/es/legislation/details/9130>

### 7.6 Datos personales

La Ley 8968 protege datos personales:

- **Art. 2:** aplica a datos en bases automatizadas o manuales de organismos públicos o privados.
- **Art. 21:** toda base de datos administrada con fines de distribución, difusión o comercialización debe inscribirse en el registro de PRODHAB.
- **Art. 31(e):** tratar datos sin inscripción cuando corresponde es falta gravísima.
- **Art. 33:** canon anual de regulación (USD 200) para bases inscribibles.

**Implicación práctica:** el juego actual no recolecta datos; el récord local con `Preferences` es doméstico. Si se añade ranking online, cuentas o analítica con datos personales, evaluar inscripción, política de privacidad, consentimiento, seguridad y retención. Consultar a PRODHAB.

**Fuentes:**
- Ley 8968: <https://pgrweb.go.cr/scij/Busqueda/Normativa/Normas/nrm_texto_completo.aspx?nValor1=1&nValor2=70975&nValor3=85989&param1=NRTC&strTipM=TC>
- PRODHAB FAQ: <https://prodhab.go.cr/preguntasfrecuentes.aspx>

### 7.7 Plataformas de distribución

- **Steam:** prohíbe contenido difamatorio, contenido sin derechos y contenido que viole leyes; exige Content Survey. <https://partner.steamgames.com/steamdirect/>
- **itch.io:** exige derechos sobre el contenido, prohíbe difamación/acoso y permite clasificar contenido adulto. <https://itch.io/docs/legal/terms> y <https://itch.io/docs/creators/quality-guidelines>

**Implicación práctica:** la página de tienda debe incluir el descargo, no afirmar hechos reales y declarar contenido sensible si aplica.

### 7.8 Identidad, privacidad y “persecución política”

Si el objetivo es reducir exposición personal:

1. **No incluir** nombre real, correo, teléfono, dirección ni fotos en el juego, README, capturas o créditos.
2. **Git:** los commits actuales muestran `francis <francismelendez134@gmail.com>`. Para futuros commits:
   ```bash
   git config user.name "Alias"
   git config user.email "12345678+alias@users.noreply.github.com"
   ```
3. **Historial:** si se quiere borrar el correo ya publicado, se puede reescribir con `git filter-repo` y `force-push`. Es destructivo, cambia hashes y requiere coordinar con colaboradores; hacer backup y confirmar con el dueño. No ejecutar automáticamente.
4. **Cuentas:** usar una cuenta de GitHub/itch.io separada de la identidad personal si se desea seudonimato. Recordar que el correo de pago, la tienda y el hosting pueden revelar identidad.
5. **Contenido:** la mejor reducción de riesgo legal es ficcionalizar y no afirmar hechos. El anonimato no elimina la responsabilidad legal.
6. **Si hay una amenaza o denuncia:** conservar capturas/fuentes, no borrar evidencia, no responder públicamente de forma impulsiva y consultar a un abogado. Usar los mecanismos de la plataforma.

### 7.9 Fuentes y fecha de revisión

Revisado el 2026-11-21. Verificar vigencia antes de un release; las leyes y reglas de plataforma cambian.

- Constitución Política de Costa Rica, arts. 28-29.
- Código Penal, arts. 145-153.
- Código Civil, arts. 47-48.
- Ley 6683, Derechos de Autor y Derechos Conexos, arts. 1, 8, 16, 70, 101-102.
- Ley 7978, Marcas y Otros Signos Distintivos, arts. 7, 8, 25.
- Ley 8968, Protección de la Persona frente al Tratamiento de sus Datos Personales, arts. 2, 21, 31, 33.
- Steam Direct Rules; itch.io Terms of Service/Quality Guidelines.

---

## 8. Orden de ejecución recomendado

1. **Ronda 1 completa**, con gate legal.
2. **Ronda 2**, empezando por tests de caracterización y luego input/física.
3. **Ronda 3**, empezando por assets/caché y terminando en CI/release.
4. No empezar Ronda 3 antes de que la Ronda 2 esté estable: empaquetar un juego que aún cambia de mecánicas genera retrabajo.

## 9. Definición de terminado del proyecto

- `mvn -B clean verify` verde en local y CI.
- `java -jar target/Cementazo-*.jar` funciona.
- Instalador/app image probado en una máquina sin Java.
- Tests de reglas críticas pasando.
- `README.md`, `LEGAL.md`, `ASSETS.md`, `LICENSE`, `LICENSE-ASSETS` completos.
- Sin nombres, caras, logos ni afirmaciones de hechos sobre personas reales.
- Assets con licencia documentada.
- Sin datos personales en el repo.
- Release con checksum y notas.
