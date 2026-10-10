# Quietly v5: interacción amarilla, Jade y bloqueo de traslado

Reemplazo del Quietly cooldown-v4 enviado. Jade 15.10.6 se conserva sin modificar.

## Interacción

- Contorno amarillo del bloque que se mira para contenedores con inventario/menú, cofres de Ender, puertas, trampillas y portones. Usa la forma nativa del bloque.
- Silueta amarilla de la entidad apuntada si es un contenedor/menú o CorpseEntity del mod Corpse de Max Henkel. No añade brillo persistente en el servidor ni marca entidades fuera de la mira.
- En Jade, conserva el nombre nativo y añade `[tecla asignada a Usar] Abrir / interactuar`, o abrir/cerrar para puertas. Lee el binding real; no impone E ni botón derecho.
- Vista breve por categorías, hasta tres: medicina, alimentos/bebidas, ropa/equipo, herramientas, electrónica, armas/munición y materiales/provisiones.
- Contenedores con LootTable pendiente no se leen con getItem: no se genera loot por mirar. Si la tabla conocida de Mosslorn permite inferir la categoría, dice «Posibles…»; en otros casos «Contenido por revisar».
- Respeta los bloqueos de contenedores: no revela su contenido. Los cofres de Ender solo indican almacenamiento personal. Para Corpse comprueba onlyOwnerAccess, propietario, permisos y skeletonAccess antes de mostrar categorías del equipo visible.

Jade puede conservar su propia vista de iconos de almacenamiento, configurable en sus ajustes. El añadido de esta revisión es una línea breve por categorías; no reemplaza todo Jade ni altera su JAR.

## Cooldown

La v4 añadía el cooldown después de clicked/drop, pero no cancelaba el próximo intento. Ahora las inyecciones previas son cancelables y revisan el objeto del slot/cursor antes de cambiar el inventario. Se aplican al cliente para evitar predicción visual y al servidor para impedir saltarse el bloqueo.

Se cubren pickup, Shift/quick move, teclas numéricas, offhand dentro del inventario, arrastre, recoger todas las pilas, soltar desde menú, soltar desde mano y F/intercambio entre manos. Objetos que no enfrían siguen moviéndose. Cuando ItemCooldowns deja de marcar el tipo, se permite moverlo otra vez. Se conservan los tiempos originales de v4, el seguimiento de cooldowns, apertura silenciosa y sus animaciones.

El cooldown vanilla es por tipo de Item, por lo que distintas pilas del mismo tipo comparten el bloqueo. También se respetan cooldowns existentes de otros usos. CLONE creativo y FakePlayer conservan las excepciones del flujo original. No bloquea cierre automático de menús, tolvas ni operaciones directas de otros mods que no pasen por clicked/drop/handlePlayerAction.

## Instalar

Cerrar Minecraft/servidor. Reemplazar quietly-1_0_0-cooldown-v4.jar por quietly-1_0_0-cooldown-v5-interaction-jade.jar en mods y dejar solo una versión de Quietly. Conservar Jade-1.21.1-NeoForge-15.10.6.jar.

En multijugador, instalar Quietly v5 y Jade en cliente y servidor para la vista breve del contenido. Sin Jade, Quietly mantiene el bloqueo y el contorno, pero no puede añadir el texto a su HUD.

No se cambia el mundo ni sus inventarios. La detección genérica cubre Container/MenuProvider; otros mods que implementen interacciones propias necesitarán adaptadores. El adaptador de cadáveres conocido es de.maxhenkel.corpse.entities.CorpseEntity; muestra categorías de su equipo, sin prometer acceso a todo su inventario interno.

## Validación y límites

30 casos del helper real con dobles de API, cubriendo tipos de clic, objeto en cursor, mano secundaria, soltar, vencimiento, loot pendiente sin lectura de slots y protección de contenido. Se valida que las dos inyecciones modificadas son cancelables. ASM comprueba pila/flujo de 6 métodos originales parcheados y 42 métodos añadidos. Entradas ajenas del JAR se conservan byte por byte; no se empaquetan stubs ni clases de prueba.

No se ejecutó Minecraft: deben comprobarse los objetivos de los mixins, contorno con el renderer del modpack, detección de cadáveres y sincronización de inventario en una prueba real. Los shaders/renderizadores personalizados pueden afectar a los contornos nativos.

## Reconstrucción

Java 21, Python 3, ECJ y ASM. `python build.py BASE_QUIETLY_V4.jar JADE_15.10.6.jar CARPETA_TOOLS`. Se verifican SHA256 de entradas y herramientas; compila, parchea y prueba antes de generar output/quietly-1_0_0-cooldown-v5-interaction-jade.jar. El ZIP de fuentes incluye esas entradas y herramientas para reproducirlo.
