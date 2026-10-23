# tactical.58 — cultivos y trabajos por necesidad

Sustituye únicamente el JAR de tacznpcs anterior por `tacznpcs-2.1.0-1.21.1-tactical.58-work-sites.jar`. No dejes dos versiones. Guarda copia del mundo para la prueba. No cambia los otros mods.

## Qué fallaba y qué cambia

- El oficio se escogía por historia o UUID. Ahora el trabajo comunitario elige por déficit: un trabajador asegura el bancal y la reforestación; los demás priorizan cerrar el perímetro. Después se asigna obtención de madera o piedra según existencias. La asignación de comida se reserva 200 ticks, se renueva mientras trabaja y puede pasar a otro NPC si deja de atenderla. La profesión de su historia permanece intacta.
- El agricultor podía navegar al lugar del propio bancal, bloquear su colocación con su cuerpo y continuar una ruta vieja durante el trabajo. Trabaja desde puestos exteriores y se detiene antes de modificar un bloque.
- Un campo ya sembrado provocaba recorridos de inspección constantes. Se comprueba si falta agua, un cultivo o hay trigo maduro antes de activar el trabajo agrícola.
- El kit anterior podía darse por entregado aunque faltaran partes. Se completa una única vez por NPC: hasta ocho semillas, doce tierras, una cubeta de agua y una azada de piedra si no tiene azada. Recupera primero lo accesible del almacén; solo entrega lo faltante. También repara NPCs antiguos con el marcador anterior. No repone infinitamente artículos consumidos o robados.
- Se mantienen la colocación del agua, labranza con azada y siembra con consumo real de semillas de Farm87. Tras la siembra, el agricultor atiende los tres puestos de árboles. Se conservan los límites de entrega de brotes de la comunidad y la cola .57 para replantar después de talar.
- El almacén ya no reclama un viaje por una mochila vacía. Se reservan materiales de trabajo; el bancal incompleto evita depósitos prematuros salvo mochila muy ocupada.
- Un constructor sin tierra puede recoger tierra superficial en una zona exterior acotada, con animación y recogida real, usando los permisos y eventos nativos. No se le regalan materiales de construcción.
- Se añade una línea de diagnóstico al log como máximo cada 1200 ticks por NPC elegible: UUID, oficio, necesidad, tarea, posición y estado del kit. Buscar `[TacZ NPC tactical.58]`.

## Pruebas realizadas

| Caso | Resultado en simulación |
|---|---|
| Mismo dispatcher y terreno, sin recolocar al actor durante la rutina | .57 falla: 2/8 cultivos; .58 completa 8/8 |
| NPC sin insumos | Coloca una fuente de agua, usa azada y consume ocho semillas |
| NPC antiguo marcado como equipado, pero solo con azada | Completa kit y termina el bancal |
| Continuar después de sembrar | Planta tres brotes reales; mantiene los ocho cultivos |
| Repetir inicialización | No regenera semillas consumidas |
| Dos NPCs en la misma base | Un agricultor y otro constructor; completan el bancal compartido |
| Mochila vacía / stock del constructor | No inventa un aporte; conserva stock útil |
| Asignación de comida abandonada | Caduca y puede tomarla otro trabajador |
| Extracción superficial de tierra | Consume tres pasos de trabajo visibles, rompe un bloque y recoge su tierra |
| Regresiones .56/.57 | Pasan las siete suites anteriores de inventario, colocación, diálogo, guardas, trabajo, replantación y recuperación |

Las pruebas nuevas ejecutan Life79.work, navegación, colisiones y pasos del actor contra las clases del JAR. El escenario es plano y simulado. No son una prueba de Minecraft ni de tu modpack. Los registros conservan una advertencia por `Footprint` ausente en la simulación de decoración y una excepción deliberada de la prueba de recuperación de máquinas; no se presentan como fallos observados en tu juego.

## Cómo probar en el juego

1. De día, usa una comunidad existente con cofre, cama, horno y mesa registrados, terreno accesible y descubierto alrededor, sin peligro inmediato ni órdenes de seguir/quedarse. También sirve un NPC antiguo sin kit completo.
2. La falta del bancal debe asignar un agricultor aunque antes tuviera otro oficio. Los otros deben atender defensa/materiales. El trabajo comunitario sigue requiriendo una base lista: esta entrega no afirma resolver todos los refugios iniciales incompletos.
3. Observa bloques reales: ocho tierras labradas, agua central y ocho siembras. Debe trabajar alrededor del campo. Después, en terreno adecuado y sin árboles disponibles, debe plantar los tres brotes de su zona.
4. Con dos NPCs, confirma que no intenten trabajar simultáneamente en el mismo bancal. La presencia de enemigos, noche o una orden explícita puede interrumpir el trabajo normalmente.
5. Si no avanza, el nuevo diagnóstico del log permite ver oficio, necesidad, posición e insumos sin depender del rótulo de la cabeza. El identificador del kit nuevo es `npc91_kit_completo`; la necesidad es `npc91_necesidad`.

## Límites reales

No pude ejecutar Minecraft, comprobar la animación de agacharse en el cliente, puertas de todos los mods, terrenos con desnivel, crecimiento y cosechas durante horas, completar todo el perímetro, ni probar partidas grandes. El plan sigue siendo un bancal compartido de ocho cultivos y tres puestos de árboles: no es todavía una economía dimensionada por población. La selección de trabajos es una política básica de necesidades, no el sistema completo de sociedades pedido anteriormente. Las tareas militares y de caza existentes se conservan en el JAR, pero esta política prioriza comida y construcción para trabajadores comunitarios y no les asigna caza al azar. La extracción de tierra es superficial y limitada, no una cantera ilimitada. No se incorporó todo SmartNpc ni se cambiaron los otros mods.

## Archivos

Clases modificadas mediante cinco enlaces: Farm87 (work y starter), TradeWorld87 (approach), Community87 (workNative56), Settlement87 (profession). Immersion82 actualiza la versión del diagnóstico; META-INF/neoforge.mods.toml actualiza la versión cargada. Se añaden Approach91 y Needs91 con sus clases auxiliares.

Fuentes, constructor ASM, scripts, pruebas y resultados en `fuentes-y-pruebas.zip`. `build.py` exige el SHA256 exacto de la .57 publicada. Requiere Java 21, ECJ, ASM 9 y Gson; acepta rutas `--base`, `--tools`, `--gson`. `test.py` acepta `--jar`, `--tools`, `--gson`. Las APIs simuladas solo están en fuentes de prueba y no se empaquetan en el mod.
