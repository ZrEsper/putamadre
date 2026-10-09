# TacZ NPCs tactical.3 + Zombie Remains Loot 1.6.1

Corrección del movimiento, combate, looteo y compatibilidad de cadáveres de tactical.2. Minecraft 1.21.1, NeoForge 21.1.255 o posterior, TACZ y las dependencias existentes de Zombie Remains Loot, incluido Doomsday Decoration. Selfexpression 2.22a y Fracture Point 3.0.0-PT2 son integraciones opcionales; no se modifican sus JARs.

## Instalar ambos JARs

1. Retirar las otras versiones de TacZ NPCs, incluidas `tacznpcs-2.1.0-1.21.1.jar`, tactical.1 y tactical.2. El log suministrado enumeraba el original y tactical.2 juntos.
2. Retirar `zombie-remains-loot-1.6.0-neoforge-1.21.1.jar`.
3. Instalar `tacznpcs-2.1.0-1.21.1-tactical.3.jar` y `zombie-remains-loot-1.6.1-neoforge-1.21.1-npc-compat.jar` en cliente y servidor. Mantener las dependencias originales y una sola versión de cada mod.
4. Reiniciar Minecraft. No se requiere borrar el mundo ni las configuraciones existentes.

El huevo de camuflaje continúa en creativo y búsqueda. Comando:

    /give @s tacznpcs:bandit_spawn_egg

La plantilla del huevo utiliza bandits, bandit, illagers o default, en ese orden. Mantener una plantilla bandits/illagers para el comportamiento hostil. No se añaden armas artificialmente a quienes aparecen desarmados.

## Movimiento y combate

Los bandidos usan un solo Goal para movimiento, mirada y decisiones. Se retiran de sus selectores las órdenes anteriores de paseo con sprint, disparo, huida durante recarga y persecución cuerpo a cuerpo. Se conserva FloatGoal para nadar. El corte de la rutina antigua de melee impide que vuelva a imponer movimiento a 1.2.

Velocidad base MOVEMENT_SPEED: 0.095, frente a 0.1 de la caminata normal del jugador, sin sprint. Es una referencia a la caminata vanilla: efectos de armaduras, peso, terreno y otros mods pueden cambiar la velocidad efectiva. No se prometen diferencias exactas frente a jugadores con modificadores particulares.

- No hay strafe periódico ni vueltas programadas alrededor del objetivo. El paseo ocasional es de dos bloques y con pausas largas.
- Los armados priorizan zombis visibles y disparan hasta 24 bloques. Se mantienen aproximadamente a ocho bloques; frente a cuatro o más zombis cercanos, buscan unos once bloques de separación. Cada paso atrás está limitado a tres bloques y no vuelve a solicitarse antes de 30 ticks. No corren alejándose indefinidamente y pueden disparar mientras retroceden.
- Los desarmados no cazan jugadores. Se defienden de agresores recientes y zombis cercanos, retroceden con pasos de hasta dos bloques y golpean a corta distancia con swing vanilla sincronizado.
- La inteligencia depende del arma y la cantidad/tipo de equipo: tres niveles máximos. Mejora reacción, detección y cadencia, pero sigue habiendo dispersión y pausas. No se aumentan salud ni daño. La protección nativa de las prendas sigue siendo la de sus mods.
- La detección de jugadores usa visión frontal y distancia; agacharse reduce el rango, e invisibilidad lo limita a cuatro bloques. Al perder visión buscan la última posición vista durante seis segundos, sin seguir coordenadas actuales a través de paredes.
- No disparan si un compañero ocupa la línea de fuego. El daño atribuido a otro bandido aliado se rechaza, evitando muertes y represalias por fuego amigo. El daño de jugadores y zombis continúa permitido.
- Se conserva la recarga y el manejo de armas de TACZ. Los zombis reciben un objetivo adicional contra estos NPCs; las reglas existentes para atacar jugadores se mantienen.

## Looteo y ropa

Los NPCs ociosos buscan cuerpos dentro de doce bloques, tanto los bloques de Zombie Remains Loot como las entidades del mod Corpse. Se acercan caminando, adoptan pose agachada y mueven la mano mientras buscan. Tardan tres segundos por transferencia y toman un objeto por vez. Una reserva temporal evita que dos NPCs saqueen simultáneamente el mismo inventario.

Equipan mejoras de armas, armas cuerpo a cuerpo y armadura sin degradar un arma superior. Lo reemplazado y otros objetos se guardan en una bolsa interna persistente de nueve entradas. Una bolsa llena no borra objetos del cadáver. Se conservan los componentes de ItemStack; una transferencia fallida intenta restaurar origen, equipo y bolsa. Los NPCs interrumpen el saqueo al detectar amenazas. El equipo visual de los cuerpos del mod Corpse también se actualiza y guarda al retirar prendas.

Prendas civiles de Selfexpression: gray_jacket, casual_autum y apocalypsise_survivor. Para NPCs sin armadura previa, la probabilidad de ropa civil es 65%, generalmente parcial. Una tirada de 1% permite un conjunto militar completo de Fracture Point, ratnik o fsb; solo se aplica si las cuatro piezas están realmente registradas. No se crean IDs inexistentes. La selección se marca en NBT para no repetirse al cargar.

## Cadáveres

Zombie Remains Loot 1.6.0 reconocía `tacz_bandits:bandit`, pero no `tacznpcs:npc`. La compatibilidad nueva reconoce ambos en el evento de drops, sin instalar el controlador antiguo de tacz_bandits en estos NPCs.

Los cuerpos nuevos de TacZ NPCs guardan su arma, armaduras, mano secundaria y bolsa interna antes de generar botín adicional. Ese contenido tiene prioridad y no se sustituye por LootExtras. Se conserva el inventario persistente de 27 espacios, los estados de los restos y las reglas originales para zombis y otros bandidos. Si las reglas originales no encuentran un bloque de suelo apto para colocar un cuerpo, pueden seguir dejando drops normales; no se fuerza la colocación dentro de agua o bloques sólidos.

## Reproducción

Python 3 y Java 21. ECJ y ASM se incluyen en tools/ con sus licencias dentro de sus JARs; SHA-256 de herramientas y bases se verifica antes del build. Extraer las fuentes y ejecutar:

    python3 -B build.py /ruta/tacznpcs-2.1.0-1.21.1-tactical.1.jar /ruta/zombie-remains-loot-1.6.0-neoforge-1.21.1.jar

Los dos JARs de base se conservan junto a la documentación del repositorio únicamente para reconstrucción, no para instalar junto a las versiones nuevas. Salidas en output/. src/ implementa el controlador, huevo y looteo; remains-src/ implementa la compatibilidad; Patch.java define las transformaciones. Los dobles de test-src/ y stubs/ no se empaquetan en los mods.

## Evidencia y límites

Pasaron 1176 aserciones con dobles de API, incluyendo instalación idempotente de un solo controlador, velocidad base, combate contra zombis, distancia limitada ante multitudes, ausencia de círculos en una posición estable, defensa/animación desarmada, recarga, memoria, fuego amigo, looteo temporizado y reservas, conservación de objetos/componentes, rollback, bolsa llena, persistencia, ropa civil, conjunto militar, inventario del cadáver y huevo en creativo/búsqueda. Se analizaron 138 métodos de las cinco clases modificadas con ASM BasicVerifier. Los helpers se ejecutan con `-Xverify:all`, incluido el helper real BanditBrain parcheado para las mejoras de armas.

En el JAR de NPCs solo cambian seis entradas preexistentes; en Remains, dos. El resto del contenido de cada base se conserva, incluyendo plantillas, recursos, spawn en estructuras, ledger regional y controladores de otros tipos de bandidos. El spawn regional sigue respetando Pacífico, doMobSpawning y supervivencia, y conserva la corrección de ensanchamiento numérico de coordenadas.

No se ha ejecutado Minecraft, NeoForge ni el modpack completo. Las simulaciones no comprueban pathfinding físico, render de prendas, interacción real de PlayerAnimator/TACZ ni integración de todos los mods del log. Antes de considerar validado en juego, probar con una sola versión de cada mod: spawn por huevo, combate con uno y varios zombis, fuego amigo, defensa desarmada, saqueo visible, muerte/cadáver, guardado/reapertura y ropa. Registrar latest.log después de esas pruebas. El error de registros de biomas de fabulous-furnished que aparece en el log suministrado es independiente de estos parches y no se modifica aquí.
