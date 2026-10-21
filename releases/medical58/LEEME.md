# Corrección 58 — haz isométrico y filtro de cuerpos enlazados

Minecraft 1.21.1 / NeoForge. Sustituye Flashlight .23 por .24 y Gore .38/.39 por .40. Incluye Ragdoll .32 de la optimización 57 para sustituir .31 si aún lo tienes; si ya instalaste .32, es el mismo JAR. No dejes versiones duplicadas. Conserva Dungeons Iso .34 y los otros mods.

## Linterna

El compositor de Flashlight consultaba proyección/vista del evento final, aunque la captura de la escena se hacía antes. Dungeons Iso puede entregar proyección ortográfica a LevelRenderer mediante ModifyArg y conservar otra proyección en el evento final de GameRenderer. La profundidad capturada debe reconstruirse con las matrices de su propia captura.

Ahora se copian proyección y vista al comienzo de la captura y se reutilizan al componer. Se actualizan con cada captura/zoom, se reutiliza el almacenamiento y se descartan al cambiar de mundo. Se conserva la copia GPU a media resolución, la oclusión por bloques, los shaders y el orden de composición de Darkness de .23. No se modifica el JAR isométrico ni se cambia la dirección del arma.

## Cuerpo duplicado

Se añade el filtro de cadáveres enlazados a la decisión compartida por ambas fábricas locales de ragdolls. Antes la segunda fábrica podía crear un ragdoll visual de un jugador muerto además de su Corpse enlazado. Las caídas de un jugador vivo siguen permitidas. Se mantienen las reglas de zombis/NPCs.

El log adjunto no contiene los identificadores/origen del cadáver duplicado. Por ello esta corrección cubre la ruta reproducida en las pruebas, pero no demuestra que fuera la causa exacta de ese cadáver. No borra cadáveres guardados ni fusiona inventarios existentes.

Gore .40 conserva las optimizaciones de .39. El último log aún muestra Gore .38 y Ragdoll .31, por eso el paquete incluye la pareja actualizada.

## Validación

Pasan 8 controles de matrices capturadas, copias independientes, zoom, perspectiva, cambio de mundo y desconexión. Se reproduce la omisión del filtro con el Gore anterior; el nuevo rechaza el segundo cuerpo local de un jugador muerto y permite el derribo vivo. Pasan también 2.017 controles heredados de geometría/caché/frustum. Java 21 y verificación ASM de los JARs modificados.

No se ejecutó Minecraft ni OpenGL con esta entrega. Falta verificar visualmente el haz en tu escena isométrica, con/sin Iris/MakeUp. Las pruebas de matrices usan eventos y matrices simulados. No hay medición nueva de FPS.

## Fuentes

Se incluyen Frame58, CorpseChoice51, parche ASM, pruebas y scripts; también las fuentes/pruebas heredadas de la optimización 57. La compilación local usa Java 21, ECJ y ASM del directorio hordes-melee-interiors-patch/build/tools. Los JARs base y sus hashes figuran en el manifiesto. Los fixtures de pruebas no forman parte de los mods.
