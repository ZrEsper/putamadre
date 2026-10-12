# Isométrica .18: mochilas y recorte al adelantar con CTRL

[Descargar .18](../mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.18-backpacks-ctrl-cutaway.jar). Reemplaza la .17 adjunta; deja una sola versión de Dungeons Iso. Sophisticated Backpacks permanece sin modificar.

Se inspeccionó Sophisticated Backpacks 3.26.7.2182. BackpackBlock abre su menú mediante useWithoutItem, pero no quedaba clasificado en todos los casos por el selector isométrico. Ahora se reconoce como interactuable, respetando el blacklist y los límites del mundo. Bridge.open ahora procesa ambas manos y, si la interacción no consume la acción y se tiene una mochila, llama al uso nativo del objeto. Así puede abrir la mochila en mano después de un PASS. Mantiene la colocación nativa si useItemOn ya consumió la acción. Un FAIL detiene la interacción. Alcance, línea de visión, permisos y menús siguen gestionados por las APIs originales.

Para CTRL, los escáneres de habitaciones y líneas de visión siguen el foco adelantado por edgeX/edgeZ. Usan la posición real de la cámara final, y desplazan horizontalmente el objetivo visual del recorte manteniendo su altura. Cuando se suelta CTRL, el offset vuelve a cero y el foco regresa al jugador. Los intervalos de escaneo y las colas de reconstrucción de .17 se conservan. La cámara, el apuntado y los rayos de interacción no se reescriben.

Se cambian tres clases originales: Bridge, InteractionTargeting y SodiumCompat, más la versión interna de metadata. Se añaden dos helpers. El resto del JAR adjunto se conserva, incluyendo el comportamiento de interiores direccionales de .17.

15 comprobaciones simuladas: mochila de mano principal/secundaria, swing, interacción consumida, FAIL, ausencia de uso involuntario de otros ítems, reconocimiento de bloques, blacklist, límites, foco adelantado, retorno, coordenadas negativas y desactivación del modo. Se verifica bytecode e integridad ZIP. No se ha ejecutado Minecraft: comprobar apertura de mochilas colocadas/en mano y recorte al adelantar con CTRL en una partida sigue pendiente.

Reproducción con Java 21, ECJ y ASM:

```sh
python iso-backpack-patch/build.py /ruta/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.17-directional-interiors.jar /ruta/herramientas
```

La carpeta de herramientas contiene ecj.jar, asm.jar, asm-tree.jar y asm-analysis.jar. La base debe tener SHA-256 `759e099a7415c6e27748f01f8e10dba24122f85a13fb18ee0324dd724135618c` (la .17 adjunta). No se empaquetan las clases simuladas ni los tests.
