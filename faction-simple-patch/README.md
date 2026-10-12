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
