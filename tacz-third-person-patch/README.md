# TACZ: apuntado en tercera persona y niebla ambiental

[Descargar TACZ modificado](../mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.7.jar).

Sustituye el JAR TACZ hotfix-r7 del cliente y del servidor (si juegas en multijugador); no instales dos TACZ. Conserva sus paquetes de armas y dependencias. No requiere Dungeons Iso. El identificador y versión internos siguen siendo los originales; para los disparos ampliados, todos los clientes y el servidor necesitan esta versión .7. En singleplayer basta reemplazar el JAR de la instancia.

En tercera persona normal, clic derecho usa el apuntado nativo y muestra el círculo y la línea de tiro hasta el primer obstáculo o entidad. La dirección proviene de la rotación real del jugador, no de una selección asistida. Conserva el disparo, retroceso y marcas nativas de acierto. La primera persona usa el comportamiento original. La ampliación telescópica del mundo se omite en tercera persona para evitar saltos de zoom. Este parche no controla cambios de cámara impuestos por otros mods.

La mira indica una línea de tiro: no predice dispersión, gravedad ni penetración de cada bala. Tampoco añade cursor libre en tercera persona normal; el mouse conserva el control normal de mirada.

## Niebla de prueba y automática

- `/zomboidfog prueba`: un episodio de prueba: entrada gradual de 60 segundos, fase fuerte aleatoria de 2–10 minutos y salida gradual de 60 segundos; luego vuelve al automático.
- `/zomboidfog auto`: modo automático.
- `/zomboidfog off`: desactivarla hasta cambiar de modo o reiniciar el cliente.

En automático aparece cada tres días de tiempo del mundo. Cada episodio entra en 60 segundos, mantiene la fase fuerte entre 2 y 10 minutos y sale en 60 segundos, usando transiciones suaves. En el ciclo automático la duración se sortea de forma determinista por intervalo; 20 ticks equivalen a un segundo nominal. La prueba usa el reloj real del cliente. El primer intervalo comienza en el tick 24000 del mundo. Usa el contador total de ticks, no la hora modificada con `/time set`.

En la fase fuerte, el final de la niebla queda a unos 12 bloques, añadiendo la distancia entre la cámara y los ojos del jugador para no ocultarlo al alejar la cámara. Es una aproximación mediante la niebla nativa centrada en la cámara, no un círculo exacto de 12 bloques centrado en el jugador. No aumenta una distancia menor. No cambia la IA ni el clima del servidor. Los comandos son locales al cliente. Se preserva la distancia de niebla nativa en agua/lava.

## Validación

40 comprobaciones con APIs simuladas del HUD y calendario; verificación ASM de métodos modificados y añadidos, integridad ZIP y preservación de todas las entradas originales salvo tres clases y el listado de mixins. Prueba de renderizado, conexión al servidor, eventos de niebla y comandos pendiente en Minecraft.

```sh
python -B tacz-third-person-patch/build.py /ruta/tacz-neoforge-1.21.1-1.1.8-hotfix-r7.jar /ruta/herramientas
```

Necesita Java 21, ECJ y ASM. Los dobles de prueba no se incluyen en el JAR.

Versión .7: duplica el alcance audible de la anterior .6: sin silenciador pasa de ×1.50 a ×3.00 respecto del paquete base; con silenciador, de ×1.25 a ×2.50. Con las configuraciones por defecto son 192 y 40 bloques (antes 96 y 20). Volumen de disparos ×1.45 respecto del base y un refuerzo adicional ×1.08 bajo techo. TACZ conserva sonido posicional sin corte por paredes: se oye dentro y fuera de edificios. Otros mods de oclusión pueden afectar el resultado.

El servidor envía disparos a jugadores de la misma dimensión dentro del radio, sin limitarse a quienes rastrean el chunk del tirador. El mensaje incluye posición en su nombre de sonido; los clientes .7 la interpretan para reproducir un disparo lejano aunque no tengan la entidad del tirador cargada. No crea entidades en el mundo, no carga chunks y no cambia el formato del codec. Clientes anteriores no interpretan estos nombres: actualiza ambos lados. Recargas y otros sonidos mantienen su alcance y distribución originales. Por seguridad aritmética, el alcance ampliado se limita a 32767 bloques para configuraciones extremas.

Estos cambios son de sonido audible; no modifican el radio de detección de zombis de otros mods.

Versión .4: la cámara isométrica sobrescribía la distancia de niebla en su hook final de FogRenderer. Un hook de compatibilidad posterior, con prioridad 500, vuelve a aplicar el episodio atmosférico después del ajuste isométrico. Conserva la distancia compensada, transiciones y duración. Se probaron la escritura final de distancia y las exclusiones de agua/vista normal con APIs simuladas; la aplicación y orden de mixins del modpack siguen pendientes de validar en Minecraft.

Versión .5: con mano principal vacía y cámara isométrica, antes de `Minecraft.startAttack` restaura el objetivo de entidad bajo el cursor si está vivo, dentro del alcance vanilla y visible desde los ojos del jugador. Esto evita que la selección posterior de bloques del parche isométrico deje `hitResult` apuntando al suelo en lugar del zombi. No envía ataques extra ni cancela el ataque; conserva estamina, cooldown y validación del servidor. Ocho pruebas de restauración, paredes, alcance, objeto equipado, GUI, cámara normal y objetivo muerto/no entidad aprobadas con APIs simuladas. El rechazo por agotamiento de estamina sigue siendo posible y no se ha eliminado. Pendiente verificar el reporte en Minecraft.

Versión .6: además de las distancias, restaura el color de la niebla después de `CullingBackdrop` de Dungeons Iso, que podía llevar los colores a negro al ocultar techos. Actualiza los campos de FogRenderer, el color del shader y el fondo recortado al mismo tono atmosférico. Un segundo hook de color se ejecuta al final de setupColor; el hook de distancia reaplica también el color. Conserva agua/lava, modo apagado y cámara normal. Añade `/zomboidfog estado` para mostrar intensidad, cámara isométrica detectada y modo. Cuatro regresiones con APIs simuladas reproducen el color negro y comprueban su restauración en las tres salidas; el aspecto y el orden real con BetterFog/Sodium requieren validación en Minecraft.

Validación .7: 53 comprobaciones con APIs simuladas, incluidas distancias, ganancia bajo techo, destinatarios del servidor y nombres posicionales; verificación ASM e integridad del JAR. La reproducción real de audio, su mezcla y la compatibilidad con mods de oclusión requieren probarse en Minecraft.
