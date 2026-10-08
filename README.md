# Mods de Zomboid

## Zomboid Survival 1.0.2

[Descargar el JAR actualizado](https://github.com/ZrEsper/putamadre/raw/refs/heads/main/mods/zomboid-survival-1.21.1-1.0.2-generator-ui.jar)

La versión 1.0.2 corrige el fallo de arranque `FoodMenuMergeMixin is missing an @Mixin annotation` de la 1.0.1, que fue retirada.

Para Minecraft 1.21.1 con NeoForge y las dependencias del mod original.

- Interfaz propia de generador: motor, combustible, reserva, barras de energía y autonomía. Conserva los 54 espacios guardados.
- Alimenta máquinas a 25 bloques de distancia.
- 64 carbones duran 30 minutos de generación a 20 TPS; el combustible se pausa cuando el depósito está lleno.
- Sonido de motor y humo de escape mientras genera energía.
- Los alimentos con diferencias de frescura de hasta 10 segundos se pueden volver a apilar. La mezcla conserva la frescura más vieja.
- Mantiene la interfaz de First Aid, sin reemplazarla por la pantalla de Zomboid Survival.

Reemplaza el JAR anterior de **Zomboid Survival** en `mods`, tanto en cliente como en servidor si juegas en uno. Conserva tu JAR de **First Aid**. Instala solo una versión de Zomboid Survival; el JAR 1.0.0 también se conserva en este repositorio como base y versión anterior.

Las comprobaciones de lógica y bytecode pasaron con dobles de las API. Falta verificar el aspecto, los mixins y la interacción con los demás mods dentro de Minecraft.

[Código del parche, instrucciones de reproducción y comprobaciones](generator-patch/README.md).
