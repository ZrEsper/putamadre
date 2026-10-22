# Tactical .49 / remains .28 / ragdoll .33 — NeoForge 1.21.1

Parche sobre los JAR exactos adjuntados: tactical.48, useful-loot.27 y view-culling.32. No sustituye Gore .43 ni Corpse .5.

## Instalación

Guarda copia del mundo y los JAR anteriores. Retira esas tres versiones y coloca las tres nuevas; no mantengas dos versiones del mismo mod. Reinicia cliente y servidor. Para decoración usa doomsday_decoration .5, cuyo JSON real se verificó durante las pruebas.

## Cambios

- Tablones de Doomsday junto a la cara EXTERIOR de ventanas. Conserva vidrio y puertas; el NPC debe encontrar ruta a un punto exterior y situarse fuera antes de colocarlos. Respeta permisos, mobGriefing, ocupación y chunks cargados. Selección conservadora: exterior abierto al cielo y más alejado del centro registrado de la base que el vidrio; no cubre patios complejos, aleros ni todas las formas de edificio.
- Cajonera woodendrawer y tablones usando recetas verificadas por RecipeManager e ingredientes del inventario. Recetas auxiliares de roble, barriles, botones y losas. No fabrica automáticamente todo el catálogo de decoración. Si faltan materiales, cede a las tareas existentes. Fuente de recetas editable en data/tacznpcs/npc-decor-recipes.json dentro del JAR.
- Un fallo al nombrar un horno no interrumpe la tarea. Si la API no permite nombre visible, conserva el nombre en metadatos.
- Las respuestas seleccionadas se envían completas, sin eliminar líneas individuales por haberlas oído antes. Conserva tokens y consecuencias. Una amenaza almacenada ya desactualizada no basta para cancelar la charla; combate, daño y amenazas visibles siguen interrumpiéndola. Al agotarse las variantes puede repetirse una respuesta completa.
- Validación estable del propietario del menú de ragdoll: si desaparece mientras está abierto, el menú queda inválido y se cierra, sin intentar leer el mapa de cadáveres de bloque que provocaba el crash.
- Clic izquierdo normal en un bloque remains no lo destruye ni elimina inventario. No se modifican los canales específicos de daño gore de ragdolls ni Corpse.
- Con snapshot de servidor, evita crear también la copia local visual al morir una entidad en singleplayer. Corrige ese camino concreto de duplicación; no demuestra que no existan otras causas.

## Verificación ejecutada

Compilación Java 21 de helpers contra las entradas reales; verificación ASM de bytecode. Pruebas JVM con fixtures: 10 controles de menú/clic izquierdo y 15 de diálogos, peligro, horno, recetas y selección geométrica exterior. Se ejecutó bytecode original de validación para reproducir IllegalStateException y comprobar la salida segura del parche. Las recetas board y woodendrawer se comprobaron contra los JSON del mod suministrado, con consumo finito.

NO se ejecutó Minecraft real ni un servidor NeoForge. No se han comprobado renderizado, navegación real por puertas/escaleras, estructuras irregulares, multijugador, loot con todos los mods, ni ausencia total de duplicados. Las pruebas con APIs simuladas no sustituyen esas comprobaciones.

## Pruebas pendientes en tu partida

| Caso | Resultado esperado |
|---|---|
| Casa con vidrio, puerta, base registrada y materiales | NPC sale por una ruta válida y coloca board fuera; no dentro ni sobre la puerta |
| Sin acceso exterior o bajo alero | Omite esa ventana sin colocar desde dentro |
| Ingredientes suficientes de cajonera | Consume receta, coloca cajonera y la usa para guardar |
| Sin materiales / mobGriefing desactivado | No inventa muebles ni fuerza modificaciones |
| Abrir ragdoll y esperar a que desaparezca | Menú se cierra, servidor no crashea |
| Golpear remains de bloque | No se elimina el cadáver ni el inventario por el golpe normal |
| Muerte en partida integrada | No aparece la copia local adicional; revisar también otros creadores de cadáver |
| Conversación con frases ya oídas y amenaza vieja | Respuesta completa; combate real aún interrumpe |

## Archivos cambiados

NPC: Base48, Authority70, Free75, Chat71 y helpers nuevos Decor82/Dialogue82; JSON de recetas nuevo. Remains: ExpandedBodies.valid, ZombieRemainsLoot.hitCorpse y helper SafeMenu82. Ragdoll: ClientDeathHandler.onLivingDeath. Fuentes y parche ASM incluidos en fuentes-y-pruebas.zip; los JAR no contienen los stubs de prueba. Los scripts requieren rutas de dependencias locales indicadas, no son un proyecto Gradle independiente.
