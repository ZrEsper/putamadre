## Actualización .10: loot de oleada solo por baja de jugador

[Descargar .10](../mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.10-player-kill-corpse-loot.jar). Sustituye .9 en cliente y servidor. Conserva dificultades elegibles y ausencia de cooldown.

La regla se aplica exclusivamente a entidades marcadas `FactionPveWave`, tanto zombis normales como mutantes de defensa. El daño final debe atribuirse a un ServerPlayer real para conservar cadáver looteable, drops y XP. Los proyectiles con dueño jugador califican; NPC, entorno y FakePlayer de automatización no. Haber golpeado antes al zombi no habilita loot si el daño final fue ambiental.

Se registra la atribución al morir y se conserva para drops diferidos, de modo que la atribución antigua de combate no convierta una muerte natural en una baja looteable. La limpieza automática marca a cada enemigo antes de infligir daño letal y bloquea su loot incluso si otro mod reescribe la fuente de ese daño. Un enemigo que el jugador mate antes de recibir el daño automático de limpieza sigue las reglas de baja de jugador.

En muertes sin jugador se cancela LivingDropsEvent con prioridad HIGHEST y se vacía la lista de drops. Se inspeccionaron Zombie Remains Loot 1.6.1 npc-compat y 1.6.2 death-compat.4-mutant-loot.1: sus creadores de cadáveres escuchan LivingDropsEvent en LOWEST, sin recibir eventos cancelados. Su secuencia visual de muerte puede seguir funcionando, pero no recibe el evento que genera el inventario/cadáver pendiente. No se cancela LivingDeathEvent ni se impide el conteo normal de la defensa. La recompensa de victoria del clan se conserva; no se modifica el loot de zombis fuera de oleadas.

También se pone a cero LivingExperienceDropEvent para esas muertes, evitando XP por daño reciente previo. Otros mods que creen cadáveres por mecanismos independientes de LivingDropsEvent podrían requerir un adaptador adicional.

183 comprobaciones simuladas (68 + 71 + 33 + 11): muertes ambientales, baja real, atribución del dueño, NPC, limpieza, baja previa seguida de muerte natural, drops diferidos, XP, FakePlayer y conservación de mobs ajenos a la oleada. Se verifica el bytecode y el JAR. No se ha ejecutado Minecraft: comprobar una baja natural y otra por jugador con tu mod de cadáveres sigue pendiente.

## Actualización .9: dificultad elegible, más mutantes y sin cooldown

[Descargar .9](../mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.9-selectable-difficulty-no-cooldown.jar). Reemplaza .8 en cliente y servidor; el protocolo cambia a .9.

En K → Oleadas PvE, el botón «Dificultad» recorre cinco opciones y «Iniciar defensa» comienza directamente con la seleccionada. No exige solicitar una oferta aleatoria. No existe cooldown PvE de inicio ni al terminar, y los tiempos guardados por versiones anteriores se ignoran/restablecen a cero. Se conserva una defensa activa por clan y un máximo global de tres. No cambia la preparación ni las treguas de guerras PvP.

| Dificultad | Enemigos por fase, incluyendo mutantes | Mutantes por fase | XP al ganar las tres fases |
|---|---:|---:|---:|
| Fácil | 12 | 0 | 20 |
| Normal | 20 | 3 | 35 |
| Difícil | 28 | 8 | 60 |
| Extrema | 36 | 14 | 100 |
| Pesadilla | 48 | 24 | 160 |

Hay tres fases. La vida del nexo se conserva entre ellas. Los niveles altos mantienen la preferencia por especiales más fuertes y mejores cantidades/calidad de recompensas. Para Normal o superior se requiere Mutants Zombies. Los mutantes sustituyen parte de los enemigos del total de la tabla; no son un número añadido encima del total.

El selector puede cambiarse antes de iniciar; durante una defensa no se modifica su dificultad. El servidor valida el rango 0–4 y la autoridad del clan. `/clanpvetest` conserva la prueba de dificultad Fácil para operadores. Las condiciones de nexo/territorio, spawns exteriores de .8, HUD, sonidos, depósito y limpieza gradual se mantienen.

172 comprobaciones simuladas (68 + 71 + 33), incluyendo selección explícita, rechazo de dificultad inválida, aumento de mutantes, inicio de Pesadilla sin solicitar oferta pese a un cooldown antiguo y final sin cooldown. No se ha ejecutado Minecraft: probar el selector y el balance real pendiente.

Las reglas de ofertas aleatorias y cooldown de las secciones anteriores son históricas y quedan reemplazadas por .9.

## Actualización .8: inicio con nexo interior y suelos de losas

[Descargar .8](../mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.8-indoor-nexus-slab-spawns.jar). Reemplaza .7 en cliente y servidor. Se comprobó que el JAR .7 adjunto coincide con el publicado, SHA-256 `8493af27e42b54226b1e93d0093838fb09ec1591e546634d7f2a503300262cde`.

La captura muestra el rechazo del primer spawn. La .7 exigía una ruta inicial al bloque del nexo y aceptaba como piso solo bloques de render sólido. Ambos criterios podían descartar calles válidas con losas y nexos resguardados en habitaciones/pisos superiores. La .8 no exige esa ruta para generar: mantiene la comprobación de espacio del cuerpo, el exterior y la ausencia de agua. Si existe una ruta directa/parcial útil, la usa; si no, intenta acercarse a nivel del suelo hacia la fachada, buscar entradas y aplicar el asedio local de .6.

El apoyo del zombi se calcula a partir de las cajas de colisión del piso, incluyendo losas y formas no completas. La posición vertical usa la parte superior real: una losa inferior en Y=64 produce apoyo en Y=64.5, sin generar flotando a Y=65.

Se muestrean hasta 192 columnas exteriores, primero dentro de 32 bloques y después hasta 48 por eje, consultando solamente chunks ya cargados para la ampliación. Se conservan las superficies más bajas (+2 de margen), los grupos y la separación. Los chunks adicionales elegidos reciben tickets temporales propios de la defensa, liberados al terminar. No se busca dentro de sótanos/habitaciones ni se cambian la vida o recompensas del nexo.

Si todavía no se puede generar, el mensaje informa de puntos exteriores, intentos bloqueados por colisión y spawns cancelados por eventos de otros mods. Conserva la oferta y retira los tickets al rechazar el inicio. La captura por sí sola no identifica cuál de esos factores ocurrió en tu mundo; el nuevo mensaje permite distinguirlos.

166 comprobaciones simuladas (68 + 67 + 31), incluyendo nexo resguardado en un piso superior con ruta nula, apoyo de losas, acercamiento por suelo y cancelación externa sin enemigos contados. No se ha ejecutado Minecraft: navegación en el edificio concreto de Mosslorn pendiente.

Prueba con `/clanpvetest` (operador y nexo colocado). Mantiene partículas, sonidos, HUD, depósito y recompensas de .7.

## Actualización .7: generador, alertas, recompensas e IA de superficie

[Descargar .7](../mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.7-nexus-alerts-surface-rewards.jar). Sustituye .6 en cliente y servidor. Cambia el protocolo a .7: ambos lados deben actualizarse.

Durante la defensa el nexo emite humo y chispas, con un motor procedural de tres segundos repetido mientras esté activo. Su daño se agrupa por segundo: alerta sonora personal a los participantes, barra grande arriba de la pantalla con PV actuales/máximos, fase y pérdida reciente en rojo durante 2,5 segundos. No necesitas estar cerca del nexo para recibir la alerta de daño. El motor es un sonido espacial del bloque. Añade ocho sonidos originales: motor, golpe al nexo, inicio, nueva fase, victoria, derrota, depósito de recompensas y recogida. Al terminar deja de emitir partículas y de repetir el motor; la última muestra de tres segundos puede acabar de sonar.

Spawns: se muestrean 96 superficies exteriores en un anillo de 8–32 bloques por eje. Se usa la altura de superficie, no alturas interiores aleatorias, y se conservan las superficies más bajas del área (+2 bloques de margen), descartando azoteas altas cuando hay calles más bajas. Esto evita sótanos y habitaciones interiores. No identifica semánticamente todos los edificios: si todo el entorno disponible está sobre una misma plataforma/azotea, esta puede contar como superficie exterior.

Los grupos salen cerca de un punto común, con separación mínima de dos bloques entre sus miembros, cuerpo sin colisión, piso sólido y una ruta inicial de más de un nodo hacia el nexo. Se admite una ruta parcial hasta el obstáculo para conservar el asedio por puertas, ventanas, rotura y peldaños de .6. La IA se activa explícitamente, comienza a navegar al generarse y recibe una comprobación del servidor cada segundo. No se teletransportan zombis para resolver obstáculos. Si desaparecen por otro mod, se dejan de contar tras diez comprobaciones para evitar una fase bloqueada. Si no hay punto válido al iniciar, se conserva la oferta.

Las tres fases, defensa al salir del territorio, limpieza gradual al perder y `/clanpvetest` se mantienen.

Recompensas: más suministros, experiencia embotellada, equipo y placas de FracturePoint si están registrados. Se incluyen cascos; desde poco común, chalecos; desde rara, piezas de conjunto y placas cerámicas. Las categorías altas entregan más equipo. Armas: se leen los índices reales de TACZ instalados y se priorizan namespaces que contienen `fracture` o `warborn`; si no existe ese gunpack, se usan las armas TACZ disponibles. El JAR FracturePoint 3.0.0 PT2 inspeccionado contiene armaduras/equipo, no un gunpack TACZ de armas incluido. No se inventan IDs de armas.

Se entrega una arma por defensa común/poco común/rara, dos por épica y tres por legendaria, cuando TACZ tiene armas disponibles. Cada una deposita 32 × (rareza + 1) cartuchos de su calibre. Los stacks de armas se crean con ID de arma, modo de disparo válido y descargados; la munición lleva su ID de calibre. Los ítems de un mod/pack ausente permanecen pendientes, sin consumir su recompensa. Los nombres internos `gun@namespace:id` y `ammo@namespace:id` se guardan en el mismo depósito del clan y se reconstruyen al recogerlos. El menú dibuja los iconos reales y sus nombres de objeto.

160 comprobaciones de API simuladas (68 + 67 + 25), incluyendo un spawn real dentro del simulador, rechazo de alturas de azotea frente a calle, navegación iniciada, heartbeat de IA, armas con componentes, munición compatible, armaduras registradas, iconos, registro de sonidos, motor sin spam, partículas, daño/HUD y alertas por fase/derrota. Se verifica que los sonidos originales del mod se conservan y que los ocho nuevos OGG son válidos. No se ha ejecutado Minecraft: falta comprobar audio, renderizado y navegación en Mosslorn con todos tus mods.

Sonidos reproducibles con `python faction-simple-patch/generate_sounds.py` (NumPy y ffmpeg). No se usaron muestras de otros juegos.

## Actualización .6: suelo, entradas, asedio y comando de prueba

[Descargar .6](../mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.6-ground-entry-debug.jar). Reemplaza versiones anteriores en cliente y servidor. El protocolo cambia: ambos deben usar .6.

Los enemigos buscan pisos sólidos con espacio y sin agua. Priorizan alturas cercanas al nivel del mar + 1 (aproximadamente Y=64 en Mosslorn), como referencia para suelo/primer piso, y recorren después el resto de la altura legal del mundo. Esto no identifica semánticamente el primer piso de cada edificio. Ya no se exige que exista un camino completo antes de generar: la IA puede abrirse paso. Se mantiene el anillo de 8–24 bloques por eje alrededor del nexo.

Si hay una ruta directa, la usan. Si no, buscan entradas alcanzables hasta seis bloques alrededor: puertas y trampillas antes que cristal/ventanas. Los enemigos atascados abren puertas/trampillas próximas, rompen ventanas después y finalmente obstáculos sólidos. No rompen el nexo por esta vía, bloques irrompibles, cofres, barriles, shulkers ni bloques con block entity. Un jugador detrás de una pared no interrumpe la IA de asedio sin línea de visión.

Como último recurso, después de atascarse, pueden colocar peldaños de cobblestone para subir hacia un objetivo más alto, hasta 16 bloques por infectado. Los bloques colocados y rotos modifican el mundo de forma permanente. Esta es una solución local de obstáculos, no un planificador que garantiza superar cualquier edificio.

Prueba: `/clanpvetest`. Requiere operador nivel 2, ser jefe/comandante, tener al menos un chunk y un nexo colocado. Inicia una defensa común saltándose el cooldown y la solicitud de oferta; conserva los requisitos de espacio. No inicia otra si hay una activa. Es una defensa real: puede romper el nexo y entrega sus recompensas normales si ganas. No se añade tecla.

135 comprobaciones simuladas, incluyendo alturas completas, preferencia por puertas sobre ventanas, apertura, rotura de ventanas y paredes, protección de almacenamientos, construcción limitada y registro/permisos del comando. Falta probar la navegación y el rendimiento en Minecraft con tus edificios.

## Actualización .5: puedes salir y final gradual

[Descargar .5](../mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.5-pve-roaming-gradual-end.jar). Reemplaza versiones anteriores en cliente y servidor.

Salir de cualquier chunk o alejarse de la base no termina la defensa. Los zombis siguen buscando destruir el nexo y puedes regresar a defenderlo. Se mantienen las tres fases y la victoria al eliminarlas. Se pierde si cae el nexo o mueren todos los participantes; quienes ya murieron no se reincorporan al reaparecer.

Al perder se detiene la generación y los enemigos restantes mueren en grupos pequeños, aproximadamente durante 12 segundos como máximo, con un segundo adicional para su animación de muerte. La limpieza no da XP ni premios de victoria. Otros mods pueden modificar las animaciones o los drops normales de muerte. El cierre del servidor realiza una limpieza inmediata.

Una zona de 5 × 5 chunks alrededor del nexo permanece en simulación durante la defensa y la limpieza mediante tickets temporales, independientes por partida y liberados al terminar. No se cancelan las defensas por salir, desconectarse sin morir, quedarse temporalmente sin lugar de generación, ni por el antiguo límite de 15 minutos. Borrar el clan, retirar el nexo por comandos o cerrar el servidor sigue interrumpiéndolas.

120 comprobaciones con API simuladas: continuidad fuera del territorio, ausencia del límite anterior, carga y liberación de tickets superpuestos, muerte gradual, animación antes de retirada, limpieza de contornos y ausencia de premios al perder. Falta prueba real de Minecraft.

Las secciones .4 y anteriores de abajo documentan versiones antiguas; .5 reemplaza su regla de abandono.

## Actualización .4: aparición accesible y abandono de territorio

[Descargar .4](../mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.4-pve-spawn-territory-fix.jar). Reemplaza versiones anteriores en cliente y servidor.

La búsqueda anterior se limitaba al chunk del nexo y a una altura aleatoria. Ahora prueba suelo sólido en un anillo de 8–24 bloques por eje, a la altura del nexo y hasta cuatro bloques arriba/abajo, incluyendo chunks vecinos ya cargados. Exige espacio para el cuerpo y una ruta de navegación hasta el nexo. No genera a menos de seis bloques del defensor seleccionado.

Primero intenta generar un enemigo antes de consumir la oferta; si no puede, rechaza el inicio y conserva la oferta, con un mensaje para mover el nexo cerca de un patio o entrada abierta. Durante la partida incorpora hasta tres enemigos por segundo. Conserva contornos rojos y especiales.

Debe permanecer al menos un participante vivo y conectado dentro de cualquier chunk reclamado por su clan, en la misma dimensión. Cuando no queda ninguno, la defensa falla en la siguiente comprobación (aproximadamente un segundo), limpia los enemigos y no entrega recompensas. Cambiar a otro chunk propio no cuenta como abandonar.

110 comprobaciones con API simuladas. Prueba real de navegación, spawns y renderizado en Minecraft pendiente.

## Actualización .3: descripción, contorno rojo y corrección del crash

[Descargar .3](../mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.3-nexus-tooltip-fix.jar). Reemplaza .1 o .2 en cliente y servidor; conserva una sola versión.

El nexo muestra ocho líneas de explicación al pasar el ratón por su objeto en el inventario. Los enemigos de defensa tienen brillo rojo vanilla (puede verse a través de paredes); los zombis normales mantienen su aspecto. Las entradas de su equipo temporal se retiran al morir o terminar la defensa.

Corrige el crash adjunto `ClassNotFoundException: dev.zomboid/clans/NexusGoal`: la carga reflectiva ahora usa el nombre de clase con puntos. Incluye una comprobación de creación real de la IA sobre las API simuladas y del tooltip y color. Total: 106 comprobaciones simuladas; sin ejecución de Minecraft ni verificación visual.

# Zomboid Clans .2: menú sencillo y defensa del nexo

[Descargar JAR](../mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.2-nexus-pve.jar).

Reemplaza Zomboid Clans .1 en cliente y servidor, dejando una sola versión. Mantiene identidades de clan, guerras, aliados, comercio, permisos, posiciones y depósitos de .1. Los canales requieren .2 en ambos lados. No reemplaza Mutants Zombies ni su addon L4D2: conserva sus JAR y dependencias instalados para los especiales. El bloque nuevo se registra como `factionfriction:tribal_nexus` y todos los clientes deben tenerlo.

## Menú y territorio

K abre cinco páginas. Clan muestra nivel y territorio y permite reclamar/liberar el chunk donde estás usando los paquetes y permisos originales. Personalizar abre los controles de lema y emblema solo cuando los necesitas. La administración avanzada queda dentro de Clan. Conflicto presenta declaración cuando no hay guerra, preparación/estado cuando existe, y selección de captura cuando se puede atacar. El mapa conserva la selección nativa, pero usa asignaciones automáticas: dos botones, Capturar selección y Volver. Muestra los requisitos reales de bloques y tiempo configurados por el servidor.

| Nivel nativo | Límite total de chunks | XP PvE para alcanzarlo |
|---|---:|---:|
| 1: Banda / tribu | 4 | 0 |
| 2: Refugio | 6 | 100 |
| 3: Coalición | 8 | 300 |

Límite de nuevas reclamaciones y capturas; incluye capitales. Una anexión de capital no puede superar el espacio disponible. Claims anteriores que excedan el límite se conservan: libera territorio para poder reclamar más. Las restauraciones de territorios previos al resolver operaciones conservan las reglas originales. No elimina bases existentes. Los tres niveles nativos y sus otras reglas se mantienen; subir por PvE nunca rebaja un clan que ya tenía un nivel superior.

## Nexo tribal

1. Reclama al menos un chunk.
2. El jefe (CREATOR o LEADER) pulsa Oleadas PvE → Recibir nexo.
3. Coloca el bloque dentro de territorio propio en el Overworld. Hay un nexo colocado por clan. Fuera de defensas, el jefe puede romperlo, recogerlo y volver a colocarlo; administradores pueden recuperar nexos abandonados.
4. Solicita una oferta y acepta la defensa dentro de la base. Debe haber participantes vivos conectados dentro del territorio. El menú oculta las acciones que aún no se pueden realizar.

El nexo tiene 100 PV iniciales y diez mejoras opcionales de +25 PV: máximo 350. Cada mejora cuesta `8 × número de mejora` lingotes de hierro del inventario del jefe. Sus mejoras permanecen guardadas aunque se recoja y recoloque el bloque. Si se destruye en una defensa, el jefe puede recibir un reemplazo.

Durante la partida los jugadores no pueden romper ni retirar el nexo. Resiste explosiones y pistones. Los zombis de la defensa buscan el nexo y atacan a supervivientes próximos. Un golpe al nexo quita 5 PV para zombis normales y 10 para especiales/mutantes, con un intervalo individual de un segundo. Solo dañan el bloque con una línea despejada: no golpean a través de paredes. Los especiales conservan sus modelos, atributos y habilidades del mod instalado; se priorizan supervivientes a mayor distancia que los zombis básicos. First Aid derribado no se considera un objetivo que pueda combatir.

Cada defensa tiene **tres fases**. La vida del nexo se conserva entre fases; vuelve a su máximo al iniciar otra partida. Se pierde al destruirse el nexo o morir todos los participantes conectados. Los muertos no vuelven al grupo mediante respawn durante esa misma partida. Salir del territorio o alejarse no cancela la defensa. No existe límite de 15 minutos. La zona del nexo permanece cargada mediante tickets transitorios hasta terminar y limpiar enemigos. Hay **cinco minutos de enfriamiento después de terminar**, contando ticks del mundo.

## Ofertas y recompensas

| Rareza | Probabilidad | Enemigos por fase | Especiales por fase | XP por victoria |
|---|---:|---:|---:|---:|
| Común | 60% | 12 | 0 | 20 |
| Inusual | 25% | 20 | 1 | 35 |
| Rara | 10% | 28 | 2 | 60 |
| Épica | 4% | 36 | 4 | 100 |
| Legendaria | 1% | 48 | 6 | 160 |

Una oferta dura cinco minutos y no se vuelve a sortear mientras esté vigente. Los especiales se eligen entre crawler, blister_zombie, spitter, zombie_brute, split_head_zombie, rotten_mutant y mutant_brute del namespace `mutantszombies`. Los más fuertes predominan en ofertas altas. El addon L4D2 modifica esos infectados cuando está instalado; no añade otros tipos de entidad al generador. Si falta Mutants Zombies, las ofertas con especiales se bloquean, sin consumirlas como si fueran partidas completas.

Una victoria añade XP, una victoria al marcador y `1 + rareza` lotes de suministros/equipo del depósito original. También entrega hierro adicional; niveles de rareza altos añaden pechera de hierro, espada y pechera de diamante, diamantes, manzanas doradas y, en legendaria, dos fragmentos de netherita. Los materiales y objetos normales permiten usar el depósito existente. Trampas y aliados pueden ayudar a eliminar la oleada; la recompensa exige completar las tres fases con el nexo vivo. La finalización no puede pagar dos veces.

Oleadas PvE muestra el top diez por XP; desempata por victorias y nombre. XP, victorias, recompensas, mejoras, posición del nexo, ofertas y enfriamiento se guardan en los datos originales del mundo. Las partidas activas se cancelan al cerrar el servidor: no se conceden premios por reiniciar. Sus enemigos transitorios se eliminan; si quedan guardados por un cierre inesperado, se descartan al cargar después.

## Spawns y límites de rendimiento

Se bloquean nuevas apariciones de zombis vanilla y mutantes en chunks reclamados. Los enemigos etiquetados de una defensa voluntaria están permitidos. Los zombis ya guardados no se borran; pueden entrar caminando desde fuera. No bloquea cadáveres, proyectiles ni NPC de otros namespaces.

Máximo de tres defensas simultáneas, una por clan. Los enemigos aparecen gradualmente en el chunk del nexo, sobre un suelo sólido y con espacio para su tamaño. No se fuerzan chunks a cargar. Si no se encuentran posiciones válidas durante un minuto, falla la partida sin recompensas. La navegación se actualiza como máximo cada medio segundo y el HUD de progreso una vez por segundo.

## Validación

100 comprobaciones con APIs simuladas: 68 anteriores de guerras/permisos y 32 de registro del bloque/ítem, límites y capitales, probabilidades, XP, colocación única, bloqueo de rotura, spawns protegidos y excepciones, daño 5/10 y su intervalo, fases sin reinicio de vida, derrota por muertes/destrucción, recompensas sin duplicación, enfriamiento y guardado. Se verifica el bytecode y la integridad ZIP; no se incluyen clases de pruebas ni dobles de Minecraft.

**No se ejecutó Minecraft.** Deben probarse la pantalla a distintas escalas, el registro real, colocación/recogida, navegación de cada especial y compatibilidad con los filtros de spawn y cámara del modpack. Los límites y permisos originales siguen vigentes; otros mods pueden cancelar un spawn o alterar la navegación.

Para reconstruir, Java 21 y Python 3. El JAR base y las herramientas se verifican por SHA-256. Extrae las herramientas del ZIP de fuentes de .1 y usa:

```sh
python -B faction-simple-patch/build.py mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.1.jar /ruta/a/tools
```

El script extrae automáticamente los stubs del archivo de fuentes .1 conservado en este repositorio.
