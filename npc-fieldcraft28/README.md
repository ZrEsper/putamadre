# NPC .28 — salir de huecos y observar con calma

Base .27 fijada por SHA en `inputs.json`. Compilar con `python npc-fieldcraft28/build.py` y validar con `python npc-fieldcraft28/test.py` desde la raíz. Reutiliza Java21, ECJ/ASM y fixtures existentes de npc-social17/npc-roles22. No cambia los otros mods.

## Escape local

Field28 se ejecuta desde el seguimiento final del tick nativo, después de los controles de movimiento. Primero registra posición y espera tres segundos sin avance antes de intervenir. Las pausas deliberadas de inspección, cobertura y tratamiento no cuentan como un atasco. Si hay un objetivo visible más alto, un ruido reciente en una posición inaccesible o un encierro local, comprueba salidas. Un enemigo ya a distancia de golpe tiene prioridad de combate. En agua cancela la intervención y devuelve el control a la IA normal.

Intenta caminar por una salida disponible. Para subir puede mantener un bloque como escalón, abrir los dos espacios de cabeza/pies sobre él y ordenar el movimiento y salto nativos. Si falta apoyo, puede poner un bloque común del inventario: no fabrica bloques gratis. Al romper usa drops reales de Minecraft; la recogida sigue siendo la nativa. No aprende mediante machine learning, no se teletransporta y no calcula una ruta de construcción arbitraria por todo el mapa.

Las acciones tardan entre uno y cuatro segundos según dureza, con gesto y progreso de rotura. Comprueba otra vez bloque, distancia, chunk y permisos al terminar. Tras cada modificación vuelve a intentar caminar. Conserva gravedad y colisiones. No altera la altura física del NPC.

Limita cada NPC a seis modificaciones por ventana de 600ticks (30s), una sola acción en curso y un cambio por mundo/tick. Examina cuatro direcciones locales; como máximo ocho comprobaciones de ruta al buscar salida. Solo abre bloques comunes de tierra, piedra, ladrillos de piedra o tablas incluidos en una lista; dureza máxima3. No rompe inventarios, bloques con block entity, minerales, bedrock ni bloques ajenos a esa lista. Coloca únicamente materiales comunes admitidos que ya tenga en el inventario. No garantiza salir de todas las trampas o materiales del modpack.

Respeta `EventHooks.canEntityGrief`, `onEntityDestroyBlock` y `onBlockPlace`. Si un evento cancela una colocación, restaura el BlockSnapshot anterior y no consume materiales. No coloca sobre una entidad. `mobGriefing` y los vetos de mods que intervengan en esos eventos siguen vigentes. No carga chunks para trabajar.

## Esquinas y oído

La inspección anterior duraba24ticks y cambiaba bruscamente entre dos orientaciones relativas al rumbo actual. Ahora captura un rumbo fijo, gira suavemente y mantiene pausas mirando ambos lados. Dura90ticks (4,5s), con velocidad máxima de giro de5grados/tick. Se agacha, pausa la navegación y mantiene el arma preparada si tiene una. Las celdas ya revisadas se guardan en una lista limitada a32; no repite indefinidamente la misma esquina.

Se añade escucha en el evento servidor de sonidos ya registrado por el mod. Detecta pasos a12bloques y disparos/explosiones a32, con hasta12 NPC por evento. Usa `PlayLevelSoundEvent.AtPosition.getPosition()` y `AtEntity.getEntity()` del API real NeoForge1.21.1. También recibe el evento de tiro TaCZ existente. El origen se guarda como coordenadas, sin seguir entidades ocultas. Un disparo tiene más prioridad que pasos; ignora lluvia y los pasos propios. Un enemigo visible interrumpe el reconocimiento para que no miren a una pared mientras les disparan.

Las emisiones se agrupan con caché limitada a64 por mundo. Los ruidos repetidos no reinician indefinidamente el barrido; hay un intervalo de atención para no inmovilizar grupos por sus propios pasos. El oído no da permiso nuevo para atacar a neutrales ni visión a través de paredes. Se conserva la prioridad hostil de .27.

## Verificación

160 comprobaciones numeradas y una prueba de modelo pasadas con Java `-Xverify:all`: sonido de pasos a través del enganche real, prioridad de disparos, giro y pausas, interrupción por enemigo, planificación de un escalón con dos roturas, paso posterior a navegación, detección del bloqueo desde el tick, materiales consumidos, rollback de colocación, protección, chunks, colisión y presupuesto. Se mantienen las pruebas de .26/.27 de mirada, visor, fogonazo, lesiones, diálogo, curación y provisiones.

ASM BasicVerifier revisa las clases tácticas empaquetadas y verifica el enganche de sonido. Las firmas de los eventos NeoForge y BlockSnapshot se cotejaron con su fuente oficial; referencias y hashes en `native-api-audit.json`. Los93 recursos/data anteriores y la configuración Mixin son idénticos a la base. Las fixtures no se empaquetan.

No se ejecutó Minecraft con el mapa/modpack. Estas comprobaciones no prueban la salida de todos los huecos ni el resultado visual final. La navegación física, los bloques especiales y los mods de protección requieren prueba dentro del juego.
