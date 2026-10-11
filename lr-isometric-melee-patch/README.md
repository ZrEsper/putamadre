# LesRaisins Tactical: entrada melee en isométrica .1

[Descargar JAR](../mods/LesRaisins-Tactical-Equipements-1.21.1-0.4.3-zomboid-isometric-melee.1.jar).

Reemplaza LesRaisins Tactical del cliente, sin duplicarlo. Conserva `zomboid-global-melee-animations`, eZWeight, TACZ y sus dependencias. No cambia identificador, versión, recursos, paquetes ni código de servidor. El servidor puede conservar el original enviado por el usuario.

La entrada original del ataque normal dependía de `NORMAL_ATTACK.isDown()` y sus clics registrados. El parche añade una lectura del botón físico configurado cuando el modo isométrico está activo, la ventana tiene foco, no hay un menú abierto y la tecla mouse no requiere modificadores. Mantiene la lectura original de teclas en las otras situaciones. Evita depender exclusivamente del estado del keymapping en una cámara con cursor liberado. No se ha confirmado dentro del juego que esta fuese la única causa del fallo reportado.

Los ataques normales y especiales toman la dirección del objetivo bajo el cursor isométrico; para bloques conserva el plano horizontal, y para entidades admite la altura del punto seleccionado. Sin modo isométrico se devuelve la dirección original. No invoca ataques extra ni suprime cooldowns, estamina, validación o paquetes de ataque.

Ocho comprobaciones con APIs simuladas de pulsación, liberación, GUI, foco, modificadores, cámara normal y dirección. Verificación ASM y comparación de todas las entradas: solo cambia `AttackKeys.class` y se añaden los helpers. El resto del JAR del usuario, incluidas sus modificaciones JSON, se preserva. Aplicación de hooks, animaciones, daño, cooldown y compatibilidad con todo el modpack pendientes de prueba en Minecraft.

```sh
python -B lr-isometric-melee-patch/build.py /ruta/LesRaisins-Tactical-Equipements-1.21.1-0.4.3-zomboid-jsononly-safe.jar /ruta/herramientas
```
