# tactical.57 — trabajo sostenido, refugio económico y adaptación de comportamientos SmartNpc

Base: tactical.56 publicada en [baconwithd/Steph](https://github.com/baconwithd/Steph/tree/4c7cb5bbc2e7ac6390e12e6925dcb09c922abec2/tacznpcs/tactical.56). Se conserva ese parche de estabilidad y los sistemas previos de armas, conversación local, reputación, comunidades, agricultura y servicios Doomsday.

## Instalación

Cierra Minecraft y sustituye el único JAR TacZ NPCs anterior por `tacznpcs-2.1.0-1.21.1-tactical.57-focused-work.jar`. No requiere borrar NPCs, bases ni inventarios. No cambies los otros mods de gore/cadáveres/decoración por esta entrega. Conserva una copia del mundo antes de probar construcciones nuevas.

Esta adaptación funciona en tus entidades TaCZ actuales y no necesita el JAR SmartNpc aparte. No transforma ni controla entidades de SmartNpc si instalas también ese mod; tendrían su propia población y comportamiento.

## Qué cambia

### Trabajo y distracciones

- El trabajo comunitario reserva control entre decisiones. Un nuevo Goal de prioridad 0 con flags MOVE/LOOK impide que paseo y mirada aleatorios sobrescriban su ruta mientras trabaja. Se instala una vez DESPUÉS de la limpieza de goals, también en NPCs existentes.
- Cede ante combate, agua, sueño y pérdida de la tarea. La reserva no detiene por sí misma la navegación que el trabajo ya pidió. Las tareas sin progreso tienen espera y liberación; no se convierten en una ocupación permanente cuando el estado es descanso o faltan materiales.
- El control de progreso cubre también desplazamientos comunitarios como llevar aportes. A los 400 ticks sin movimiento/progreso de inventario o construcción libera y espera 200. Los límites nativos de la .56 siguen activos.
- Se retira el inicio automático de conversaciones completas por proximidad. Si está ocioso puede decir una línea sin abrir opciones ni robar foco: 2400 ticks por NPC y 600 ticks de separación por jugador. Los textos están en JSON. Las ofertas automáticas no se inician mientras está ocupado; siguen ejecutándose antes las actualizaciones de navegación, necesidades y trabajos.
- El jugador conserva la interacción manual para conversar, incluso con un trabajador; se mantienen sus protecciones, tokens, opciones y reglas de seguimiento. Los comentarios de trabajo y conversación ambiental entre compañeros siguen siendo audibles sin abrir un diálogo obligatorio con el jugador.

### Tala y replantado

- Una tala confirmada del tronco de un árbol sobre suelo válido crea una solicitud persistente de replantado en ese tocón. No se generan solicitudes por golpes fallidos o por talar un poste elevado.
- Planta el brote de la especie cuando el lugar está cargado, libre, permitido, visible, a distancia de interacción y admite la planta. Incluye el propágulo de manglar como equivalente cuando corresponde.
- Usa brotes existentes; si no tiene, el juego concede como máximo UNO por tocón confirmado, registrado en la solicitud. Una cancelación o interrupción no concede otros indefinidamente. El plantado consume un objeto real y respeta eventos de colocación.
- Hasta 32 solicitudes por NPC; vencen tras 24000 ticks. Si se aleja demasiado, el lugar cambia o no es accesible, puede quedar pendiente hasta expirar. El crecimiento posterior es el nativo. No se ha reemplazado la tala completa por un talador automático de todos los troncos de la copa.

### Colocación visible y cadáveres

- Refugios nativos, muebles y colocaciones comunitarias mantienen la mirada al bloque durante diez ticks después de una colocación confirmada, con movimiento del brazo y sonido nativo. Las cancelaciones no simulan una colocación exitosa.
- Se excluyen fuentes vacías antes de seleccionarlas como botín. Al terminar de vaciar un cadáver, se registra que ese NPC ya lo revisó; los cadáveres de varias listas se marcan tras comprobar todas. Hasta 256 identificadores por NPC, persistentes en sus datos. Para remains se incorpora la fecha de creación cuando el proveedor la ofrece.
- No elimina cadáveres, no borra inventarios y no marca como terminado un cuerpo que aún conserva objetos por transferir. Los objetos tirados siguen tratándose como objetos tirados, no como cadáveres.

## Refugio económico y ampliación según las imágenes

El refugio económico es una alternativa a una construcción inicial incompleta y más cara. Se contempla después de 200 ticks desde que esta versión observa ese intento, si faltan tablones para acabarlo y dispone de seis tablones o nueve losas. No cancela un refugio ya terminado.

1. Busca entre seis ubicaciones próximas de suelo natural sólido. Rechaza líquidos cercanos, bloques de inventario, áreas no permitidas, estructuras ajenas y terreno incompatible. No rompe la obra anterior ni edificios del jugador.
2. Excava PRIMERO el acceso escalonado, después un interior de 3×3 con dos bloques de altura y lo cubre con nueve losas. Usa drops, herramientas y permisos reales. Puede fabricar las losas con la receta real; no concede madera.
3. Registra ese refugio como hogar. El selector de muebles deja libre su pasillo de entrada, evitando colocar allí una cama. El amueblado posterior conserva el sistema nativo.
4. Con base operativa, huerto comunitario irrigado y ocho cultivos ya plantados, el dueño puede ampliar si tiene el presupuesto de materiales de una etapa. Primero añade un patio cercado; después una habitación lateral con tres ventanas de vidrio y techo de losas. El techo ocupado y la escalera original se conservan.

El patio del plano es un espacio reservado: esta entrega no trasplanta automáticamente a él un huerto comunitario ya existente. Tampoco crea una segunda planta, arquitecturas ilimitadas ni reproduce cada proporción de las fotos. La habitación añadida tiene dos bloques de altura libre, una entrada de dos bloques y un tejado propio.

Los planos editables están en `data/tacznpcs/dialogue/work90.json` dentro del JAR. Se leen al iniciar; `/npcreload` no recarga ese recurso auxiliar. El patio cuesta 15 tablones. La habitación cuesta 27 tablones, 3 bloques de vidrio y 25 losas; las losas pueden fabricarse si sobran tablones para ello. Comprueba materiales antes de empezar una etapa. Un bloqueo posterior conserva el avance y aplica espera, sin destruir el refugio útil para reiniciar.

## Qué se aprovechó de SmartNpc

Se revisó el JAR SmartNpc 2.0.1 suministrado, de **pla_is_me**, cuya metadata declara GNU GENERAL PUBLIC LICENSE. Las referencias concretas fueron `TreeAi` y `PathStuckFallbackAi`.

La integración de esta entrega adapta esas ideas mediante implementación propia para AutoGunOperator:

- Búsqueda local primero, ordenada por distancia, reanudable y con límite de cuatro columnas por llamada. Limita las lecturas de selección y la comprobación de hojas. Exige suelo natural y al menos tres hojas próximas antes de seleccionar el tronco.
- Recuperación de ruta tras 100 ticks sin avance: hasta cuatro candidatos de rodeo, un waypoint temporal y regreso al objetivo. El trabajo no sobrescribe ese rodeo cada tick. Tras dos intentos sin resultado utiliza la espera/descartado de la navegación nativa.
- No teletransporta, no deja caer al NPC desde grandes alturas y no destruye obstáculos ni puertas del jugador para abrir paso. Los pasos alternativos admiten un desnivel máximo de un bloque.

No incorpora las entidades, renderer, combate EpicFight/BetterCombat, todas las recetas ni todos los goals de SmartNpc. No se añaden servicios de IA externos. `reference.json` identifica el JAR inspeccionado y su SHA256; no se distribuyen sus clases ni recursos.

## Validación y límites reales

**NO se ejecutó Minecraft ni el modpack real.** Se compila Java 21, se comprueba bytecode y se cargan las clases parcheadas con `-Xverify:all` en siete suites JVM.

| Área | Comprobado con APIs simuladas | Pendiente en Minecraft |
| --- | --- | --- |
| Trabajo | Reserva MOVE/LOOK, cede a combate/sueño, instalación única posterior a limpieza | Competencia real con todos los goals y otros mods |
| Árboles | Selector → tala original → solicitud persistida → brote real; lecturas limitadas | Árboles y especies del mundo/modpack, crecimiento y tala de copas |
| Colocación | Brazo, sonido, mirada temporal y cancelación sin consumo | Apariencia del gesto y giro del modelo con tus animaciones |
| Cadáveres | Exclusión de vacíos y memoria persistida | Proveedores reales de cadáveres y sus casos de varias listas |
| Refugio | El método Shelter77.work completa excavación, consume nueve losas y registra hogar | Recorrido real por escalera e interior, colisiones y amueblado |
| Ampliación | Construcción de patio y habitación, consumo exacto y entradas libres | Acceso real a cada bloque, terreno, acopio y convivencia |
| Rutas | Tick nativo inicia rodeo, go conserva waypoint y termina en espera tras fallos | Ventanas, paneles, puertas y rutas del mapa real |

Las dos suites nuevas hacen 149 comprobaciones, incluidas verificaciones por paso de construcción. En las pruebas de refugio y ampliación se controlan las posiciones del actor para aislar permisos, materiales y avance del método real. **Eso no prueba una rutina autónoma completa de desplazamiento** ni equivale a dos días de supervivencia dentro del juego. Las cinco suites de estabilidad .56 siguen pasando. El log de pruebas incluye una excepción de lectura de máquina inyectada deliberadamente para verificar su recuperación.

El terreno urbano puede no ofrecer suelo natural apropiado para excavar. Si no hay sitio, losas/madera, herramientas adecuadas o permiso, no se construye ese refugio. No garantiza completar cualquier edificio con cualquier inventario. Persisten los límites geométricos de comunidades y profesiones previos. Entierros, duelo/venganza y ampliaciones de compatibilidad Furniture/Supplementaries siguen pendientes.

## Prueba breve en tu instancia

- Con un NPC que ya estaba construyendo, observa si conserva ruta y trabajo, sin abrirte conversación al acercarte. Interactúa tú para comprobar que sigue disponible la charla manual.
- Deja uno ocioso cerca: debe emitir un saludo espaciado, sin respuestas obligatorias.
- Haz que tale un árbol natural con hojas y suelo válido: revisa el tocón y el objeto brote consumido.
- Deja un cadáver vacío cerca tras el saqueo: no debería ser elegido como nueva tarea.
- En terreno de tierra/cesped sólido, un refugio incompleto con recursos limitados puede pasar al excavado. Confirma escalera libre, techo y hogar registrado.
- Una ampliación requiere huerto plantado, base operativa y excedentes. Comprueba que no use materiales inexistentes ni tape las entradas.
- Para diagnóstico, copia `npc87_actividad`, `npc78_navegacion`, `npc90_trabajo_hasta`, `npc90_refugio_excavado`, `npc90_casa`, `npc90_replantar` y `npc90_cadaveres_revisados` del NPC afectado.

## Fuentes y reconstrucción

El ZIP incluye el delta de código, aplicador ASM, API mínima de Goal solo para compilación, pruebas, planos y manifiestos de referencia/cambios. Las APIs simuladas no entran en el JAR del mod.

```sh
python3 build.py --base /ruta/tactical.56-stability.jar --tools /ruta/herramientas --gson /ruta/gson.jar
python3 test.py
```

La carpeta de herramientas debe contener `ecj.jar`, `asm.jar`, `asm-tree.jar` y `asm-analysis.jar`. El build comprueba el SHA256 de la base exacta .56. `test.py` tiene por defecto las rutas de herramientas de este entorno; ajústalas al ejecutar fuera de él.
