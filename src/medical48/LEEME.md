# Cadáveres, impactos y vendas — revisión 48

Minecraft 1.21.1 · NeoForge. Cierra juego y servidor antes de reemplazar los archivos.

## Instalación

Reemplaza las versiones anteriores de estos tres mods por los JAR de `mods/`, tanto en cliente como en servidor. No dejes dos versiones del mismo mod:

- Zomboid Zombie Gore **.32**.
- Ragdollified **.25**.
- TaCZ NPCs **.32**.

Conserva Corpse **.5**, First Aid **3.6.16** y TaCZ First Aid Compat **1.0.4**. Los remains requieren tu Doomsday Decoration; este ZIP no instala ni restaura el mod independiente de loot. Las recetas, inventarios y ajustes de iluminación no cambian.

## Cambios

- Al vencer la retención nativa del ragdoll de un zombi se busca un bloque de remains de Doomsday cerca del torso asentado. Se eligen modelos tumbados y orientación cardinal cercana al eje torso-cabeza. Se comprueba el volumen visual de las piezas, incluidos sus giros, los bloques vecinos, suelo firme y chunks ya cargados. No se sobrescriben paredes, contenedores ni bloques ocupados. Si no cabe ningún modelo cercano, no se fuerza la colocación. Las posiciones quedan ajustadas a la cuadrícula de Minecraft.
- Una colocación aceptada reproduce una sola salpicadura de sangre y envía la retirada del cuerpo físico. No se genera loot adicional ni se convierten cadáveres de jugadores/NPCs o cuerpos vivos/derribados. Si el mod antiguo de loot conserva sus propios cuerpos, esa gestión mantiene su ruta existente.
- Vendas con textura tejida, vuelta alrededor de la parte corporal, tamaño estable durante la curación y colocación que sigue el modelo normal o slim. La venda de cabeza queda por encima de los ojos. Se evita repetir una venda en piel y manga.
- Los impactos usan la última pose física conocida mientras llega una pose nueva. La prueba de balas incluye tejido y anatomía supervivientes; las partes totalmente destruidas y los fragmentos visuales desconectados no crean objetivos invisibles. Cabezas y extremidades de bebés respetan las escalas del renderer.
- Los cortes sobre cadáveres convierten el punto mundial de impacto al sistema local de la extremidad girada, incluyendo brazos slim y escala de bebés. Se conserva la destrucción procedural existente en cabeza, torso, brazos y piernas.
- Se retira el ragdoll anterior al crear el cadáver enlazado, se oculta el NPC muerto incluso cuando su cliente conserva temporalmente vida positiva, y se bloquea la creación reentrante del inventario del cadáver. El jugador sí puede recuperar su visibilidad al revivir.
- La malla conserva la masa conectada y elimina islas suspendidas. Si una parte queda totalmente destruida, se liberan sus uniones físicas invisibles en el hilo de física para que no sostengan otras extremidades en el aire. Los fragmentos con física y duración propia siguen existiendo.
- Algunas mordidas registradas por First Aid dejan una pérdida superficial localizada de tejido, además de la marca. La variación se decide por la ubicación registrada; se conserva al sincronizar y guardar. Desaparece al recuperar la lesión. No añade daño médico independiente ni amputaciones de jugadores.
- La ropa de la skin usa sus cortes procedurales. Las armaduras humanoides de `ModelPart` conservan UV e iluminación: los impactos leves dejan grietas oscuras y los cortes más profundos abren el material en la región dañada. No se modifica la durabilidad ni la protección. Los renderizadores externos de armadura que dibujan directamente `GeoBone`/mallas propias aún necesitan una integración específica; no se declara compatibilidad visual completa con ellos.

## Verificación y límites

Se ejecutaron pruebas de los helpers reales con mundo, registro, buffers de render y objetos del juego simulados, además del análisis de bytecode de todos los métodos de los tres JAR. Se conservaron y volvieron a ejecutar las regresiones anteriores de guardado, curación, cadáver único y balas. El informe y los hashes están incluidos.

No se ejecutó Minecraft, Jolt dentro del juego, los mixins sobre el modpack completo ni una prueba visual con shaders. Estos archivos son una compilación para probar esas integraciones; no una confirmación de que el resultado visual ya sea correcto en tu instalación. Los cadáveres que ya existían antes del parche no se reconstruyen retroactivamente.
