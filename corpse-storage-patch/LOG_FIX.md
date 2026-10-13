# Corrección del arranque: Quietly v15

El log recibido corresponde a Minecraft 1.21.1 / NeoForge 21.1.255. El error que detiene el arranque es:

```text
InjectionError: Critical injection failure: Redirector quietly$expanded(...)
in quietly.mixins.json:ExpandedChestMenuMixin from mod quietly
failed injection check, (0/1) succeeded. Scanned 0 target(s). No refMap loaded.
```

La v14 añadió un redirect al flujo de apertura de `ServerPlayer`. No se encontró su punto de inyección en el entorno real del usuario. La comprobación de bytecode y las pruebas con dobles de API anteriores no comprobaron esa aplicación real del mixin.

Quietly **v15** retira `ExpandedChestMenuMixin` y `ExpandedChestDropsMixin` por completo. `quietly.mixins.json` se conserva byte por byte como en la v13 enviada. La apertura paginada usa `PlayerContainerEvent.Open`; el contenido adicional al romper un cofre se conserva mediante `BlockDropsEvent`. Ambos son eventos documentados de NeoForge para Minecraft 1.21.1. El evento Open se publica después de que el servidor haya asignado el menú abierto, según el parche oficial de ServerPlayer. El reemplazo valida esa identidad, cierra el menú nativo y abre el paginado con su tipo vanilla correspondiente; reconoce su propio menú para impedir la recursión y conserva un único contador de apertura.

Fuentes oficiales consultadas:

- https://github.com/neoforged/NeoForge/blob/1.21.1/src/main/java/net/neoforged/neoforge/event/entity/player/PlayerContainerEvent.java
- https://github.com/neoforged/NeoForge/blob/1.21.1/src/main/java/net/neoforged/neoforge/event/level/BlockDropsEvent.java
- https://github.com/neoforged/NeoForge/blob/1.21.1/patches/net/minecraft/server/level/ServerPlayer.java.patch

No se sustituyen errores de inyección obligatoria por `require=0`; se elimina esa integración y se utiliza la API de eventos. Se mantienen el almacenamiento sin desbordamiento, las páginas, los componentes, los contornos y los datos `QuietlyOverflow14` de mundos anteriores. Remains .7, NPC tactical.14 y Dungeons Iso .27 conservan exactamente los JAR previamente publicados.

Validación: 164 comprobaciones, incluidas 26 nuevas del flujo de eventos, recursión, contadores, nombres de contenedor, cofres dobles, drops al romper y persistencia. ASM verifica los métodos, anotaciones del subscriber y descriptores de sus callbacks. El empaquetado exige que el archivo de mixins coincida con el original y que las dos clases retiradas no aparezcan en el JAR. Integridad ZIP y hashes verificados.

No se ha ejecutado Minecraft ni el modpack completo en este entorno. Estos resultados verifican la corrección y sus adaptadores con dobles, y la ausencia del mixin que causó el cierre; falta comprobar el nuevo arranque y las pantallas en la instalación del usuario. Los avisos de compatibilidad opcional de TACZ/Backpacks del log no son la excepción fatal indicada arriba.

Para actualizar, retirar **Quietly v14** y colocar **quietly-1_0_0-cooldown-v15-neoforge-menu-fix.jar**, en cliente y servidor. No duplicar Quietly. Los otros tres reemplazos no necesitan actualizarse.
