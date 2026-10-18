# Survivor Creator v35 — aparición adaptable al mundo

Reemplaza v34 por este JAR en mods. Instálalo también en el servidor si juegas en LAN o dedicado; no dejes ambas versiones. Conserva la linterna/batería inicial, profesiones, rasgos y habilidades de v34.

La selección comienza en «Automático: mundo actual». Confirma tu personaje normalmente. Puedes mantener las opciones de zonas originales: sus casas solo se usan si el terreno cargado contiene el contenedor del catálogo y una ubicación segura. En otro mapa se buscan posiciones aleatorias en el terreno actual, alrededor del spawn del mundo y del jugador.

La búsqueda comprueba borde del mundo, suelo con soporte superior, espacio libre para pies/cabeza, fluidos y bloques peligrosos. Prefiere lugares sin monstruos próximos. Usa solo chunks ya cargados, con cantidades y tiempo de búsqueda limitados; no intenta generar cientos de chunks en las coordenadas de Mosslorn.

Si no hay ningún lugar seguro dentro del terreno disponible, la creación se completa conservando la posición vanilla y se da la protección inicial. No construye plataformas ni garantiza terreno seguro en mapas vacíos. Si solo hay suelo seguro con enemigos cerca, usa ese suelo con la protección inicial existente. Un personaje ya creado no se vuelve a trasladar ni recibe el equipo otra vez por reenviar la confirmación.

No se identifica Mosslorn por el nombre de la carpeta: se verifican contenedores y suelo del catálogo en chunks cargados. Las casas de regiones que todavía no están cargadas no se cargan de forma forzada; en ese caso se usa la búsqueda del mundo actual. El mapa estático de Mosslorn se oculta al seleccionar Automático.

## Diagnóstico
El código anterior siempre recorría casas del CSV de Mosslorn y llamaba getChunk para cada intento. Un mundo distinto podía agotar la lista y rechazar la creación. El latest.log adjunto muestra retrasos de unos 6–7 segundos; no contiene una traza que permita atribuir todos esos retrasos a Survivor Creator. Este parche elimina la búsqueda forzada de chunks de ese flujo.

Busca `[SurvivorCreator] Adaptive spawn:` en latest.log para comprobar el modo elegido y las coordenadas.

## Comprobación en juego
1. Crea un mundo normal nuevo y confirma con Automático: debe cerrar el menú y aparecer en terreno válido.
2. Repite en un mapa importado distinto y, después, en Mosslorn.
3. Comprueba un jugador nuevo en LAN/servidor.
4. Verifica que conserva profesión/rasgos y entrega el equipo inicial una sola vez.
5. En mapas vacíos, la creación no queda bloqueada; se conserva la posición vanilla.

## Fuentes y compilación
JDK 21 y Python 3:
```
python build.py "ruta/al/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v34-flashlight-battery.jar" "survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v35-adaptive-world-spawn.jar"
```
`src/` contiene la implementación completa nueva y Zones; Patch.java aplica las entradas de búsqueda/colocación y la selección automática a las clases originales. Se conservan todas las demás clases del JAR base. `tests/` usa dobles de terreno para verificar el algoritmo sin Minecraft.

Comprobado: compilación, análisis de bytecode ASM, suelo/agua/magma/colisiones/borde, rechazo de chunks no cargados, aparición en mundo nuevo, conservación de rasgos y protección de personajes ya creados, fallback sin terreno. Pendiente: ejecución real con Minecraft y el modpack completo.
