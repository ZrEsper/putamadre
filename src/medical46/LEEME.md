# Gore .31 — corrección del crasheo al disparar a cadáveres

Cierra Minecraft y el servidor. Retira `zomboid-zombie-gore-1.0.20-neoforge-1.21.1-medical-corpse.30.jar` y coloca el Gore .31 incluido, en clientes y servidor. Deja una sola versión de Gore.

Conserva los otros cinco mods del paquete anterior: First Aid 3.6.15, compat TaCZ 1.0.3, Ragdollified .24, Corpse .5 y NPCs .31. No hay cambios en TaCZ, Tactical ni TaCZ Addon en este reemplazo.

## Causa comprobada

El reporte `crash-2026-10-19_11.47.54-server.txt` muestra `IllegalStateException: scale`, causado por `NoSuchMethodException: scale`, dentro de `Bullet13.tick` de Gore .30. Su llamada por reflexión entregaba un `Float` a `Vec3.scale(double)`. El resolvedor de reflexión no convierte automáticamente ese argumento a `Double`.

Esta versión convierte explícitamente ese valor a double. La magnitud y dirección del vector se conservan. Solo cambian `dev/zomboid/gore/Bullet13.class` y los metadatos de versión del JAR; se conservan recursos, anatomía, heridas, cadáveres e integraciones del paquete anterior. La versión interna 1.0.20.46 sigue dentro del rango requerido por Ragdollified .24 y Corpse .5.

## Verificación

La prueba ejecuta el `Bullet13.tick` real y el resolvedor `FastReflection` real con objetos simulados para el entorno del juego, incluyendo un vector cuya firma es `scale(double)`. Con Gore .30 reproduce la misma excepción `scale`; con .31 completa el impacto, envía una sola notificación y conserva el vector esperado. Se comprobó la integridad ZIP y la conservación de todos los demás archivos del JAR, además del análisis ASM del código modificado.

No se ejecutó Minecraft con el modpack completo. El informe de pruebas está incluido. Para comprobarlo en el juego, repite el disparo contra un cadáver que provocó este cierre.
