# TacZ NPCs tactical.2 — Minecraft 1.21.1 / NeoForge

Parche sobre el JAR táctico suministrado, conservando las dependencias originales (NeoForge y TACZ). Instalar solo una versión de TacZ NPCs: retirar el original y tactical.1; colocar tactical.2 en cliente y servidor. No se modifica la configuración de plantillas del mundo.

## Huevo

ID: `tacznpcs:bandit_spawn_egg`. Aparece en la pestaña de huevos de creativo y en la búsqueda. Textura original de camuflaje con transparencia, adaptada a 32×32 para el atlas de Minecraft. Comando:

    /give @s tacznpcs:bandit_spawn_egg

Utiliza el tipo de entidad existente `tacznpcs:npc`, con componente ENTITY_DATA. Selecciona las plantillas configuradas en este orden: bandits, bandit, illagers, default. El fallback default solo evita un fallo si se eliminaron todas las plantillas hostiles; para obtener bandidos mantener bandits o illagers configurados. El registro del huevo usa DeferredSpawnEggItem de NeoForge 1.21.1, conservando el manejo de dispensadores del framework.

## Cambios de combate

- Reacción inicial individual de 6–14 ticks (0,3–0,7 segundos a 20 TPS).
- Tras perder visión se elimina el objetivo directo y se navega a su última posición vista durante un máximo de 160 ticks (8 segundos). No se actualiza esa posición a través de paredes. Recuperar contacto exige una nueva reacción corta.
- Puntería al torso con dispersión variable según distancia e individuo, reemplazando el ciclo fijo de aciertos/fallos de tactical.1. Pitch y yaw usan la misma muestra dentro del tick.
- Movimiento lateral intermitente durante combate visible entre 4 y 24 bloques; no sustituye la retirada existente ni el movimiento durante recarga.
- Alcance del goal de disparo: 24 bloques, antes 8.
- Jugadores creativos, espectadores y objetivos muertos se excluyen del combate táctico.

## Spawn existente

Se conserva sin modificaciones la clase de reemplazos en estructuras y el ledger regional persistente. Se corrige la llamada reflectiva que pasaba la altura entera a `moveTo(double,double,double,float,float)`, aceptando el ensanchamiento numérico que admite Java. Los encuentros regionales respetan `doMobSpawning`, Pacífico y jugadores de supervivencia. El huevo permite aparición manual independientemente de esas condiciones naturales.

## Reproducir

Extrae `tacznpcs-tactical.2-fuentes.zip`. La base de reconstrucción está en `base/tacznpcs-2.1.0-1.21.1-tactical.1.jar`; no debe instalarse junto al JAR nuevo.

Python 3 y Java 21. ECJ y ASM están incluidos en tools/ con sus licencias dentro de sus JARs; sus SHA-256 se verifican en cada build. El JAR base también se verifica por SHA-256.

    python3 -B build.py /ruta/tacznpcs-2.1.0-1.21.1-tactical.1.jar

Salida: output/tacznpcs-2.1.0-1.21.1-tactical.2.jar. src/ contiene los añadidos; Patch.java especifica los cambios de bytecode; test-src/ y stubs/ son exclusivamente dobles de API y no se empaquetan en el mod. El mod original declara licencia MIT y autor Corbin Smith; se conservan sus metadatos y requisitos.

## Validación y límites

Pasaron 1038 aserciones de registro del huevo, componente de plantilla, creativo/búsqueda, selección de plantilla, reacción, percepción, memoria, movimiento, dispersión, condiciones de aparición y ensanchamiento numérico. Las pruebas ejecutan las clases nuevas y el helper real BanditBrain parcheado con `-Xverify:all` y dobles de API. ASM BasicVerifier analiza 105 métodos de las clases modificadas. Se comprueba que solo cambien seis entradas preexistentes y que no se incluyan clases de prueba.

No se ha arrancado Minecraft, NeoForge ni el modpack completo. Las pruebas con dobles no demuestran integración real del registro, dispensadores, animaciones, TACZ o pathfinding. No se garantiza que produzca miedo ni que imite a una persona: la percepción y navegación siguen limitadas por Minecraft y TACZ.

Prueba en juego: abrir creativo y buscar «bandido»; obtener el huevo por comando; usarlo sobre suelo sólido y desde un dispensador; comprobar arma y aspecto. En supervivencia, observar el retraso de reacción y disparo a distintas distancias; romper visión tras una pared y cambiar de posición; observar búsqueda de la última posición. Comprobar retirada ante zombis, recarga, spawn regional con doMobSpawning true/false y Pacífico, y guardar/reabrir. Si falla, conservar latest.log para diagnosticarlo.
