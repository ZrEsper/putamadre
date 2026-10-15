# ZOMBIES POLDO — Cadáveres, dificultad y medicina

## Instalar

1. Cierra Minecraft y guarda una copia del mundo.

2. En `mods`, retira las versiones anteriores de **Zomboid Hordes, Zombie Remains Loot, FirstAid y SurvivorCreator**. Debe quedar una sola versión de cada mod.

3. Copia los cuatro JAR de este ZIP a `mods`. En multijugador, servidor y jugadores deben tener estos mismos archivos. Conserva los demás mods y tu TZP. Este ZIP es una actualización, no todo el modpack.

4. Entra y escribe `/zhorda panel`. Necesitas comandos activados en individual o permisos OP en servidor.

## Elegir la dificultad

El botón superior **Preparar: nombre del perfil** prepara el perfil mostrado. Cada pulsación cambia al siguiente: Desolado, Medio, Normal, Sobrado e Inicios del apocalipsis. Preparar rellena los controles, pero **no guarda todavía**. Puedes cambiar cualquier valor y después pulsar **Aplicar**.

Un perfil es un punto de partida: no bloquea opciones ni se vuelve a aplicar automáticamente. Preparar otro sustituye los valores del borrador. No ejecuta borrar zombis, borrar cadáveres, limpiar objetos o reiniciar cofres. La curación por nivel queda apagada; puedes activarla después.

| Perfil | Suministros nuevos | Población | Mutantes ZH | Experiencia |
|---|---|---|---|---|
| Desolado | 0,08x | 1,6x | 4% | Mucha escasez. Muchos cuerpos sin suministros salvo ropa. Zombis más atentos, rápidos y dañinos. |
| Medio | 0,45x | 1,25x | 2% | Escasez y presión moderadas. |
| Normal | 1x | 1x | 1% | Valores de referencia. |
| Sobrado | 2,5x | 0,75x | 0,5% | Más suministros y menos presión. |
| Inicios del apocalipsis | 5x | 0,4x | 0,1% | Abundancia y zombis más simples. El espacio de inventario sigue siendo limitado. |

La cantidad se aplica a loot nuevo de cofres/barriles, drops y cadáveres que pasan por las integraciones de ZH. No borra cosas guardadas ni la carga real de NPC. Otros generadores de loot conservan sus reglas. La ropa vestida se conserva aparte de los suministros, incluso en Desolado.

## Moverse por el panel

Hay 16 páginas, con cuatro pestañas visibles a la vez. **Página >** y **< Página** recorren las páginas y conservan el borrador válido. Pasa el ratón por una casilla para ver su explicación. En Mutantes y Profesiones, **Ver probabilidades** actualiza los porcentajes en la ayuda de cada casilla.

Siguen disponibles dificultad, loot y NPC, población, limpieza, cofres, drops, cadáveres, IA, velocidad/daño y cooldowns melee. Aplicar guarda en este mundo; otro mundo puede tener ajustes distintos.

## Mundo y medicina

| Opción | Qué hace |
|---|---|
| Cadáveres por profesión | Da suministros relacionados con el oficio. El oficio aparece al abrir el cuerpo. |
| Zombis con profesión (%) | Cuántos zombis nuevos sin oficio conocido reciben uno del pool. 50 es aproximadamente la mitad. El cadáver aparece cuando muere el zombi; esto no coloca nuevas estructuras por el mapa. |
| Reconocer equipo y nombres | Prioriza equipo real, nombres, oficio de aldeanos zombis y skins conocidas de TZP. Apágalo para usar solamente el pool. |
| Conservar ropa vestida | Copia prendas equipadas con sus datos. Para ropa pintada en una skin, usa la prenda aproximada del catálogo. Si falta ese ítem del mod, usa ropa de cuero básica. Desnudo no inventa ropa. |
| Curar al subir de nivel | Apagado de inicio. Al activarlo, cualquier subida real de habilidad en SurvivorCreator cura las ocho partes, heridas, balas alojadas, fracturas y sangrado; restaura salud y elimina todos los efectos, también beneficios e infección zombi. Limpia los estados internos de heridas, gripe y adicción. No resucita jugadores muertos. |
| Inyección en loot (%) | Posibilidad de encontrar la cura al generar loot de cofres/barriles o cadáveres. 0 la quita. La escasez reduce también esa posibilidad. |
| Loot de profesión (x) | Multiplicador de suministros del oficio, combinado con Cantidad de Cadáveres. 0 quita esos suministros; ropa conservada se trata aparte. |

Los cuerpos con inventario ya guardado no se rellenan al cambiar ajustes. Los NPC conservan arma, ropa y carga reales: no se sustituyen por un sorteo. Los cadáveres de jugadores mantienen su sistema habitual.

## Pool: peso y probabilidad

Un **peso** indica qué tipo es más común comparado con los demás. No tiene que sumar 100. Mecánico 10, Médico 30 y los demás 0 produce aproximadamente 25% mecánicos y 75% médicos entre los sorteados. **0 excluye del sorteo**. Todos en 0 desactiva ese pool.

La probabilidad de recibir oficio y el peso de cada oficio son controles distintos. Un oficio reconocido por apariencia tiene prioridad. Para excluirlo también en ese caso, apaga Reconocer equipo y nombres y pon su peso a 0.

## Todas las profesiones

Incluye las 35 profesiones de tu SurvivorCreator, más Militar y Desnudo. Un perfil indica qué puede llevar el cuerpo; no todos los oficios pueden distinguirse visualmente de forma fiable.

| Cadáver | Peso inicial | Probabilidad en el pool | Suministros posibles |
|---|---|---|---|
| Herrero | 3 | 2.31% | iron_ingot × 3; coal × 3; iron_pickaxe |
| Trabajador de hamburguesería | 2 | 1.54% | cooked_beef × 2; bread × 2; bowl |
| Ladrón | 2 | 1.54% | iron_nugget × 4; tripwire_hook; shears |
| Carnicero | 3 | 2.31% | cooked_porkchop × 2; iron_axe; bone × 2 |
| Carpintero | 4 | 3.08% | oak_planks × 8; stick × 4; iron_axe |
| Cocinero | 3 | 2.31% | baked_potato × 3; bread × 2; bowl |
| Constructor | 5 | 3.85% | cobblestone × 12; bricks × 6; iron_shovel |
| Repartidor | 3 | 2.31% | paper × 3; bread × 2; string × 2 |
| Manitas | 3 | 2.31% | iron_nugget × 5; redstone × 3; lever |
| Médico | 3 | 2.31% | firstaid:bandage × 2; firstaid:plaster × 2; firstaid:prophylactic_antibiotic |
| Electricista | 3 | 2.31% | redstone × 5; copper_ingot × 3; lever |
| Ingeniero | 3 | 2.31% | redstone × 4; iron_ingot × 2; piston |
| Agricultor | 5 | 3.85% | wheat_seeds × 6; carrot × 3; iron_hoe |
| Bombero | 2 | 1.54% | iron_axe; bucket; firstaid:bandage |
| Pescador | 4 | 3.08% | fishing_rod; cooked_cod × 3; string × 3 |
| Guía de pesca | 2 | 1.54% | fishing_rod; cooked_salmon × 3; compass |
| Entrenador físico | 2 | 1.54% | cooked_chicken × 2; water_bucket; leather |
| Cazador | 3 | 2.31% | bow; arrow × 8; cooked_rabbit × 2 |
| Leñador | 4 | 3.08% | iron_axe; oak_log × 6; stick × 3 |
| Personal de mantenimiento | 4 | 3.08% | iron_nugget × 5; iron_pickaxe; lever |
| Mecánico | 5 | 3.85% | iron_ingot × 3; copper_ingot × 3; iron_pickaxe |
| Músico | 2 | 1.54% | note_block; string × 4; paper × 2 |
| Enfermero | 3 | 2.31% | firstaid:plaster × 2; firstaid:bandage × 2; firstaid:painkillers |
| Paramédico | 2 | 1.54% | firstaid:bandage × 2; firstaid:adrenaline_injector; firstaid:prophylactic_antibiotic |
| Guardabosques | 3 | 2.31% | compass; iron_axe; bread × 3 |
| Policía | 3 | 2.31% | iron_sword; firstaid:bandage; paper × 2 |
| Ganadero | 3 | 2.31% | lead; cooked_beef × 3; wheat × 4 |
| Explorador | 3 | 2.31% | compass; torch × 6; bread × 2 |
| Chatarrero | 4 | 3.08% | iron_nugget × 8; copper_ingot × 3; flint × 2 |
| Guardia de seguridad | 3 | 2.31% | iron_sword; torch × 4; firstaid:plaster |
| Recolector de suministros | 4 | 3.08% | bread × 3; torch × 5; firstaid:plaster × 2 |
| Sastre | 3 | 2.31% | shears; string × 6; leather × 3 |
| Civil / desempleado | 20 | 15.38% | bread; paper × 2; stick × 2 |
| Veterano | 2 | 1.54% | iron_sword; firstaid:bandage; iron_ingot × 3 |
| Soldador | 3 | 2.31% | iron_ingot × 4; copper_ingot × 3; coal × 3 |
| Militar | 3 | 2.31% | firstaid:bandage × 2; iron_sword; iron_ingot × 4 |
| Desnudo | 1 | 0.77% | rotten_flesh |

No recibes toda la lista: se hacen selecciones según cantidad y espacio. Los objetos de mods ausentes se omiten. `iron_ingot` es hierro; `copper_ingot`, cobre; `fishing_rod`, caña; `oak_planks`, tablas; `bricks`, ladrillos; `bandage`, venda; `plaster`, apósito. Militares, veteranos, policías, guardias y cazadores también pueden seleccionar un arma TaCZ válida si está disponible, conservando sus datos y ajustes de accesorios/munición.

## Reconocer skins de TZP

Se analizó el paquete adjunto y se incluyeron **55 coincidencias iniciales** de uniformes blancos, chalecos de construcción y conjuntos compatibles con militar. Es una heurística editable de ropa, no identificación perfecta de todas las profesiones. Las apariencias ambiguas quedan sin clasificar y usan el pool. Una camiseta gris no basta para distinguir mecánico y civil.

ETF, ya instalado según tu log, permite consultar la textura visible del zombi. El cliente informa textura y hash; el servidor exige una coincidencia de su catálogo, proximidad y visibilidad. No carga chunks ni permite cambiar dificultad con esos informes. Equipo y oficio reales tienen prioridad. En multijugador usa el mismo TZP en todos los clientes. Sin ETF se conservan equipo, nombres y pool.

La información visual procede del cliente. En servidores con clientes no confiables puedes apagar Reconocer equipo y nombres, que también desactiva estos informes. El servidor no puede verificar por sí solo lo que renderiza un cliente.

## Cambiar objetos y reglas de un oficio

Cuando se necesita, aparece `serverconfig/zomboid-apocalypse-corpse-catalog.properties` dentro de la carpeta del mundo. Puedes editarlo con Bloc de notas. Se relee al cambiar; si un valor está mal, conserva el último catálogo válido y lo indica en el log.

| Ejemplo | Significado |
|---|---|
| mechanic.loot=iron_ingot@3,copper_ingot@3,iron_pickaxe | Objetos posibles; @3 significa tres unidades en esa selección. |
| doctor.chance=50 | 50% de médicos pueden generar suministros de oficio. Ropa se conserva aparte. |
| fisherman.amount=2 | Duplica selecciones de oficio, combinado con los multiplicadores del panel. |
| doctor.clothes=selfexpression:doctor_helmet | Prenda aproximada para una skin sin equipo real. Puedes poner varias separadas por comas. |
| nude.clothes= | Sin ropa generada. |
| mechanic.names=mechanic,mecanico,mecánico | Palabras que permiten reconocer oficio por nombre/equipo. |

`.chance` permite 0–100 y `.amount`, 0–10. El catálogo incluye esas claves para todos los oficios. Los IDs son los de SurvivorCreator; Militar usa `military` y Desnudo, `nude`.

Para corregir una skin añade una propiedad con clave `texture.` seguida del identificador de textura y valor `profesion` + separador vertical + SHA256. Ejemplo de clave: `texture.minecraft\:optifine/random/entity/zombie/zombie1034.png`. La asignación inicial con hashes está dentro del JAR en `apocalypse31/skin-profiles.properties`; el proyecto incluye `skin-analysis.json` con los archivos analizados. Usa el hash del archivo correcto. Las correcciones afectan nuevos informes y cuerpos nuevos, no rellenan inventarios antiguos.

## Cada mutante

| Tipo | Peso inicial |
|---|---|
| Rastrero / crawler | 18 |
| Ampollado / blister_zombie | 25 |
| Cabeza partida / split_head_zombie | 23 |
| Mutante podrido / rotten_mutant | 18 |
| Escupidor / spitter | 10 |
| Zombi bruto / zombie_brute | 5 |
| Mutante bruto / mutant_brute | 1 |

Mutantes (%) decide con qué frecuencia ZH intenta seleccionar un mutante; los siete pesos deciden cuál. Máx. mutantes mantiene el límite local. Si solamente Escupidor tiene peso, los candidatos aceptados serán escupidores. Las especies que faltan se excluyen antes del sorteo. Todos los pesos en 0 desactiva la selección adicional.

No elimina mutantes existentes. Estos controles pertenecen al selector de Zomboid Hordes; no cambian huevos, `/summon` o generadores independientes de otros mods. Las características propias de cada especie siguen siendo las de MutantsZombies.

## La nueva cura

**Inyección contra la infección zombi**: mantén clic derecho 1,6 segundos. Se consume una unidad en supervivencia. Elimina la infección de The Hordes y sincroniza con jugador y observadores. No cura otras heridas ni da inmunidad permanente: una mordedura posterior puede infectarte otra vez.

El antibiótico anterior trata infección de heridas; es distinto. Puedes obtener la nueva cura en la pestaña creativa FirstAid o probar `/give @s firstaid:zombie_cure_injector`. Su aparición en loot se controla en Mundo y medicina.

## Qué significa cada categoría de drops

| Categoría | Explicación |
|---|---|
| Armas | Objetos utilizables como armas, según las tablas del pack. |
| Medicinas | Vendas, apósitos, analgésicos, inyectores y tratamientos registrados. Antibiótico y cura zombi tienen funciones distintas. |
| Comida | Alimentos que recuperan hambre. |
| Herramientas | Objetos para trabajar, como picos, hachas y palas. |
| Ropa y armaduras | Prendas y protección. Lo realmente equipado de un cadáver se conserva aparte. |
| Munición | Suministros de munición. Tener arma no garantiza tener balas. |
| Accesorios | Mejoras de armas disponibles en el pack. |
| Materiales | Recursos de fabricación y construcción: lingotes, componentes y otros materiales. |

Cantidad cambia cuántas selecciones se hacen. Los pesos cambian qué categoría puede salir y se normalizan entre sí. Un cuerpo profesional utiliza su catálogo de oficio; las categorías generales de Cadáveres se mantienen para cuerpos sin un oficio conocido.

## Si algo falla

Comprueba que no haya dos versiones del mismo mod y que servidor/jugadores coincidan. Guarda `logs/latest.log`. Busca `Apocalypse31`, `loot profesional`, `skin report`, `Catalogo de cadaveres` o `Curacion por nivel`. Un objeto ausente se omite; el inventario real de NPC no se reemplaza.

El log enviado también contiene errores anteriores de libros de Mosslorn con color `reset` y un aviso de compatibilidad de mochilas Sophisticated. Esta actualización no cambia esos mods.

**Verificación:** compilación Java 21, puntos de bytecode y comprobaciones con dobles de la API. Todavía falta probar dentro de Minecraft con tu modpack, especialmente ETF, inventarios, inyección y sincronización multijugador.
