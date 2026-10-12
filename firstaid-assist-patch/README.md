# First Aid: derribo y asistencia

[Descargar JAR](../mods/firstaid-1.21.1-ZOMBOID-v3.5.0-assisted-revival.jar).

Construido sobre el v3.4.9 adjuntado. Reemplaza First Aid en cliente y servidor, dejando una sola versión. Conserva identificador, versión interna y dependencias originales para compatibilidad. No hace falta borrar el mundo ni los datos del jugador.

La versión anterior aún tenía una ruta de `CommonUtils.killPlayer` que terminaba en muerte directa y un sistema de rescate que exigía suministros. Se intercepta ahora esa ruta antes de morir: si queda salud corporal global, continúa vivo; si no queda, entra en derribo. La pérdida de cabeza o torso mantiene las penalizaciones de v3.4.9, sin muerte por esa zona sola.

- +6 puntos de salud máxima (+3 corazones), con modificador permanente sin acumulación por entrar varias veces. Otros modificadores de vida se conservan.
- Derribo de 2000 ticks (100 segundos con 20 TPS), sin ruleta de muerte inmediata ni recuperación espontánea.
- Otro jugador inicia asistencia con clic derecho, sin requerir un objeto. Debe permanecer a tres bloques o menos durante 160 ticks (8 segundos). Se interrumpe si se aleja, cambia de dimensión, recibe daño o el objetivo deja de poder ser rescatado.
- Un jugador derribado con desfibrilador en cualquiera de las manos puede iniciar su propia reanimación con la tecla de usar/clic derecho. Se valida y temporiza en el servidor durante 8 segundos; retirar el objeto interrumpe el proceso. Al completarlo gasta un uso del desfibrilador. No se levanta automáticamente sin ese objeto.
- Regresa con 6 puntos de vida global (3 corazones). Las heridas permanecen y el sangrado de First Aid tiene 20 segundos de pausa para permitir vendarlas. Otros ataques pueden volver a derribarlo.
- Tres ataques adicionales con entidad atacante o proyectil pueden rematar al derribado. Sangrado ambiental sin atacante no cuenta como golpe de remate.
- HUD sencillo con cuenta atrás; el ayudante ve una barra de progreso en texto, movimientos de brazo y dos sonidos originales sintetizados incluidos en el JAR.

La muerte por tiempo agotado se conserva. Los comandos administrativos de muerte y caída fuera del mundo mantienen su comportamiento. El sistema original de objetos no puede saltarse la duración del rescate de un jugador derribado.

## Validación

43 comprobaciones de salud y penalizaciones y 24 del ciclo de derribo/asistencia: 159 frente a 160 ticks, interrupción por distancia, vida recuperada, gracia de sangrado, tres golpes de remate, desfibrilador obligatorio y consumo al finalizar, modificador de corazones sin duplicación y eventos de sonido. APIs simuladas; no se ejecutó Minecraft. Verificación ASM de clases modificadas e integridad ZIP. La selección de jugadores con otros mods de cámara, el sonido y las animaciones necesitan prueba real.

```sh
python -B firstaid-assist-patch/build.py /ruta/firstaid-v3.4.9.jar /ruta/herramientas
```

Requiere Java 21, ECJ y ASM. Los recursos de sonido se incluyen ya generados; las clases de pruebas no se empaquetan.
