# Survivor Creator v19: mapa de aparición y música de introducción

Parte del JAR v18-stamina enviado por el usuario, SHA256 611180934cd863cd7dd9f85d182084d4bd1c9f0fc758d544e89a76e176cdc501. Mantiene sus perfiles, rasgos, profesiones y cambios de estamina.

## Flujo

Tras confirmar el personaje se abre el mapa dibujado de Mosslorn, inspirado en mapas de supervivencia. Cuatro sectores: residencial noroeste/noreste y ciudad suroeste/sureste. Seleccionar con botones, marcadores o teclas 1–4 y comenzar con Enter. Se envía la profesión/rasgos elegidos y un identificador de sector; el servidor elimina ese identificador antes de validar y guardar el personaje. El cliente no puede enviar coordenadas arbitrarias.

Hay 22 posiciones candidatas (6/6/4/6), obtenidas de los archivos de mundo enviados: suelo, dos bloques libres, techo próximo y paredes alrededor de un contenedor. Noroeste/noreste/suroeste usan contenedores de categoría vivienda; sureste incorpora interiores de oficina porque no se encontraron suficientes posiciones en viviendas de esa categoría. No todas son casas independientes: hay interiores de varias plantas. Catálogo completo en homes.json.

El servidor elige aleatoriamente entre posiciones disponibles en el sector y verifica suelo, espacio, ausencia de agua y monstruos en un área de 16 bloques horizontal/8 vertical. Si no hay lugar válido, rechaza antes de crear el personaje y permite elegir otra zona. Revalida antes del traslado y conserva la posición actual si entretanto deja de ser válida. No construye casas ni elimina criaturas. Se mantiene la protección inicial nativa de 200 ticks. Después de ese período, la zona puede volver a ser peligrosa.

La aparición es una vez por personaje nuevo, incluida una nueva creación tras morir según el flujo nativo. Editar un personaje completado no permite teletransportarse repetidamente. Los interiores conservan sus contenedores e inventarios existentes: no se rellenan ni borran. Se añaden personalmente 2 panes, 2 manzanas y 4 antorchas una vez al crear; si no caben, se sueltan en el lugar.

## Música

La v18 hacía que playEndingMusic reutilizara CreationAudio.track(), que selecciona la música del menú incluso en la pantalla final. Se añadió una pista separada para EndingScreen cuando no es una muerte, y CreationAudio deja de reiniciar la música de creación en esa pantalla. Se conserva la música de muerte y de creación original.

El audio del usuario Project Zomboid Intro.ogg sustituye ending_theme.ogg, sin recodificación: Vorbis, 44.100 Hz, estéreo, 36,618 segundos. Se reproduce cuando aparecen los mensajes finales del personaje. Al abandonar esa pantalla, se detiene como en el flujo original. Requiere volumen de música y volumen maestro distintos de cero.

## Instalación

Cerrar Minecraft/servidor y guardar una copia del mundo. Reemplazar el JAR v18 por survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v19-spawn-map-intro.jar en mods. Dejar solo una versión de Survivor Creator e instalar la misma en cliente y servidor. No necesita un resourcepack aparte: textura del mapa y sonido están incluidos. No reconvertir el mundo.

Preparado para las coordenadas del mundo Mosslorn enviado, dividido por X=0 y Z=192. En otro mundo esas ubicaciones pueden no existir y se rechaza la selección cuando no sean seguras. El mapa dibujado y sus marcadores representan sectores aproximados; no constituyen un mapa GPS exacto de las posiciones candidatas.

## Validación

13 pruebas del helper real con dobles de API: catálogos, marcadores válidos/duplicados/inválidos, conservación de perks, rechazo por monstruos y espacio bloqueado. Comprobación ASM de pila y flujo de 125 métodos de las cuatro clases parcheadas. Todos los demás archivos originales se conservan byte por byte, excepto el audio final sustituido. No se empaquetan clases de API falsas ni de prueba.

No se ejecutó Minecraft: faltan comprobaciones reales de renderizado, audio, métodos de las APIs y teletransporte en el modpack. Validar creación, muerte/recreación y una zona ocupada antes de usarlo en un servidor habitual.

## Reconstruir

Java 21, Python 3 y ECJ/ASM. `python build.py RUTA_V18.jar CARPETA_TOOLS` comprueba el SHA256 de base y herramientas y ejecuta compilación, parche y pruebas. Usar herramientas del ZIP de fuentes incluido (tools/ecj.jar y asm*.jar), verificadas con tools.sha256.json. Las clases de stubs/tests no se incluyen en el mod.
