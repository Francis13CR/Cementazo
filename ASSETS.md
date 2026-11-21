# Inventario de assets y licencias

> **Fecha:** 2026-11-21  
> **Alcance:** todos los archivos de `src/main/resources/` que se empaquetan en el jar.  
> **Regla:** ningún release público hasta que cada asset tenga autor, licencia y
> permiso verificables. Este archivo es un inventario, no una licencia.

## Estado general

- **Código:** MIT, ver `LICENSE`.
- **Imágenes y sprites:** el dueño del proyecto declara que son de su autoría.
  Se distribuyen con el juego con todos los derechos reservados; ver
  `LICENSE-ASSETS`. La autoría no se ha verificado con documentos externos.
- **Música:** obra de un tercero. El dueño declara que cuenta con permiso del
  autor. La constancia escrita y el alcance deben completarse en
  `docs/permiso-musica.md` **antes de un release comercial**.
- **Assets duplicados:** hay 6 archivos idénticos a otros; se eliminarán en la
  Ronda 3 si siguen sin referenciarse.

## Inventario

| Archivo | Tipo | Autor | Licencia / permiso | Estado | Notas |
|---|---|---|---|---|---|
| `img/bg_costa_rica.png` | Fondo | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Fondo inicial |
| `img/bg_heredia.png` | Fondo | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Rotación en Ronda 2 |
| `img/bg_cartago.png` | Fondo | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Rotación en Ronda 2 |
| `img/bg_limon.png` | Fondo | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Rotación en Ronda 2 |
| `img/bg_puntarenas.png` | Fondo | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Rotación en Ronda 2 |
| `img/bg_guanacaste.png` | Fondo | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Rotación en Ronda 2 |
| `img/bg_alajuela.png` | Fondo | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Rotación en Ronda 2 |
| `img/sprite_personaje_derecha.png` | Sprite | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Jugador mirando a la derecha |
| `img/sprite_personaje_izquierda.png` | Sprite | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Volteo del anterior |
| `img/item_saco_cemento.png` | Sprite | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Cemento |
| `img/item_billetes.png` | Sprite | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Dinero |
| `img/item_grilletes.png` | Sprite | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Esposas |
| `img/perico_vuelo.png` | Sprite | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Perico volando |
| `img/perico_robando.png` | Sprite | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Perico con botín |
| `img/perico_caida.png` | Sprite | Autor del proyecto (declarado) | Todos los derechos reservados | OK para el juego | Perico derrotado |
| `music/tema_fondo.wav` | Música | Tercero (nombre/alias pendiente) | Permiso del autor; ver `docs/permiso-musica.md` | **PERMISO DECLARADO; RELEASE COMERCIAL BLOQUEADO** | 27 MB; comprimir en Ronda 1.5 |
| `img/cemento.png` | Duplicado | Autor del proyecto (declarado) | Todos los derechos reservados | No referenciado | Idéntico a `item_saco_cemento.png`; eliminar en Ronda 3 |
| `img/dinero.png` | Duplicado | Autor del proyecto (declarado) | Todos los derechos reservados | No referenciado | Idéntico a `item_billetes.png`; eliminar en Ronda 3 |
| `img/esposas.png` | Duplicado | Autor del proyecto (declarado) | Todos los derechos reservados | No referenciado | Idéntico a `item_grilletes.png`; eliminar en Ronda 3 |
| `img/fondo.png` | Duplicado | Autor del proyecto (declarado) | Todos los derechos reservados | No referenciado | Idéntico a `bg_costa_rica.png`; eliminar en Ronda 3 |
| `img/pDerecha.png` | Duplicado | Autor del proyecto (declarado) | Todos los derechos reservados | No referenciado | Idéntico a `sprite_personaje_derecha.png`; eliminar en Ronda 3 |
| `img/pIzquierda.png` | Duplicado | Autor del proyecto (declarado) | Todos los derechos reservados | No referenciado | Idéntico a `sprite_personaje_izquierda.png`; eliminar en Ronda 3 |

## Verificación de duplicados

Comprobado por hash MD5; son idénticos:

- `cemento.png` = `item_saco_cemento.png`
- `dinero.png` = `item_billetes.png`
- `esposas.png` = `item_grilletes.png`
- `fondo.png` = `bg_costa_rica.png`
- `pDerecha.png` = `sprite_personaje_derecha.png`
- `pIzquierda.png` = `sprite_personaje_izquierda.png`

## Pendientes antes de publicar

- [ ] Completar `docs/permiso-musica.md` con nombre del autor, alcance, firma y
      fecha.
- [ ] Confirmar si el permiso de la música cubre compresión/modificación del
      archivo y distribución en las plataformas previstas.
- [ ] Añadir crédito de la música en el menú/README si el permiso lo exige.
- [ ] Eliminar duplicados no referenciados en Ronda 3.
- [ ] Registrar cualquier asset nuevo en esta tabla antes de commitearlo.
