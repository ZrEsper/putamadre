# TACZ: apuntado en tercera persona y niebla ambiental

[Descargar TACZ modificado](../mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.1.jar).

Sustituye el JAR TACZ hotfix-r7 del cliente; no instales dos TACZ. Conserva sus paquetes de armas y dependencias. No requiere Dungeons Iso. El identificador y versión internos siguen siendo los originales; el servidor puede conservar su hotfix-r7.

En tercera persona normal, clic derecho usa el apuntado nativo y muestra el círculo y la línea de tiro hasta el primer obstáculo o entidad. La dirección proviene de la rotación real del jugador, no de una selección asistida. Conserva el disparo, retroceso y marcas nativas de acierto. La primera persona usa el comportamiento original. La ampliación telescópica del mundo se omite en tercera persona para evitar saltos de zoom. Este parche no controla cambios de cámara impuestos por otros mods.

La mira indica una línea de tiro: no predice dispersión, gravedad ni penetración de cada bala. Tampoco añade cursor libre en tercera persona normal; el mouse conserva el control normal de mirada.

## Niebla de prueba y automática

- `/zomboidfog prueba`: niebla durante 90 segundos reales, luego vuelve al automático.
- `/zomboidfog auto`: modo automático.
- `/zomboidfog off`: desactivarla hasta cambiar de modo o reiniciar el cliente.

En automático aparece cada tres días de tiempo del mundo durante 6000 ticks (cinco minutos a 20 TPS), con transiciones de 600 ticks (30 segundos). El primer intervalo comienza en el tick 24000 del mundo. Usa el contador total de ticks, no la hora modificada con `/time set`. Limita la vista a unos 64 bloques en su fase fuerte; no aumenta una distancia menor. No cambia la IA, el clima del servidor ni la iluminación de bloques. Los comandos son locales al cliente. Se preserva la niebla nativa en agua/lava.

## Validación

17 comprobaciones con APIs simuladas del HUD y calendario; verificación ASM de métodos modificados y añadidos, integridad ZIP y preservación de todas las entradas originales salvo dos clases y el listado de mixins. Prueba de renderizado, conexión al servidor, eventos de niebla y comandos pendiente en Minecraft.

```sh
python -B tacz-third-person-patch/build.py /ruta/tacz-neoforge-1.21.1-1.1.8-hotfix-r7.jar /ruta/herramientas
```

Necesita Java 21, ECJ y ASM. Los dobles de prueba no se incluyen en el JAR.
