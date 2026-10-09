# TacZ NPCs tactical.5: civiles, profesiones y goals vanilla

Minecraft 1.21.1 / NeoForge, TACZ y **Self Expression 2.22a**. Sustituir todas las versiones anteriores de TacZ NPCs por `tacznpcs-2.1.0-1.21.1-tactical.5.jar`, en cliente y servidor. Mantener Zombie Remains Loot 1.6.1 npc-compat, Doomsday Decoration y las demás dependencias existentes. Fracture Point continúa siendo opcional para el conjunto militar raro. No borrar el mundo ni la configuración. **Los NPCs existentes también migran al nuevo vestuario al cargar.**

## Ropa y profesión

Las plantillas originales equipaban cuero/hierro y el parche anterior solo vestía civiles si estaban sin armadura. Esta entrega sustituye la armadura de plantilla por Self Expression, aunque el NPC tenga la marca de vestuario de .3/.4. El uniforme y la profesión se guardan; no cambian en cada carga. Las piezas ausentes del uniforme dejan el espacio libre, sin completar con armadura vanilla. Las prendas y herramientas reemplazadas se conservan en la bolsa; si está llena, se dejan como objetos en el mundo.

| Uniforme de Self Expression | Profesión | Herramienta |
| --- | --- | --- |
| builder | Constructor | Pico de piedra |
| miner | Minero | Pico de hierro |
| doctor | Médico | Tijeras |
| woodworker | Carpintero | Hacha de hierro |
| fire | Bombero | Hacha de hierro |
| courier | Mensajero | Espada de madera |
| police | Policía | Espada de hierro |
| official | Oficinista | Espada de madera |
| medieval_farmer | Agricultor | Azada de piedra |
| gray_jacket | Civil | Espada de piedra |
| casual_autum | Civil | Espada de madera |
| apocalypsise_survivor | Superviviente | Hacha de piedra |

Se verificaron los doce conjuntos contra el JAR exacto suministrado: SHA256 `8d8772bb9a5b1d33916295a3f98c082a6e488196458d1924a21a1591581cad46`. Algunas profesiones no tienen casco, por diseño del mod. El arma de fuego existente se conserva; su herramienta se lleva en la mano secundaria si está libre, o en la bolsa si hay espacio. Quienes no llevan arma de fuego usan la herramienta de su profesión para defenderse. La profesión representa uniforme y herramienta; no se añaden rutinas de minería, medicina o construcción automática.

Se mantiene la probabilidad 1% de un conjunto militar completo de Fracture Point y se preservan los conjuntos militares completos existentes. Sin Self Expression instalado no se inventan objetos; se conserva el equipo hasta que la integración esté disponible.

## Movimiento y combate

Se reemplaza el Goal que monopolizaba MOVE y LOOK por decisiones que solo reservan TARGET. Goals independientes se encargan de disparar, golpear, recuperarse de una ruta fallida en agua, saquear, pasear y observar. El selector vanilla resuelve qué tarea puede usar movimiento/mirada.

Los pillagers usan un goal de ballesta. Para conservar las armas TACZ, esta entrega añade la interfaz vanilla `RangedAttackMob` al NPC y adapta **`RangedAttackGoal` de Minecraft** para navegación, mirada y cadencia, con el disparo TACZ como callback. **`MeleeAttackGoal`** controla persecución, alcance y golpes con swing al llevar herramientas o ir desarmado. El paseo utiliza **`WaterAvoidingRandomStrollGoal`**, con **`LookAtPlayerGoal`** y **`RandomLookAroundGoal`** para observación ociosa. Los goals que se ejecutan son los de la versión de Minecraft instalada.

- Base de movimiento 0.30. Paseo con factor 0.85 y persecución con factor 1.1. Se permite sprint hacia amenazas distantes: más de 18 bloques con arma de fuego, más de cuatro en combate cuerpo a cuerpo. No se impone permanentemente `setSprinting(false)`.
- Se retiran del atributo de estos NPCs los identificadores de penalización de peso/movimiento de arma declarados por TACZ (`WEIGHT_SPEED_MODIFIER*`, `EXTRA_SPEED_MODIFIER*`). Se conservan otros modificadores de armadura, pociones y sprint. Esto evita que apuntado/peso vuelva a reducir la velocidad tras ajustar la base. No se afirma una diferencia física exacta del 5% frente al jugador.
- Navegación flotante y FloatGoal conservados. Una ruta fallida en agua puede solicitar movimiento directo al control; en combate cuerpo a cuerpo existe un Goal específico para ese caso. No se agachan dentro del agua. Estas medidas requieren comprobación física en el escenario inundado del modpack.
- El atacante reciente tiene prioridad sobre zombis y el foco anterior. Los zombis lejanos/distraídos no impiden recoger botín. Los civiles sin armas de fuego no cazan jugadores; se defienden con la herramienta o a puños cuando son atacados o un zombi se acerca.
- El rango de posicionamiento del ataque a distancia es 18 bloques. Se gira cabeza/cuerpo antes de disparar; los tiros se limitan a 24 bloques y requieren visión y orientación. Sigue habiendo dispersión, recarga TACZ y protección entre aliados. Solo hay una retirada corta cuando un zombi entra a menos de 4.5 bloques, sin movimiento lateral periódico.
- La reacción mejora con equipo y experiencia limitada de saqueo. No se aumenta salud ni daño base. Los atributos nativos de herramientas y prendas siguen aplicándose.

## Saqueo y cadáveres

Se mantienen cuerpos de Zombie Remains y Corpse, objetos sueltos y materiales en doce bloques, bolsa persistente de nueve stacks, mejoras de equipo y experiencia de saqueo. Se tarda dos segundos por objeto de un cuerpo y uno por objeto suelto, con pose/movimiento de mano. Las tareas de combate interrumpen el saqueo y liberan la reserva.

Se desactiva la recogida automática antigua de estos NPCs para evitar que vuelva a equipar armadura vanilla o compita con las transferencias. El saqueo propio recoge y conserva esos objetos, pero equipa prendas de Self Expression/Fracture Point. Las armas y herramientas se siguen pudiendo mejorar.

Remains **1.6.1 npc-compat no cambia**: los cadáveres contienen equipo y bolsa reales, antes del botín generado. Se mantienen spawn natural, registro regional, reglas de supervivencia y huevo de camuflaje en creativo/búsqueda:

    /give @s tacznpcs:bandit_spawn_egg

## Fuentes y verificación

Java 21, Python 3, ECJ y ASM incluidos con checksums. Extraer las fuentes y ejecutar:

    python3 -B build.py /ruta/tacznpcs-2.1.0-1.21.1-tactical.1.jar /ruta/zombie-remains-loot-1.6.0-neoforge-1.21.1.jar
    python3 verify_integrations.py /ruta/selfexpression-2.22a-neoforge-1.21.1.jar

Las bases del repositorio son para reconstrucción, no para instalar junto a las actualizaciones. `src/` contiene la IA, vestuario y saqueo; `Patch.java` aplica transformaciones; `remains-src/` conserva la compatibilidad existente. Las herramientas verifican que solo cambien las entradas previstas. Los dobles y fuentes de referencia de tests no se incluyen en los mods.

Pasaron **1247 aserciones con dobles de API** y análisis ASM de **139 métodos**. Para los tests de combate se compilan fuentes de referencia de los goals vanilla (proveniencia indicada en cada archivo); el mundo, navegación, selector y API continúan siendo dobles. Se comprueban prioridades de flags, orientación, cambio de atacante, melee/swing, sprint, retirada limitada, integración de vestuario viejo, herramientas para cada uniforme, conservación de armaduras reemplazadas, filtro contra equipamiento vanilla, penalizaciones TACZ, recuperación en agua, saqueo, persistencia, cadáveres y huevo. Los helpers se ejecutan con `-Xverify:all`. También se reconstruyen las fuentes en un directorio limpio y se comparan todas las entradas del JAR.

**No se ejecutó Minecraft ni el modpack completo.** Los tests no validan locomoción física, render de prendas o animaciones en esa instalación. No se presentan las aserciones como evidencia de que el comportamiento ya quedó bien en juego. La descarga del runtime NeoForge/Minecraft para una prueba real no estuvo disponible en este entorno.

El log nuevo confirma tactical.4 y Self Expression 2.22; no muestra errores del controlador. Los errores de biomas de fabulous-furnished y demás errores ajenos que aparecen no se corrigen mediante este JAR. Este parche se aplica a `tacznpcs:npc`, no a las entidades de Faction Friction ni a los zombis de Hordes.
