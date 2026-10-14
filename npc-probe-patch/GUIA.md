# NPC de pruebas con ocho partes del cuerpo

## Instalar

Cierra Minecraft y detén el servidor. Reemplaza `tacznpcs...tactical.14-corpse-cargo.jar` por **tactical.15-playerlike-test-npc.jar**, en cliente y servidor. Deja una sola versión de TacZ NPCs. Conserva TaCZ, Player Animator y las demás dependencias que ya usabas.

No necesitas cambiar plantillas ni archivos de configuración para usar los comandos. Debes tener permisos de administrador y ejecutarlos dentro del juego.

## Empezar en un minuto

1. Ponte en un lugar despejado y sostén el arma que quieras probar.
2. Escribe `/npcprueba crear`. Aparece delante de ti, en modo **quieto**, con una copia de tu equipo.
3. Dispara o golpea al NPC. Luego escribe `/npcprueba estado` para ver qué parte perdió salud.
4. Usa `/npcprueba derribar` para probar la reanimación sin tener que matarlo a tiros.
5. Con la mano vacía, haz clic derecho al NPC y permanece a menos de 3 bloques durante 8 segundos. Recibir golpes o alejarte interrumpe el proceso.
6. Después apunta a una parte herida y haz clic derecho con una venda o un apósito de First Aid.

Para repetir sin perder tiempo, `/npcprueba reiniciar` restaura todas las partes del NPC vivo. Si ya murió definitivamente, usa `/npcprueba crear` para tener otro.

**Es un NPC adaptado para pruebas, no un jugador real ni un FakePlayer del servidor.** Utiliza disparo, recarga, melee, equipo, armadura y muerte del NPC original. Su sistema de ocho partes, derribo, curación y reanimación es una adaptación propia: no sustituye una prueba con un jugador real de los eventos, habilidades, interfaz y lógica interna exclusivos de First Aid.

## Las ocho partes

| Parte | Qué ocurre si llega a cero |
|---|---|
| Cabeza | Es crítica: el NPC queda derribado. |
| Torso | Es crítico: el NPC queda derribado. |
| Brazo izquierdo | Reduce fuerza de melee; ambos brazos destruidos reducen más. |
| Brazo derecho | Lo mismo, de forma independiente. |
| Pierna izquierda | Reduce la velocidad; perder ambas reduce más. |
| Pierna derecha | Lo mismo, de forma independiente. |
| Pie izquierdo | Añade una penalización de movilidad. |
| Pie derecho | Lo mismo, de forma independiente. |

**Izquierda y derecha son las del NPC**, no las de quien lo está mirando. Destruir una extremidad no provoca automáticamente una muerte definitiva. El estado de cada parte se conserva al guardar el mundo.

Si First Aid está disponible, al crear el NPC se intenta copiar la salud máxima de las partes del administrador que lo creó. Si no se puede leer esa integración, usa: cabeza 6, torso 12, brazos 6 cada uno, piernas 8 cada una y pies 4 cada uno. No copia las heridas que ya tenía el jugador.

El daño recibido se toma del evento de daño final, después de los cálculos de armadura y resistencias. Para melee se usa el rayo de mirada contra la caja del NPC; para proyectiles, su posición disponible o la información de headshot. Cuando no puede identificar una posición, usa el torso. Son zonas geométricas: no se simula exactamente el sistema interno de heridas de First Aid ni un hitbox separado de cada hueso de la animación.

Para una prueba repetible de una parte concreta, usa `/npcprueba herir cabeza` o sustituye cabeza por uno de estos nombres: `torso`, `brazo_izquierdo`, `brazo_derecho`, `pierna_izquierda`, `pierna_derecha`, `pie_izquierdo`, `pie_derecho`. Cada comando aplica 4 puntos a esa parte sin pasar por un ataque real; sirve para preparar estados de prueba.

## Derribo, reanimación y muerte

**Derribado** sigue vivo con una salud general mínima, deja de combatir y se coloca en pose de derribo. Una cabeza o un torso a cero inicia este estado. Tienes 60 segundos de tiempo de juego para reanimarlo mientras está cargado; el plazo se conserva en el mundo. Tras tres impactos adicionales de un atacante/proyectil, o al agotarse el tiempo, muere definitivamente. Daño de vacío o muerte administrativa sigue la ruta de muerte definitiva.

**Reanimar naturalmente:** mano vacía, clic derecho y 8 segundos a menos de 3 bloques. El proceso se cancela si el ayudante deja de estar disponible, se aleja o recibe un golpe; atacar al NPC derribado también interrumpe el intento. Solo hay un intento activo por NPC. No cambia a otro NPC de forma automática.

Reanimarlo devuelve al menos 1 punto a cabeza y torso, **pero mantiene las heridas de las otras partes**. Necesita curación después. Si era enemigo, conserva ese modo: para una prueba tranquila cambia a quieto antes de derribarlo.

**Muerte definitiva:** usa la muerte y los sistemas de cadáver/cargo originales de tactical.14. No se genera un cadáver al quedar simplemente derribado. Un muerto definitivo no se levanta con vendas: crea otro NPC de pruebas para repetir. `/npcprueba retirar` elimina el NPC seleccionado sin probar su muerte.

## Curación

Apunta a la parte herida y usa clic derecho:

- **Venda `firstaid:bandage`:** hasta 6 puntos en esa parte.
- **Apósito `firstaid:plaster`:** hasta 3 puntos en esa parte.
- No supera la salud máxima de la parte.
- En supervivencia consume una unidad cuando la parte necesita curarse; en creativo no la consume.
- Si la parte ya está sana, no gasta el objeto.
- Una venda no sustituye la reanimación de un derribado. Primero levántalo con la mano vacía.

Estos valores son los de la adaptación del NPC, no una reproducción de todos los tiempos o efectos configurables de First Aid. Analgésicos, férulas, inyectores y consumibles de otros mods conservan sus funciones originales para los objetivos que soportan; esta adaptación no añade tratamiento específico con ellos al NPC.

Clic derecho sin un tratamiento compatible, o `/npcprueba estado`, muestra la salud de todas las partes. El comando se lee mejor que la barra de acción si el texto completo no cabe.

## Modos y combate

| Modo | Qué hace |
|---|---|
| Quieto | No combate ni persigue. Puedes dispararle o golpearlo para probar daño. Es el modo inicial. |
| Aliado | Busca monstruos cercanos, los combate y sigue a su propietario cuando no tiene un objetivo. Usa la navegación normal del mob; no reproduce todas las interacciones de un jugador. |
| Enemigo | Ataca a su propietario cuando está vivo, cerca y en supervivencia. Respeta la línea de visión. No elige a otros jugadores como objetivo de prueba. |

Para disparar debe llevar un arma TaCZ válida. Usa los métodos originales de disparo, cooldown, apuntado y recarga del operador NPC. Mantiene las reglas de munición de TacZ NPCs, que pueden ser distintas de la mochila y consumo de munición de un jugador real.

Con arma de fuego, el melee usa el ataque cuerpo a cuerpo TaCZ. Con otro objeto, usa el ataque nativo del NPC y sus modificadores de equipo. No reproduce automáticamente todos los ataques exclusivos de LR Tactical que requieran un objeto Player.

## Todos los comandos

Los comandos actúan sobre **tu NPC de pruebas vivo más cercano a 32 bloques**. No toman un bandido normal ni un NPC de otro administrador.

| Comando | Para qué sirve |
|---|---|
| `/npcprueba` | Muestra ayuda rápida. |
| `/npcprueba crear` | Crea uno delante de ti, si no tienes otro vivo cerca. |
| `/npcprueba quieto` | Detiene su comportamiento de combate. |
| `/npcprueba aliado` | Activa defensa y seguimiento. |
| `/npcprueba enemigo` | Prueba sus ataques contra su propietario en supervivencia. |
| `/npcprueba equipo` | Copia lo que llevas en manos y armadura al NPC. No quita tu equipo. |
| `/npcprueba disparar` | Orden de disparo en modo enemigo, si hay arma válida y visión de su propietario. |
| `/npcprueba recargar` | Pide una recarga al operador TaCZ original. |
| `/npcprueba melee` | Orden de melee en modo enemigo, a corta distancia. |
| `/npcprueba estado` | Muestra las ocho partes, salud máxima, modo y estado. |
| `/npcprueba herir cabeza` | Prepara 4 puntos de daño en cabeza. Admite las otras siete partes. |
| `/npcprueba derribar` | Destruye el torso para preparar una prueba de reanimación. |
| `/npcprueba reanimar` | Reanimación instantánea administrativa. Para probar los 8 segundos, usa clic derecho. |
| `/npcprueba curar` | Restaura todas las partes del NPC vivo. Es una herramienta de administración, no consume vendas. |
| `/npcprueba reiniciar` | También restaura el NPC vivo, manteniendo equipo y modo. |
| `/npcprueba matar` | Fuerza una muerte definitiva para probar cadáver/cargo. |
| `/npcprueba retirar` | Elimina el NPC vivo seleccionado sin generar una muerte de prueba. |

El equipo es **una copia para pruebas de administrador**. El NPC puede dejar copias en el cadáver al morir: no es un sistema de compañeros de supervivencia ni de entrega de objetos reales. No aparece naturalmente y no da estos comandos a jugadores sin permisos.

## Comprobaciones y límites

130 comprobaciones con dobles de las APIs: ocho zonas, lados, daño independiente, daño final, partes críticas, penalizaciones, derribo, temporizador, cancelación de reanimación, heridas persistentes, recuperación, consumo de vendas/apósitos, muerte definitiva única y aislamiento de NPC normales. También se verifica el bytecode de los métodos modificados, la integridad del JAR y que todos los recursos originales permanezcan iguales.

**No se ejecutó Minecraft en este entorno.** Falta probar renderizado del derribo, navegación, registro de eventos real, disparos, tratamientos, guardado y cadáver/cargo con tu pack completo. No debe considerarse una verificación de que todas las funciones de un jugador funcionan en NPC.

Para una prueba inicial: crear quieto, herir un brazo, ver estado, curarlo con apósito, derribar, reanimar con mano vacía, comprobar que conserva heridas, y usar matar para revisar el cadáver. Después probar el equipo TaCZ y los modos de combate.
