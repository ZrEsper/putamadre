# Dungeons Iso + TACZ: cámara de supervivencia .1

[Descargar el JAR](../mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.1.jar).

Con Minecraft cerrado, sustituye **solo Dungeons Iso** en `mods` del cliente. No dejes su versión anterior junto a esta. Conserva TACZ hotfix-r7, Zombie Remains Loot, Quietly y sus dependencias. El parche mantiene el identificador y versión internos originales de Dungeons Iso. No requiere cambios en el mundo ni en el servidor.

## Comportamiento

- Con la vista isométrica activa y un arma TACZ, muestra una mira pequeña sobre el primer obstáculo o entidad en la dirección del arma. Usa la rotación del jugador que TACZ envía al servidor y la matriz de proyección que Dungeons Iso entrega al renderizador, tanto en perspectiva como en modo ortográfico. Puede apuntar hacia arriba y abajo y conserva los cambios de retroceso de TACZ. Indica la línea de tiro; la dispersión, penetración y caída de la bala siguen siendo las de TACZ.
- Cada 150 ms compara ocho ángulos alrededor del jugador mediante cinco rayos por ángulo. Solo cambia si la vista actual está obstruida y otra mejora claramente; prefiere giros pequeños, mantiene el rumbo elegido hasta alcanzarlo y limita el giro a 70°/s. Evita atravesar paredes y techos y recupera suavemente la distancia. Las teclas originales de rotación y arrastre suspenden el ajuste automático durante dos segundos. Si todas las vistas están tapadas, acerca la cámara.
- El sprint admite movimiento en ocho direcciones, independientemente de dónde mire el personaje. Mantiene hambre, colisiones, efectos, estamina y las restricciones de TACZ al apuntar/recargar. Conserva los controles originales WASD y sprint.
- Clic derecho sobre un cofre, barril, puerta u otro bloque interactuable al alcance utiliza la interacción normal del juego. También reconoce los bloques `doomsday_decoration:remains_*`, restos esqueléticos de Zombie Remains Loot y entidades de Corpse. No necesita O ni la tecla especial de interacción de TACZ. Si no hay un interactuable válido, deja pasar el clic para apuntar con TACZ. Respeta alcance y paredes. Las acciones propias del mod de cadáveres, como cortar con un cuchillo, se conservan.
- No reemplaza los menús ni la generación de loot: llama a `MultiPlayerGameMode.useItemOn`/`interact`, que envían los paquetes habituales y ejecutan los eventos de interacción. Quietly puede interceptarlos para su búsqueda y cooldown.
- Desactiva la ampliación de la mira telescópica sobre el mundo mientras está activa la cámara isométrica, para que apuntar no produzca un zoom brusco. La primera persona conserva la ampliación normal.

## Validación y límites

58 comprobaciones con APIs simuladas: geometría de la mira, ángulos, retroceso, interpolación, ocho direcciones de sprint, selección de cámara, colisiones, alcance, línea de visión y despacho de clics a cofres/cadáveres. Verificación ASM de todos los métodos añadidos y modificados; integridad ZIP y comparación de todas las entradas originales. Solo cambian tres clases originales y el listado de mixins. El JAR de TACZ y el de cadáveres se inspeccionaron sin modificarlos.

**No se ha ejecutado Minecraft en este entorno.** La aplicación real de los nuevos mixins, el renderizado, los eventos con todos los mods y la experiencia en interiores necesitan una prueba en el modpack. No se ha validado la trayectoria balística completa ni la alineación bajo otros mods que alteren posteriormente la cámara/proyección.

Prueba práctica: activar la vista isométrica, apuntar a un zombi a distintas alturas, caminar junto a una pared, correr de lado manteniendo el cursor sobre el objetivo y hacer clic derecho en un barril y un cadáver cercano con un arma equipada. Para comprobar búsqueda/cooldown, usa un recipiente sin registrar previamente y espera la apertura normal de Quietly.

## Reconstrucción

Java 21, ECJ y ASM 9 en un directorio de herramientas con `ecj.jar`, `asm.jar`, `asm-tree.jar` y `asm-analysis.jar`:

```sh
python -B iso-tacz-patch/build.py /ruta/dungeons_iso-neoforge-3.6.2+1.21.1.jar /ruta/tacz-neoforge-1.21.1-1.1.8-hotfix-r7.jar /ruta/herramientas
```

Los dobles están exclusivamente en `tests/` y nunca se incluyen en el mod. `tests/make_doubles.py` regenera los archivos auxiliares de prueba. `validation.json` registra los SHA-256 de entrada y salida.
