# Tactical .53 — asentamientos, oficios y perímetro por etapas

Sustituye tactical .52 por .53. Conserva Ragdollified .34, remains .28, Gore .43 y los otros mods. Reinicia cliente y servidor si corresponde. Primero prueba una copia del mundo a 20 TPS. No requiere crear un mundo ni borrar NPCs.

## Campamentos y organización

Mantiene las bases, camas, cofres y hornos que ya colocaron. Bases compatibles a un máximo de 12 bloques horizontalmente y 3 en altura se organizan como un asentamiento persistente, con nombre, miembros, oficios y plan guardado en los datos sociales del mundo. Separa hostiles de civiles y respeta la pertenencia preexistente a facciones de jugador al agrupar. Estas comunidades se registran en el sistema social de NPCs; no crean facciones humanas ni administran nuevas facciones en la interfaz de Faction Friction.

Plan inicial respecto al centro conservado:

| Área | Ubicación |
|---|---|
| Campamento | Centro: las camas, hornos y cofres existentes permanecen |
| Cultivo | 9 bloques al este, bancal elevado de 3×3 con agua en el centro |
| Árboles | 10 al oeste, tres puestos separados por 6 bloques |
| Mina | Entrada 10 al norte, escalera descendente de un bloque por tramo |
| Perímetro | Cuadrado de 33×33, radio 16 |
| Accesos | Dos pasos de 2 bloques, norte y sur, reservados en todas las etapas |

El plan no derriba estructuras existentes para despejar una zona. Si el espacio no sirve, la tarea espera o busca otra tarea útil. Es un trazado inicial para superficies bastante llanas; no es un generador universal que se adapte a cualquier edificio, río, montaña o campamento muy extenso. El bancal se eleva para poder cultivar sobre pavimento sin arrancar el suelo de la ciudad.

## Oficios persistentes

Se aprovecha la profesión pasada cuando corresponde a granjero, minero, cazador o constructor; para las demás se asigna un oficio estable. Los militares mantienen utilidad de guardia, en puestos de acceso, sin convertirse automáticamente en granjeros. La rutina da prioridad al oficio en bases ya amuebladas, en lugar de mandar a todos a buscar madera y piedra sin distinción. Cuando no hay trabajo posible descansan, en lugar de la patrulla circular de base. Las órdenes explícitas de seguir/quedarse/guardar posición no se sustituyen por estos trabajos.

- **Granjero:** prepara el bancal, riega, usa azada, planta trigo y cosecha cuando el cultivo madura naturalmente. Si carece de semillas, azada y cubeta de agua, recibe una sola vez 8 semillas, 12 de tierra, una cubeta de agua y una azada de piedra. Es el equivalente vanilla de la herramienta solicitada. La cubeta vacía se conserva. El marcador del kit se guarda en el NPC y no se restablece al gastar/robar el equipo.
- **Reforestación:** zona separada, sin plantar sobre otro brote o tronco. Si un escaneo acotado no detecta árboles con hojas, puede recibir brotes de roble: máximo 4 por asentamiento cada 24000 ticks. No se generan troncos ni se fuerza el crecimiento. El suelo y el espacio tienen que permitir plantar; en pavimento necesita tierra real disponible.
- **Minero:** abre una mina en escalera, recoge los drops reales y busca mineral expuesto junto al frente. Límite inicial de 16 tramos, sin túneles interminables. Usa y desgasta un pico; puede fabricar uno de piedra con recetas e ingredientes reales. No rompe cofres, camas, bedrock ni obstáculos de superficie para abrir la entrada. Rechaza líquidos cercanos, suelo sin apoyo, chunks descargados y modificaciones no permitidas. Ante cancelaciones o mochila llena espera antes de reintentar. No garantiza encontrar minerales donde el terreno no los tenga.
- **Cazador:** busca vacas, cerdos, ovejas y gallinas adultas, sin nombre. Usa el combate existente, recoge objetos reales y vuelve al almacén. Si no hay presas realiza búsquedas acotadas por puntos próximos, sin generar animales o carne. Rechaza crías, lobos y animales nombrados. Es una selección simple; no reconoce todas las formas de propiedad de animales que puedan añadir otros mods.
- **Constructor:** emplea madera y materiales obtenidos/guardados para mejorar las defensas y reparar huecos de sus propias construcciones.
- **Guardia:** vigila uno de los accesos; la IA de combate existente sigue respondiendo a amenazas. Los miembros compatibles pueden asistir a un compañero que esté combatiendo contra quien lo hirió, sin romper las protecciones de conversación.

## Almacén, apoyo y convivencia

El asentamiento escoge uno de sus cofres reales como almacén común. Los miembros llevan excedentes y retiran suministros de su oficio estando cerca del cofre. Se mantienen los registros de sus cofres personales y se comprueba inventario/ubicación/permisos. No se copian recursos a distancia. Los comentarios de aportes se producen después de un depósito real; otro miembro cercano puede contestar con la pausa del sistema existente. Los textos están en `data/tacznpcs/work-actions86.json` dentro del JAR.

No incorpora diplomacia, rutas comerciales entre asentamientos lejanos, un gobierno ni una economía completa. El apoyo implementado es local: oficios, almacenamiento compartido, afiliación persistente y ayuda defensiva.

## Barricadas según las imágenes

1. Tierra, a una altura inicial: defensa provisional.
2. Madera y postes de tronco, elevando la barrera.
3. Piedra con refuerzos de madera.
4. Base de piedra, postes y remate de madera; después busca huecos propios para reparar.

Si falta tierra pero hay madera/piedra puede emplear el material superior disponible. No degrada piedra a madera durante las etapas intermedias. Las sustituciones solo se realizan sobre posiciones que el asentamiento registró como construidas por él y cuyo bloque sigue coincidiendo. Devuelve el bloque sustituido y consume el nuevo; cancelación de colocación restaura mundo e inventario. No completa una obra sin materiales ni sobrescribe casas o contenedores ajenos para completar el dibujo.

Los dos accesos quedan libres en todas las etapas, con portones cuando consigue/fabrica los materiales. Antes de cruzar un portón cerrado la navegación intenta acercarse a un punto del lado correcto, abrir, cruzar y cerrar cuando el paso queda despejado. La evacuación se intenta con al menos 5 amenazas detectadas y salud inferior al 50%, eligiendo una salida opuesta al conjunto detectado y probando la otra si la primera no ofrece ruta. Usa navegación normal; no teletransporta ni garantiza escapar si ambas rutas están bloqueadas. Tampoco demuestra que las defensas resistan todos los zombies especiales del modpack.

## Dormir

Se conservan las pausas y despertar de .51. Se añaden controles de `travel`, `push` e `isPushable`: durante el sueño se elimina la velocidad residual, se bloquean los empujones y no se ejecuta el desplazamiento normal. Al despertar esas llamadas vuelven a delegar al comportamiento original. No se reposiciona al NPC con teletransporte ni se cambia su modelo.

## Verificación ejecutada

- Compilación Java 21, 216 clases verificadas con ASM y comprobación de firmas de los helpers.
- 74 controles nuevos con mundo simulado: guardado/carga de comunidad, zonas, kit único, materiales, rollback, mejoras, pasos reservados, ciclo de cultivo completo de 8 plantas, consumo de agua/semillas, extracción de piedra, selección de presas, compartir un cofre y cruce/cierre/reentrada de portones.
- Se ejecutó el bytecode de los tres métodos de física añadidos sobre una base simulada: bloquea desplazamiento dormido y delega normalmente despierto.
- Pasan además las 79 comprobaciones anteriores de menús, colocación, ADS, supervivencia, puertas, sueño y feedback de trabajo.

**No se ejecutó Minecraft real ni el modpack del usuario.** Los fixtures no prueban animaciones, generación del mapa, crecimiento real de cultivos/árboles, combate completo contra presas, evacuación de un grupo real ni cierre total del perímetro de las capturas. No hay una prueba real de rendimiento con muchos asentamientos. No deben presentarse estas pruebas como validación en partida.

## Prueba en tu partida

| Prueba | Qué comprobar |
|---|---|
| Campamento existente con varios NPCs | Conserva camas/cofres; asigna oficios y nombre de comunidad en el texto de base |
| Granjero sin equipo agrícola | Un solo kit; bancal en zona exterior, agua y 8 cultivos; no regeneración del kit al gastarlo |
| Sin árboles cercanos | Brotes limitados, suelo adecuado, espacio entre árboles y crecimiento vanilla |
| Minero con pico y suelo apto | Entrada separada, descenso escalonado y material real en mochila/almacén |
| Cazador con presas adultas cerca | Combate, recogida real y depósito; sin inventar carne si no hay animales |
| Entregar tierra, madera y piedra en almacén | Defensa progresiva con entrada y salida posterior siempre transitables |
| Horda y poca salud | Intento de retirada por una salida disponible; sin atravesar paredes |
| NPC dormido | Sin arrastre fuera de la cama; despertar y volver a moverse con normalidad |

Tags del PersistentData útiles para diagnosticar: `npc87_oficio`, `npc87_comunidad`, `npc87_nombre_comunidad`, `npc87_actividad`, `npc87_granja_kit`, `npc87_evacuacion_hasta`, además de `npc78_navegacion`. El plan mundial guarda fase, construcción propia, parcelas y miembros dentro de `asentamientos87` en el guardado social existente. No cambies IDs de comunidad manualmente para forzar mezclas.

## Fuentes

Incluye helpers nuevos Settlement87, TradeWorld87, Farm87, Industry87, Fort87, Community87, CivilFeedback87 y Sleep87. Se enlazan desde Base48, CalmBrain, Navigation85 y AutoGunOperator; se amplía el JSON de frases de trabajo. `changed-files.json` detalla todas las entradas diferentes de .52. Los stubs de prueba no están incluidos en el JAR. Los scripts de compilación/pruebas requieren las dependencias y fixtures locales indicados, o adaptar el classpath a un entorno de desarrollo equivalente.
