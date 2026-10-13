# Cadáveres con contorno y botín sin desbordamiento

Reemplazos construidos sobre los cuatro JAR del usuario: Quietly v13, Remains .6, TacZ NPCs tactical.13 y **Dungeons Iso .26 mounted-aim-side-zoom**. La .25 anterior no se usa como base.

La revisión **Quietly v15** corrige el cierre al arrancar registrado en el log del usuario: usa los eventos oficiales de NeoForge para las páginas y la rotura de cofres, sin los dos mixins nuevos de v14. El formato de guardado sigue siendo compatible. [Diagnóstico y verificación](LOG_FIX.md). Si ya instalaste el paquete anterior, solo sustituye Quietly v14 por v15; los otros tres JAR son idénticos.

## Instalación

Cerrar Minecraft y el servidor, hacer una copia del mundo y sustituir las versiones anteriores. Una sola versión de cada mod:

- `quietly-1_0_0-cooldown-v15-neoforge-menu-fix.jar`
- `zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.7-npc-full-cargo.jar`
- `tacznpcs-2.1.0-1.21.1-tactical.14-corpse-cargo.jar`
- `dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.27-corpse-outline.jar`

Quietly, Remains y TacZ NPCs deben actualizarse **juntos en cliente y servidor**. Dungeons Iso .27 reemplaza .26 en el cliente y en los lugares donde ya estaba instalado. Conservar las demás dependencias del modpack. No instalar los JAR de `base/`: son entradas de reconstrucción.

## Cambios

**Contorno y apertura.** Quietly reconoce también los cuerpos de bloque de Doomsday y los restos esqueléticos de Remains. Los cadáveres de entidad de Corpse conservan su silueta amarilla al apuntarlos. En vista isométrica con arma, el objetivo visual pasa a ser el cadáver que selecciona el cursor, con las comprobaciones existentes de alcance y obstáculos. Se conserva el clic derecho normal, el sistema de búsqueda de Quietly, las interacciones originales y las protecciones de acceso. Los cuerpos cuyo modelo no devuelve una forma de contorno reciben una forma baja de respaldo. El helper de cámara solo hace una búsqueda adicional con arma, como máximo cada 50 ms; sin arma se conserva el selector anterior. Monturas y otros controles nativos de .26 no se interceptan.

**Quietly.** El botín adicional de búsqueda primero combina pilas con el mismo objeto y componentes, después ocupa huecos libres. Se consideran ambas mitades de un cofre doble. Si no cabe, se añaden filas de nueve espacios al mismo cofre o barril, sin reemplazar sus bloques. El nuevo menú usa seis filas: cinco de objetos y una de navegación, con flechas para acceder a las páginas. Los controles no se pueden retirar ni usar como espacios de almacenamiento. El resto de pilas permanece dentro del contenedor; no se llama a `Player.drop` por desbordamiento. En Sophisticated Storage se conserva su mecanismo existente `changeStorageSize` para añadir espacios. Los inventarios ampliables de Remains absorben el sobrante y Corpse conserva su lista adicional de objetos.

Los huecos extra de cofres/barriles vanilla quedan guardados en `NeoForgeData.QuietlyOverflow14`, con índices enteros, dentro de la misma entidad de bloque. Se mantiene el inventario nativo para las tolvas y automatización; estas acceden a sus huecos originales, mientras el menú paginado permite acceder a todos. No se convierte un cofre con filas adicionales en otro bloque durante el hallazgo, para preservar esas filas. **Romper deliberadamente un cofre conserva el comportamiento normal de soltar su contenido**, incluidos los huecos adicionales: este cambio evita que buscar botín lo tire por falta de espacio.

**NPC.** Se conservan todas las entradas de mochila, sin el límite anterior de nueve, además del arma, armadura, mano secundaria, linterna guardada, objeto desplazado de la mano secundaria y drops adicionales capturados por NeoForge. El equipo ya copiado desde las manos/armadura se descuenta una vez de los drops capturados para evitar duplicados; el contenido y componentes de cada pila se conservan. Remains amplía el cadáver solo cuando falta sitio. Se conservan sus generadores de botín de calidad, militares y addons. La .14 evita lanzar objetos antes de que NeoForge capture los drops de muerte.

Si no existe un punto válido para colocar el cuerpo, o el mundo rechaza su colocación, se guarda una entrega pendiente en el SavedData nativo de Remains bajo `NpcCorpseDeliveries14`. No se generan objetos flotantes. Se intenta crear el cuerpo cerca de la muerte, sin cargar chunks ni sobrescribir otro cadáver; las entregas pendientes se reintentan con un máximo de dos búsquedas por dimensión por segundo y se conservan al guardar y cargar. El cuerpo aparecerá cuando haya un punto válido disponible cerca. La mochila/equipo permanecen intactos hasta que el evento queda registrado como entregado. El parche de NPC tiene un fallback al comportamiento anterior si falta el nuevo Remains; para obtener el comportamiento sin objetos sueltos hay que instalar ambos reemplazos.

Conservar esta versión de Quietly/Remains o una posterior compatible al guardar inventarios ampliados o entregas pendientes. Las versiones antiguas no conocen los datos adicionales. Los identificadores y versiones internas de los mods se mantienen para conservar los requisitos de las dependencias; los nombres de los JAR distinguen los reemplazos.

## Verificación

164 comprobaciones con los helpers reales y dobles explícitos de las APIs del juego:

- 26 de los eventos oficiales de apertura/rotura, identidad del menú, recursión, contadores, títulos, cofres dobles y conservación del contenido al romper.
- 54 de inserción nativa, componentes, pilas, cofres dobles, ampliación, páginas, acceso válido, guardado de índices mayores de 255 y conservación al romper deliberadamente el cofre.
- 17 de mochila completa de 150 entradas, equipo almacenado, deduplicación del equipo capturado y codificación de entregas pendientes.
- 13 del reconocimiento real de bloques y entidades de cadáver por Quietly.
- 13 con las clases originales parcheadas de Remains: constructor, ampliación y guardado/carga real de cadáveres y entregas pendientes.
- 27 del flujo de muerte, entrega completa, evento repetido, falta de terreno, colocación rechazada, recuperación tras guardar y reintentos limitados. El mundo y los generadores de loot son dobles de API; el helper ejecutado es el de producción.
- 14 del helper real de cursor: arma, pantallas, monturas, cambio de mundo, alcance y limitación de búsquedas; cámara y selector preexistente sustituidos por dobles.

ASM BasicVerifier verifica los 2.361 métodos de los cuatro JAR y la retención CLASS de sus mixins. Se verifican también la integridad ZIP, los SHA-256 y la conservación byte por byte de todas las entradas originales ajenas al cambio. Ningún stub ni clase de prueba se incluye en los mods. Las clases de almacenamiento de Quietly y de entrega de Remains tienen paquetes separados.

**No se ejecutó Minecraft.** Falta comprobar el arranque con el modpack, la aplicación real de los nuevos mixins, el contorno con su renderer/shaders, las pantallas y la sincronización multijugador. Prueba en una copia del mundo: apuntar a cuerpos con/sin arma y lootear con clic derecho; buscar en un cofre lleno y recorrer sus páginas; matar un NPC con mochila/equipo, revisar el cuerpo y volver a entrar al mundo. La comprobación contra dobles no sustituye esa prueba real.

## Reconstrucción

Java 21 y Python 3:

```sh
python3 -B corpse-storage-patch/build.py
```

El script verifica las bases de `base/` con `inputs.sha256.json`, descarga ECJ 3.39.0 y ASM 9.7.1 desde Maven Central con TLS y SHA-256 fijados, genera los parches, ejecuta todas las comprobaciones y, solo si pasan, copia los cuatro JAR a `../mods/`. `build/` y `output/` son salidas locales ignoradas. `validation.json` registra el resultado y los hashes.
