# Corrección del cierre al aparecer un NPC · Hordes .34

## Instalar

Cierra Minecraft y el servidor. En la carpeta `mods`, retira Hordes .33 y pon `zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.34-npc-mixin-fix.jar`. No dejes dos versiones de Hordes. Haz lo mismo en servidor y clientes.

Conserva los otros cuatro mods del paquete anterior: Remains .10, TacZ NPCs .16, FirstAid 3.5.8 y FracturePoint NVG .1. No necesitas sustituirlos de nuevo si ya los tienes.

El paquete completo .34 contiene esos mismos cuatro mods y Hordes corregido, por si prefieres instalar todo junto. La guía general del paquete sigue explicando sus controles y cambios.

## Qué pasó

El log termina con un fallo crítico de `MilitaryBanditBalanceMixin`, el ajuste de bandidos militares de Hordes. La actualización NPC .16 conserva la función original de equipamiento con el nombre `originalOutfit33` y deja `outfit` como entrada al nuevo sistema de escasez. Hordes buscaba la tirada aleatoria militar dentro de `outfit`, donde ya no existe, y Mixin detenía el juego.

Ahora el ajuste reconoce ambos nombres. Sigue encontrando la tirada militar original, tanto en NPC .15 como en .16. Mantiene obligatorio encontrarla: no se oculta el fallo desactivando la comprobación. Solo se modifica la anotación del destino de esa conexión; las instrucciones de todos los métodos y el resto del JAR se conservan.

## Comprobaciones

La prueba reproduce el fallo original: cero destinos válidos para el ajuste militar en la pareja Hordes .33 + NPC .16. Con Hordes .34, verifica las tres inyecciones Hordes/NPC con NPC .16 y también con NPC .15. Comprueba selectores, llamadas y ordinales; las instrucciones ejecutables del mixin no cambian. Todos los demás archivos del JAR son idénticos a .33.

No se ha ejecutado Minecraft ni su transformador Mixin completo en este entorno. Falta confirmar el arranque y la aparición de NPC en tu instalación. Los avisos de recursos, sonidos y metadatos de packs que aparecen antes en el log son problemas distintos del cierre identificado.
