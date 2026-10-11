# Correcciones del latest.log enviado

Se reproduce una desconexión al abrir el almacenamiento ampliado (líneas 3414–3480), con `Incorrect block entity … expected StorageBlockEntity`. El servidor reemplazaba el contenedor y enviaba el menú avanzado inmediatamente, mientras el cliente todavía veía el bloque anterior.

Quietly v10 envía `ClientboundBlockUpdatePacket`, después el paquete de datos del bloque, y espera dos ticks de servidor antes de ejecutar la apertura nativa. Comprueba jugador, alcance, mundo, bloque e inventario activo antes de reabrir. Bloquea aperturas adicionales sobre el bloque durante la espera. Mantiene el loot, el nombre, la rareza, la ampliación y los boosts de Scavenging.

La barra fallaba con `NoSuchFieldException: overlayMessage` (líneas 3172–3206). Se usa `overlayMessageString`, con alternativa legacy. Se prueba el campo exacto y la alternativa, además del orden de los paquetes, la demora y el bloqueo de otras aperturas. 36 casos de reglas + 47 casos simulados de comportamiento; todas las clases nuevas y modificadas verificadas con ASM.

EMF rechazaba los decimales `.08` de las animaciones del pack Blues. `Blues-Better-Zombies-PZ-L4D2-Gore.2-emf-fix.zip` corrige 118 literales en 40 modelos a `0.08` y equivalentes. Se comprueba el JSON de cada modelo y se conservan byte por byte las texturas y todos los demás archivos. Esta corrección modifica fórmulas de animación, no edita las imágenes.

## Instalación

Cerrar Minecraft y el servidor. Reemplazar Quietly v9 por `quietly-1_0_0-cooldown-v10-storage-sync-hud-fix.jar` en clientes y servidor; dejar una sola versión. Conservar Survivor Creator v23, Zomboid Survival, Jade y Sophisticated Storage/Core.

En resourcepacks, reemplazar y desactivar `Blues-Better-Zombies-PZ-L4D2-Gore.1.zip` por `Blues-Better-Zombies-PZ-L4D2-Gore.2-emf-fix.zip`. Activar el nuevo pack en el cliente. No es necesario convertir el mundo ni reiniciar las tablas de loot.

No se ejecutó Minecraft. Las pruebas son dobles de API y verificaciones de formato/bytecode, no una prueba de red o renderizado en el modpack real. Esta revisión no altera los errores de creación repetida de modelos de Self Expression / Lucky's Wardrobe ni las referencias de biomas de Fabulous Furnished que también aparecen en el log.
