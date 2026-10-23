# Zomboid Hordes admin-panel.52 — registro seguro del botín

Reemplaza `zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.51-spawn-loot-authority.jar` por `zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.52-safe-loot-registration.jar`. Deja un solo JAR de zomboid-hordes y reinicia. La versión de dependencia interna sigue siendo 1.8.3 para conservar compatibilidad; la revisión se identifica por el nombre del JAR y su SHA256. Guarda una copia del mundo para la prueba.

## Causa encontrada

El latest.log del 23 de octubre muestra al servidor esperando un chunk en esta cadena:

`LootRestock.loaded -> MinecraftServer.execute -> LootRestock.remember -> BlockEntity.setChanged -> Level.updateNeighbourForOutputSignal -> getBlockState -> carga de chunk`

`execute` puede ejecutar inmediatamente si ya está en el hilo del servidor: no garantiza posponer el trabajo hasta que termine la carga actual. `setChanged` marca el bloque y notifica comparadores; esa notificación puede consultar el chunk vecino al registrar un cofre en un borde. El watchdog registró esperas de hasta 162377 ms.

## Qué cambia

- El registro de cofres y barriles en eventos de carga/descarga siempre entra en una cola. Se procesa desde el evento Post de tick de mundo, también de día; hasta 128 trabajos por evento. No se ejecuta dentro del callback de carga.
- La cola respeta el servidor propietario. Un tick de cliente o de otro servidor no ejecuta esos trabajos. Se limpia al detener el servidor.
- Una petición de `remember` desde un trabajador se difiere antes de tocar NBT o las colecciones de cofres.
- Se valida que el chunk esté disponible mediante `getChunkNow`, y que siga siendo la misma instancia. También se comprueba que la entidad de bloque siga perteneciendo al chunk.
- Las cuatro llamadas a `setChanged()` de LootRestock se sustituyen por marcado de guardado del chunk cargado. Estas operaciones cambian metadatos/loot pendiente, no el contenido visible del inventario; no necesitan notificar comparadores. No solicitan la generación de chunks vecinos.
- Se conserva el código de tablas de botín, probabilidades, noches, semilla, elegibilidad y comandos de la .51.

## Pruebas ejecutadas

Se ejecutaron los métodos reales `remember`, `loaded`, `unloaded`, `tick` y `stop` de LootRestock del JAR contra APIs simuladas.

| Prueba | Resultado |
|---|---|
| Cofre en x=15, vecino sin cargar, con `setChanged` que detecta la consulta peligrosa | .51 alcanza esa notificación y falla; .52 guarda la tabla sin llamarla |
| Persistencia | Conserva la tabla original y marca el chunk como pendiente de guardado |
| Evento de carga | No registra inmediatamente; lo hace en el tick siguiente, de día |
| Llamada desde hilo trabajador | No modifica NBT allí; se procesa en el tick del servidor |
| Cliente / presupuesto | No drena desde cliente; procesa 128 y retiene el resto |
| Evento de descarga | Retira posiciones en el siguiente tick, sin editar la colección durante el callback |
| Chunk descargado o sustituido | Descarta el registro de la instancia antigua |
| Entidad de bloque antigua | No marca el chunk de reemplazo |
| Parada | Vacía la cola y las colecciones nativas |

Pasaron 18 comprobaciones y el verificador de bytecode. El constructor exige encontrar exactamente una envoltura de remember, cuatro escrituras de metadatos, dos llamadas a execute, un tick, una parada y una validación de chunk de carga.

**No ejecuté Minecraft ni C2ME real.** La prueba reproduce el acceso peligroso con APIs simuladas y demuestra que la .52 evita esa llamada; no certifica todos los escenarios de generación del modpack. Tampoco prueba una sesión prolongada de rellenos nocturnos, guardado/reapertura del mundo o interacción con todos los inventarios modificados.

## Cómo verificar en la partida

1. Usa `/tick rate 20` (el log anterior equivalía a unos 10000 ticks/s y mostraba sobrecarga).
2. Carga zonas con cofres/barriles, especialmente bordes entre chunks; comprueba que no reaparezca la espera de LootRestock del log anterior.
3. Abre y vacía un cofre natural con tabla conocida. Comprueba el relleno nocturno con las reglas/probabilidad configuradas. Los cofres de almacenamiento sin tabla conocida no se convierten en fuentes de botín.
4. Descarga y vuelve a cargar esa zona; verifica también guardado y reapertura. Si hay otro congelamiento, la traza actual permitirá distinguir si sigue pasando por LootRestock.

Este JAR corrige zomboid-hordes; el límite de exploración y el RNG de apariciones de TacZ NPCs están en tactical.59, una entrega separada.

## Archivos

Solo cambia una clase existente: `dev/zomboid/hordes/LootRestock.class`. Se añaden `RestockSafety52.class` y su registro auxiliar `RestockSafety52$Job.class`. Todas las demás entradas de la .51 permanecen idénticas byte por byte. Las APIs simuladas NO se incluyen en el mod.

`fuentes-y-pruebas.zip` contiene RestockSafety52.java, Patch52.java, build.py, test.py, fixtures, resultados y comparación de entradas del JAR. Requiere Java 21, ECJ y ASM. El constructor valida el SHA256 exacto del JAR .51 suministrado: 9b9d12cd6343b8efa327c3a340d61618939535e863ed675e183e2f6364c087b3. Scripts: `build.py --base RUTA_JAR_51 --tools CARPETA_ECJ_ASM`; después `test.py --base RUTA_JAR_51 --tools CARPETA_ECJ_ASM`.
