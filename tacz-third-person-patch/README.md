# TACZ: apuntado en tercera persona y niebla ambiental

[Descargar TACZ modificado](../mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.4.jar).

Sustituye el JAR TACZ hotfix-r7 del cliente; no instales dos TACZ. Conserva sus paquetes de armas y dependencias. No requiere Dungeons Iso. El identificador y versión internos siguen siendo los originales; el servidor puede conservar su hotfix-r7.

En tercera persona normal, clic derecho usa el apuntado nativo y muestra el círculo y la línea de tiro hasta el primer obstáculo o entidad. La dirección proviene de la rotación real del jugador, no de una selección asistida. Conserva el disparo, retroceso y marcas nativas de acierto. La primera persona usa el comportamiento original. La ampliación telescópica del mundo se omite en tercera persona para evitar saltos de zoom. Este parche no controla cambios de cámara impuestos por otros mods.

La mira indica una línea de tiro: no predice dispersión, gravedad ni penetración de cada bala. Tampoco añade cursor libre en tercera persona normal; el mouse conserva el control normal de mirada.

## Niebla de prueba y automática

- `/zomboidfog prueba`: un episodio de prueba: entrada gradual de 60 segundos, fase fuerte aleatoria de 2–10 minutos y salida gradual de 60 segundos; luego vuelve al automático.
- `/zomboidfog auto`: modo automático.
- `/zomboidfog off`: desactivarla hasta cambiar de modo o reiniciar el cliente.

En automático aparece cada tres días de tiempo del mundo. Cada episodio entra en 60 segundos, mantiene la fase fuerte entre 2 y 10 minutos y sale en 60 segundos, usando transiciones suaves. En el ciclo automático la duración se sortea de forma determinista por intervalo; 20 ticks equivalen a un segundo nominal. La prueba usa el reloj real del cliente. El primer intervalo comienza en el tick 24000 del mundo. Usa el contador total de ticks, no la hora modificada con `/time set`.

En la fase fuerte, el final de la niebla queda a unos 12 bloques, añadiendo la distancia entre la cámara y los ojos del jugador para no ocultarlo al alejar la cámara. Es una aproximación mediante la niebla nativa centrada en la cámara, no un círculo exacto de 12 bloques centrado en el jugador. No aumenta una distancia menor. No cambia la IA ni el clima del servidor. Los comandos son locales al cliente. Se preserva la distancia de niebla nativa en agua/lava.

## Validación

28 comprobaciones con APIs simuladas del HUD y calendario; verificación ASM de métodos modificados y añadidos, integridad ZIP y preservación de todas las entradas originales salvo tres clases y el listado de mixins. Prueba de renderizado, conexión al servidor, eventos de niebla y comandos pendiente en Minecraft.

```sh
python -B tacz-third-person-patch/build.py /ruta/tacz-neoforge-1.21.1-1.1.8-hotfix-r7.jar /ruta/herramientas
```

Necesita Java 21, ECJ y ASM. Los dobles de prueba no se incluyen en el JAR.

Versión .2: disparos locales sin silenciador con volumen ×1.30 y distancia ×1.50; con silenciador, ×1.15 y ×1.25. Los disparos de otros jugadores recibidos por TACZ usan ×1.30 y ×1.50, conservando su sonido silenciado de origen. No amplifica recarga, inspección o golpes. El servidor puede limitar qué sonidos envía: este parche no altera esa distribución ni los radios de atracción de zombis de otros mods. El volumen final también depende de la mezcla del paquete de armas y las opciones de sonido.

Versión .4: la cámara isométrica sobrescribía la distancia de niebla en su hook final de FogRenderer. Un hook de compatibilidad posterior, con prioridad 500, vuelve a aplicar el episodio atmosférico después del ajuste isométrico. Conserva la distancia compensada, transiciones y duración. Se probaron la escritura final de distancia y las exclusiones de agua/vista normal con APIs simuladas; la aplicación y orden de mixins del modpack siguen pendientes de validar en Minecraft.
