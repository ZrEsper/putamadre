# Hordes 1.8.2 y melee 2.3.1

## Instalar

Reemplaza los dos JAR anteriores por los nuevos en cliente y servidor. No cargues dos versiones del mismo mod. Conserva las dependencias del modpack; esta actualización no cambia sus requisitos.

## Hordes: población por chances

Cada par fijo de chunks vecinos comparte un objetivo aleatorio de 0 a 5 zombis vivos:

| Objetivo | Probabilidad |
| --- | --- |
| 0 | 40 % |
| 1 | 30 % |
| 2 | 18 % |
| 3 | 8 % |
| 4 | 3 % |
| 5 | 1 % |

Se resta la población viva existente al objetivo; no se añaden cinco zombis nuevos sobre los que ya haya. El límite controla lo que añade este sistema: no elimina zombis anteriores ni los creados por comandos, otros mods o los que llegan caminando. Un objetivo de cero no borra los existentes.

El resultado se sortea cada 5–10 minutos de tiempo del juego y se guarda con el mundo. Un terreno bloqueado se vuelve a comprobar tras diez segundos sin volver a sortear; así, los reintentos no aumentan artificialmente la probabilidad de cinco. Se mantienen el máximo de 16 entidades y 256 intentos de posición por revisión.

Basta con que esté cargado uno de los chunks del par; solo se intenta aparecer en los chunks disponibles. Las búsquedas se distribuyen en un orden aleatorio para que un terreno bloqueado no acapare siempre el presupuesto.

Se mantienen supervivencia, Overworld, dificultad distinta de Pacífico, doMobSpawning=true, suelo apto, espacio sin colisiones, ausencia de agua, límite del mundo y distancia mínima horizontal de 24 bloques a los jugadores. No se fuerzan chunks nuevos. Se conservan ruido, IA y helicóptero de la base.

/zhorda estado muestra supervivencia, spawns habilitados, pausa restante, pares activos, población viva, objetivo sorteado y próxima revisión.

El latest.log aportado muestra una prueba rechazada en creativo y otra en supervivencia que añadió seis zombis. Después se volvió a creativo. Ese tramo no muestra un fallo de ejecución de Hordes.

## Melee: alcance +1

- V: búsqueda y límite de alcance de 1,5 a 2,5 bloques.
- Melee de LesRaisins: +1 bloque tanto al buscar objetivos como al validar el golpe en el servidor. Incluye las espadas y sartén que este addon adapta a LesRaisins.
- Se conservan daño, stamina, cono de V, colisiones de visión, cooldowns, animaciones y límite de objetivos de V.
- No se modifican los datos compartidos de las armas: los aumentos no se acumulan entre ataques.

Los nuevos mixins de alcance cubren ConeFilter, RayFilter y OBBFilter. Los nombres, campos y métodos se contrastaron con el código oficial LesRaisins 0.4.3, commit de97defc2d76c43d9724a458701a44e9ade680d2. También se contrastaron con la fuente pública de su port 1.21.1. Las anotaciones usan retención CLASS para Mixin, RUNTIME para las inyecciones y remap=false para las API externas.

## Reproducir

Python 3 y Java 21. ECJ, ASM y Gson están incluidos en hordes-patch/tools y se verifican con SHA-256 antes de usarlos.

Desde el directorio que contiene build_updates.py:

    python3 -B build_updates.py /ruta/zomboid-hordes-1.8.1-neoforge-1.21.1-chunk-population.jar /ruta/zomboid-global-melee-animations-2.3.0-elbow-range.jar

Se verifican los hashes de ambas bases. Los resultados se escriben en output/. La base de Hordes debe tener SHA-256 2d7e1c663bc09793c6b9fdc597d3294f4130bec5fb7ea90cd45999e8bfca1e3e; melee, bbd28af59fa53cd178ed27ecd4c71f102a283847c1a9ccc51d2d8c70c0934ac6.

hordes-patch/src contiene las clases de población actualizadas y melee-patch/src contiene los nuevos hooks. PatchMelee.java modifica exclusivamente las constantes de alcance de V. build_updates.py preserva el resto de las entradas originales salvo metadatos y registro de los nuevos mixins.

Las pruebas ejecutan las clases reales parcheadas de población, HordeData y HordeConfig con dobles de Minecraft/NeoForge; verifican distribución exacta, límites, guardado, reintentos y condiciones de spawn. Las pruebas de melee ejecutan las reglas reales de V y los hooks de alcance compilados con dobles de filtros y callbacks. También se verifican el bytecode y las anotaciones. Los dobles no se incluyen en los JAR finales.

**No se ha ejecutado el modpack ni aplicado los nuevos mixins dentro de Minecraft.** Validación final: probar en supervivencia, consultar /zhorda estado, esperar los cooldowns, guardar/reabrir y comprobar los golpes con V y melee antes y después de sus límites de alcance.

Los cambios de Hordes conservan GPL-3.0-only de la base. Las herramientas incluidas conservan las licencias contenidas en sus JAR.
