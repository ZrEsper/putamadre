# Apuntado manual, scopes y destello de snipers

Corrección conjunta para los **tres archivos exactos enviados**, Minecraft 1.21.1 / NeoForge. Conserva los demás recursos y clases, incluidos corpse outline y la patada instantánea a puertas.

## Instalar

1. Cierra Minecraft.
2. En la carpeta `mods` de la instancia que juegas, retira las versiones anteriores de **Dungeons Iso**, **Third Person Shooting** y **TACZ**. No dejes dos versiones del mismo mod.
3. Copia los tres JAR nuevos de este paquete a esa carpeta.
4. Abre Minecraft con las mismas dependencias y configuración que utilizabas.

En el instalador para el creador, esos mismos tres JAR van en `Para-el-creador/componentes/mods`. Reemplaza los anteriores y vuelve a preparar la distribución. El parche no exige un mundo ni modifica sus guardados.

## Cambios

- Desactiva la adquisición contextual de objetivos, el bloqueo de objetivo y la asistencia adicional de Dungeons Iso. El disparo sigue la intersección real del cursor; mantiene la comprobación de bloques y la corrección de paralaje.
- TACZ deja de ampliar las cajas de los enemigos para seleccionar el punto bajo la mira de tercera persona. Mantiene la intersección con su caja real.
- Third Person Shooting deja de imponer al jugador la rotación de la cámara en cada evento de disparo. En isométrica tampoco acumula retroceso en la cámara Shoulder Surfing.
- El retroceso isométrico sigue el cursor continuamente, sin los límites rígidos anteriores de 3°/4° ni el reinicio brusco al soltar el disparo. Conserva el retroceso del arma; restaurar la pose de tercera persona ya no lo borra.
- Zoom según `getAimingZoom` de la mira equipada, incluido el nivel seleccionado de scopes variables. Las miras ópticas reciben **35 % más aumento**, hasta 32x; hierro y aumentos de 1x quedan iguales. Conserva la transición al apuntar. Funciona mediante FOV en perspectiva y escala de proyección en isométrica ortográfica, sin desplazar la cámara física.
- Destello de sniper más de tres veces mayor a 10 bloques, luminoso/aditivo y con opacidad de 85–100 %. Cono de observación de 35° hasta 32 bloques y 18° después, alcance máximo 160 bloques. Solo otros jugadores ven el destello: conserva la exclusión del jugador local, la necesidad de sniper con scope apuntando y la comprobación de paredes.

No altera el daño, dispersión, velocidades de proyectil ni precisión propia del arma. El apuntado manual conserva el retroceso.

## Validación y límites

33 comprobaciones ejecutables de aumento óptico, transición ADS, recuperación de retroceso en ráfaga, restauración de pose y condiciones/tamaño/opacidad del destello. ASM BasicVerifier revisa los métodos modificados; comprobamos CRC, ausencia de duplicados y el inventario exacto de clases cambiadas. Todos los recursos originales permanecen idénticos.

**No se ha ejecutado Minecraft en este entorno.** Por tanto, la corrección del bloqueo visual y el resultado final de los scopes necesitan una prueba dentro del juego. Shoulder Surfing no fue adjuntado: se corrigen los tres mods recibidos y sus conexiones con ese mod; no se modifica su JAR.

Prueba en primera persona, tercera persona y ambas proyecciones isométricas: mantener disparo mientras mueves el cursor hacia arriba y abajo; mirar junto al borde de un enemigo sin que la mira salte; disparar detrás de una pared; comparar scopes 2x/4x/8x y alternar los niveles de una variable. Con dos jugadores, comprobar el destello a 5/20/80 bloques, frente al sniper, detrás de una pared y desde la vista del propio sniper.

## Reconstruir

`python3 build.py` con Java 21 y acceso HTTPS a Maven Central. El script verifica hashes de las entradas y herramientas ECJ/ASM, compila las clases de integración, aplica cambios limitados y genera `output/validation.json` y los tres JAR. Las entradas grandes se recuperan de `../mods/` del repositorio; la versión original pequeña de TPS está en `base/`. Los dobles de prueba y herramientas no se incluyen en los JAR.
