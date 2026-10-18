# NPC .29 — decisiones y movimientos estables

Base .28 fijada por SHA en inputs.json. Desde la raíz: `python npc-command29/build.py` y `python npc-command29/test.py`. Usa Java21, ECJ/ASM y las fixtures existentes de npc-social17/npc-roles22. No cambia los demás mods.

## Causas encontradas

Tactics24 cambiaba agachado/de pie, postura tumbada y desplazamientos laterales mediante el reloj. Combat26 permitía evadir por una recarga o un disparo oído aunque no recibiera daño. TeamBrain.gunTick todavía podía ordenar movimientos laterales alternos cuando un aliado bloqueaba el disparo, por encima de la ruta táctica. La pérdida de visión podía cancelar una cobertura recién alcanzada y convertirla inmediatamente en búsqueda. Client26 hacía que la orientación del cuerpo del FakePlayer fuese siempre la de la cabeza, incluyendo observaciones ambientales.

## Control de combate

Command29 mantiene una sola orden: sostener posición, avanzar, ir a una cobertura seleccionada, permanecer allí o buscar una última posición conocida. Conserva el destino mientras lo recorre; actualiza la solicitud de ruta cada30ticks, no cada fotograma. Un enemigo a menos de3bloques puede interrumpirla. Avanza cuando el enemigo está fuera de20bloques y hay ruta, en vez de caminar lateralmente por tiempo. En distancia de tiro sostiene la posición. La postura agachada usa umbrales de10/7bloques para evitar alternancia en el borde.

La recarga o un impacto real pueden motivar cobertura, con intervalo de6segundos entre selecciones. Debe ser una posición alcanzable que realmente corte la línea hacia el enemigo; no corre hacia el punto menos malo de una zona completamente abierta. Mantiene esa elección hasta llegar o vencer un plazo de5segundos. Al llegar se queda al menos2segundos; puede terminar recarga o curarse sin abandonarla al perder contacto visual. La atención a un enemigo pegado sigue teniendo prioridad. La curación conserva la venda real y solo se intenta sin visión directa del enemigo. La continuación desde el tick final permite terminar la acción aunque el objetivo de disparo deje de ejecutarse al perder su blanco.

TeamBrain.move se envuelve para que sus órdenes antiguas no sobrescriban la navegación cuando Command29 tiene el control. Fuera de combate/cobertura sigue llamando a la implementación original. Se conserva el tiro, la cadencia, munición y melee nativos. El nuevo combate no ordena deslizamientos automáticos; el helper permanece por compatibilidad y ya no acepta solo ruido o recarga como detonante. No se eliminan los modelos o paquetes de animación anteriores.

## Contacto y orientación

Un contacto recién visto se conserva hasta40ticks tras perder visión; no autoriza disparar a través de paredes ni actualiza coordenadas de un jugador oculto. Esto evita alternar inmediatamente entre combate y barridos ambientales ante una oclusión breve. El reconocimiento pausado de esquinas y los pasos de .28 siguen activos fuera de esa acción de combate.

Aim29 mantiene un rumbo de apuntado independiente del rumbo de navegación, con un solo paso angular por tick: máximo12grados en yaw y8 en pitch. Las llamadas repetidas del mismo tick no aceleran el giro. El enganche final sigue reparando los cambios de rumbo del MoveControl, pero ya no fuerza un giro instantáneo hasta el objetivo. El error angular se devuelve al camino nativo que decide el tiro.

La cabeza y el cuerpo del FakePlayer usan orientaciones separadas. Al escuchar, la cabeza gira gradualmente; el torso solo acompaña hasta2grados/tick cuando la diferencia supera35grados. Se mantienen las correcciones anteriores del visor, fogonazo, ropa y equipo.

La ingeniería de .28 reconoce la pausa deliberada de cobertura y no la confunde con atasco. Se conservan su construcción, rotura, límites, inventario y permisos.

## Verificación y límites

185 comprobaciones numeradas y una prueba del modelo pasadas con `-Xverify:all`. Incluyen una secuencia de200ticks sin movimiento lateral periódico, destino conservado, frecuencia de solicitud de ruta, cobertura que persiste al perder visión/blanco, bloqueo de órdenes nativas competidoras, navegación civil conservada, expiración de contacto sin tiro por paredes y giro separado de cabeza/torso. También se mantienen las regresiones de .26–.28 de sonido, ingeniería, protección, lesiones, equipo y curación. Las fixtures de vista separan yaw corporal y de cabeza, como el API real.

ASM BasicVerifier revisa las clases tácticas y el wrapper nativo de navegación. Los93 recursos/data y la configuración Mixin quedan idénticos. Las fixtures no se empaquetan.

No se ejecutó Minecraft con el mapa/modpack ni se midieron FPS/TPS. Estas pruebas comprueban decisiones y llamadas, no garantizan calidad visual o tácticas completas de una escuadra. Las rutas en geometría real, la interacción con otras IA y las animaciones necesitan validación dentro del juego.
