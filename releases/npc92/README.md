# tactical.59 — trabajo sin mobiliario completo

Reemplaza solo tacznpcs .58 por `tacznpcs-2.1.0-1.21.1-tactical.59-bootstrap-work.jar`. Deja un solo tacznpcs. Para evaluar las rutinas usa `/tick rate 20`, de día, sin combate ni una orden de seguir/quedarse. Conserva copia del mundo.

## Lo que confirma latest.log

El archivo recibido el 23 de octubre carga realmente tactical.58. El banner de arranque todavía decía .57 porque era una cadena antigua; también se corrige aquí. No registra ninguna de las líneas de trabajo comunitario .58, de modo que el log no identifica qué condición excluye a cada NPC.

Hay además problemas del servidor que afectan a todos los NPCs:

- Los avisos de sobrecarga equivalen a una tasa de 10000 ticks/s: 1014 ms de atraso se reportan como 10140 ticks. Prueba a 20 antes de valorar la rutina.
- A las 16:30:40 aparece `ConcurrentModificationException: ThreadLocalRandom accessed from a different thread` en `TNPC$StructureSpawnHandler.onFinalizeSpawn`, línea 106. La selección de reemplazo de entidades de estructuras usa el RNG del ServerLevel desde c2me-worker. Se cambia únicamente esa tirada a un RNG local al hilo.
- El watchdog registra un tick de 162377 ms. El hilo del servidor espera un chunk desde `zomboid_hordes -> LootRestock.remember -> BlockEntity.setChanged`. Esta entrega NO modifica zomboid_hordes ni afirma resolver ese bloqueo. Mientras el servidor esté atascado no puede avanzar ninguna rutina de NPC.

## Correcciones

**Límite de exploración:** máximo 1200 ticks (60 segundos a 20 TPS), por debajo de dos minutos. Al vencer, la rutina conserva un objetivo de asentamiento: atiende necesidades si tiene base; con hogar conocido inicia el ancla provisional; sin hogar busca cobertura existente o crea un plan nativo de refugio validando el sitio. Si no hay sitio, busca terreno transitable con rutas reales; si protección/configuración lo impide, muestra el bloqueo y reintenta el mismo objetivo. No se teletransporta ni concede materiales para fingir progreso. Las órdenes explícitas, combate, sueño y servicio tienen prioridad. Los campos persistentes son `npc92_explorar_limite`, `npc92_objetivo_forzado`, `npc92_objetivo_siguiente`. El tiempo es de juego a la tasa indicada, no una garantía de tiempo de pared durante un congelamiento del servidor.

1. El trabajo comunitario ya no exige cofre, cama, horno y mesa completos para comenzar a cultivar. Mantiene las comprobaciones de supervivencia, combate, conversación y órdenes explícitas.
2. Una ubicación de hogar ya conocida o un campamento terminado pueden establecer el ancla provisional si tienen una casilla transitable y permitida. No se inventa una base en la posición actual de un NPC sin hogar y no se da por terminado un refugio en obras.
3. Los constructores pueden intentar amueblar mediante las funciones nativas mientras el agricultor atiende el campo; una cama imposible de colocar no excluye el cultivo.
4. Las acciones reales de Settlement87.status actualizan también `npc79_tarea`. El rótulo viejo «explorar» ya no queda intacto cuando el NPC está sembrando o construyendo. No se sustituye toda exploración legítima por un texto distinto.
5. El diagnóstico se ejecuta desde Job90.tick antes del filtro de trabajo y registra cada 1200 ticks la condición de entrada, orden, tarea, acción, base y posición. Buscar `[TacZ NPC tactical.59 gate]`. Permite distinguir `sin_ancla_de_base`, combate, alerta/diálogo/IA inactiva, servicio, bloqueo de supervivencia, orden explícita y base incompleta.
6. Se conserva el kit agrícola limitado, uso de azada, agua, consumo de semillas y replantación de la .58.

## Pruebas

| Escenario | Resultado |
|---|---|
| Mismo dispatcher real, mobiliario imposible de completar, actor camina por navegación simulada | .58: 0/8 cultivos; .59: 8/8 más una fuente de agua |
| Rótulo inicial «explorar» | Se actualiza al ejecutar acciones de trabajo |
| Hogar conocido sin base amueblada | Puede formar ancla provisional |
| NPC sin hogar conocido | No recibe una base inventada |
| Orden follow y combate | La rutina no los sustituye |
| Diagnóstico con base incompleta | Se ejecuta antes de exigir mobiliario |
| Exploración vencida sin hogar | Crea plan nativo de refugio y lo conserva en la siguiente decisión; cede ante orden y combate |
| Tirada de aparición desde cuatro hilos, 40000 muestras | Sin excepción, rango [0,1) |
| Ocho suites anteriores | Pasan inventarios, colocación, diálogo, guardas, tareas, replantación, navegación adaptada y rutina agrícola |

Se verifica además la estructura de bytecode de todas las clases. El parche exige encontrar exactamente los enlaces previstos, incluida una única lectura RNG en onFinalizeSpawn.

Las pruebas son simuladas, NO Minecraft. No pude comprobar C2ME con generación real de estructuras, terreno irregular, una partida con miles de ticks por segundo, ni el bloqueo de LootRestock. Cambiar la tirada concreta no certifica toda la compatibilidad de generación multihilo. La búsqueda forzada de refugio usa validación y materiales nativos; no garantiza que el terreno tenga un sitio viable ni que los recursos alcancen. Un servidor congelado no ejecuta el límite ni el trabajo. No se implementa aquí la economía completa de facciones.

## Archivos cambiados

Se añaden Bootstrap92 y Deadline92; se enlazan Life79.work, Community87.eligible, Needs91.eligible/work, Settlement87.status, Job90.tick y TNPC$StructureSpawnHandler.onFinalizeSpawn. Se actualizan las cadenas de versión, incluidos los mensajes concatenados y META-INF/neoforge.mods.toml. No se recompilan las entidades completas ni se cambian los otros mods.

Fuentes, constructor ASM, scripts, fixtures y resultados en `fuentes-y-pruebas.zip`. Build exige la .58 exacta (SHA256 96c864ae1fafe05ad9fe93e0b5180dc2051660f1d3e978d1617cff16c5462747), Java 21, ECJ, ASM y Gson. `build.py`: --base, --tools, --gson. `test.py`: --jar, --tools, --gson.
