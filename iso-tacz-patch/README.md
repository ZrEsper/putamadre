# Dungeons Iso + TACZ: cámara de supervivencia .13

[Descargar el JAR](../mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.13.jar).

Con Minecraft cerrado, sustituye **solo Dungeons Iso** en `mods` del cliente. No dejes su versión anterior junto a esta. Conserva TACZ hotfix-r7, Zombie Remains Loot, Quietly y sus dependencias. El parche mantiene el identificador y versión internos originales de Dungeons Iso. No requiere cambios en el mundo ni en el servidor.

## Comportamiento

- El modo isométrico siempre empieza **desactivado** al entrar o cambiar de mundo. Solo se activa con su tecla asignada (F4 por defecto). Impide la activación automática aunque la configuración antigua tuviera `onStartup` o `force` activados y omite el diálogo inicial que podía activarlo.
- Con la vista isométrica activa y un arma TACZ, muestra una mira pequeña sobre el primer obstáculo o entidad en la dirección del arma. Usa la rotación del jugador que TACZ envía al servidor y la matriz de proyección que Dungeons Iso entrega al renderizador, tanto en perspectiva como en modo ortográfico. Puede apuntar hacia arriba y abajo y conserva los cambios de retroceso de TACZ. Indica la línea de tiro; la dispersión, penetración y caída de la bala siguen siendo las de TACZ.
- El cursor en los últimos cinco píxeles de los bordes izquierdo/derecho gira la cámara a 90°/s. Mantener CTRL izquierdo o derecho sustituye el giro por desplazamiento de la vista a 7 bloques/s en cualquiera de los cuatro bordes, con distancia limitada. Fuera del borde se detiene. Soltar CTRL recentra el desplazamiento; Home también lo recentra. Conserva los controles manuales originales y no gira automáticamente por obstáculos.
- El sprint admite movimiento en ocho direcciones, independientemente de dónde mire el personaje. Mantiene hambre, colisiones, efectos, estamina y las restricciones de TACZ al apuntar/recargar. Conserva los controles originales WASD y sprint.
- Clic derecho, tanto en vista normal como isométrica, sobre un cofre, barril, puerta u otro bloque interactuable al alcance utiliza la interacción normal del juego. También reconoce los bloques `doomsday_decoration:remains_*`, restos esqueléticos de Zombie Remains Loot y entidades de Corpse. No necesita O ni la tecla especial de interacción de TACZ. Si no hay un interactuable válido, mantener clic derecho activa el apuntado TACZ sin abandonar la cámara isométrica; disparar con clic izquierdo conserva la mira mientras se mantenga el derecho. La mira incluye una línea y un anillo sobre el objetivo. Respeta alcance y paredes. Las acciones propias del mod de cadáveres, como cortar con un cuchillo, se conservan.
- La selección isométrica usa un rayo propio desde la cámara y las formas visuales del cadáver, incluso cuando el selector original no lo detecta o el cuerpo no tiene colisión.
- No reemplaza los menús ni la generación de loot: llama a `MultiPlayerGameMode.useItemOn`/`interact`, que envían los paquetes habituales y ejecutan los eventos de interacción. Quietly puede interceptarlos para su búsqueda y cooldown.
- Desactiva la ampliación de la mira telescópica sobre el mundo mientras está activa la cámara isométrica, para que apuntar no produzca un zoom brusco. La primera persona conserva la ampliación normal.

- La selección para romper bloques incluye vegetación colgante y cadáveres de bloque, sin exigir que puedan abrirse. Mantiene las herramientas, tiempos de rotura y permisos normales; con un arma TACZ equipada se conserva el disparo.

## Validación y límites

132 comprobaciones con APIs simuladas: inicio desactivado y activación manual, geometría de la mira, ángulos, retroceso, interpolación, ocho direcciones de sprint, rotación manual, bordes extremos, protección de perspectiva, alcance, línea de visión y despacho de clics a cofres/cadáveres. Verificación ASM de todos los métodos añadidos y modificados; integridad ZIP y comparación de todas las entradas originales. Solo cambian cuatro clases originales y el listado de mixins. El JAR de TACZ y el de cadáveres se inspeccionaron sin modificarlos.

**No se ha ejecutado Minecraft en este entorno.** La aplicación real de los nuevos mixins, el renderizado, los eventos con todos los mods y la experiencia en interiores necesitan una prueba en el modpack. No se ha validado la trayectoria balística completa ni la alineación bajo otros mods que alteren posteriormente la cámara/proyección.

Prueba práctica: activar la vista isométrica, apuntar a un zombi a distintas alturas, caminar junto a una pared, correr de lado manteniendo el cursor sobre el objetivo y hacer clic derecho en un barril y un cadáver cercano con un arma equipada. Para comprobar búsqueda/cooldown, usa un recipiente sin registrar previamente y espera la apertura normal de Quietly.

## Reconstrucción

Java 21, ECJ y ASM 9 en un directorio de herramientas con `ecj.jar`, `asm.jar`, `asm-tree.jar` y `asm-analysis.jar`:

```sh
python -B iso-tacz-patch/build.py /ruta/dungeons_iso-neoforge-3.6.2+1.21.1.jar /ruta/tacz-neoforge-1.21.1-1.1.8-hotfix-r7.jar /ruta/herramientas
```

Los dobles están exclusivamente en `tests/` y nunca se incluyen en el mod. `tests/make_doubles.py` regenera los archivos auxiliares de prueba. `validation.json` registra los SHA-256 de entrada y salida.

En .6, mover bruscamente el cursor acelera el giro de 90°/s hasta 360°/s y la aceleración decae suavemente. La selección omite bloques ocultos por los filtros de Dungeons Iso, conserva la cara real del bloque y permite colocar bloques con clic derecho sobre superficies no interactuables. Los obstáculos físicos desde el jugador, alcance y permisos del servidor siguen vigentes.




Versión .10: sin filtro de modelos de entidades. El apuntado invierte la matriz real de proyección y sigue el rayo del cursor sin selección asistida; si no hay superficie ni entidad, usa un plano a media altura del jugador. La mira clara muestra la línea de tiro; las cruces naranjas breves marcan impactos en bloques recibidos mediante las partículas de TACZ (incluidos disparos cercanos de otros jugadores). No confirma impactos en entidades ni garantiza la trayectoria de cada bala antes de disparar. Con Sodium, el radio de renderizado isométrico baja al 70% del límite anterior, con mínimo de 48 bloques y sin superar el ajuste del usuario. La selección descarta bloques fuera del rayo antes de consultar formas. Rendimiento, aplicación de mixins y precisión visual pendientes de prueba en Minecraft.

Versión .11: el clic derecho con un melee de LR Tactical llega al ataque pesado nativo en lugar de consumirse como colocación de bloques. Los cofres y cadáveres interactuables conservan prioridad; el JAR de melee del usuario no necesita reemplazarse.

Versión .12: el retroceso temporal se separa del objetivo bajo el cursor y decae exponencialmente hacia él después de disparar; se conservan los impulsos nuevos de TACZ. El ajuste de regreso necesita validación visual dentro del juego.

Versión .13: desplazamiento acumulado del recoil limitado a ±4° verticales y ±3° horizontales respecto al objetivo del cursor. Al soltar la tecla de disparo de TACZ, el desplazamiento se limpia en la siguiente actualización de apuntado; cambiar ampliamente de objetivo también descarta el desplazamiento anterior. Prueba con 600 actualizaciones de ráfaga intensa y retorno del jugador al objetivo aprobada con APIs simuladas. No modifica la cadencia, dispersión ni daño del servidor. Pendiente comprobar ráfagas y ritmo visual en Minecraft.
