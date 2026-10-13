# Loot configurable sobre Action Sounds y NPC Full Cargo

Esta entrega parte exactamente de los dos JAR enviados:
- Hordes 26 `action-sounds`.
- Zombie Remains 7 `npc-full-cargo`.

Reemplázalos en cliente y servidor por los dos JAR de `mods/`, sin duplicados. El panel usa el protocolo 26 y sus 62 ajustes.

En `/zhorda panel`, selecciona **Cofres/barriles**, **Drops** o **Cadáveres**. Cada perfil tiene cantidad, porcentajes por categoría, rueda acelerada, entrada numérica y **Ver ejemplo**. Activa **Personalizado** y pulsa **Aplicar** para usarlo. **Original** conserva las tablas y el contenido originales.

Los porcentajes se normalizan por selección: categorías a 0 no se eligen. Cantidad: 0–10×, con límites de espacio y generación. La vista previa es ilustrativa. Cambia el loot nuevo; los cofres ya generados requieren el reinicio de loot si quieres regenerarlos. Los cadáveres anteriores no se modifican.

Se conservan los sonidos de acciones, los cambios de combate y la limpieza del Hordes enviado. Se conservan la transferencia de carga completa de los NPC, la cola de cadáveres pendientes y su guardado/recuperación. La ruta especial de los NPC también está conectada al perfil de cadáveres antes de serializar la carga y evita generar un segundo lote al suelo.

Importante para el comportamiento: Original mantiene íntegra la carga del NPC. Si eliges Personalizado para cadáveres, su contenido nuevo se sustituye según el perfil, incluida la carga heredada; la cantidad y las categorías configuradas pueden reducirla o vaciarla.

Validación: los recursos de sonidos y las clases de combate se conservaron byte por byte; también las clases de persistencia de carga no modificadas. Verificación de bytecode: Hordes 110 clases/900 métodos y Remains 59 clases/420 métodos. Pruebas de guardado por mundo, los 62 ajustes y conservación de las herramientas previas. No probado dentro de Minecraft.
