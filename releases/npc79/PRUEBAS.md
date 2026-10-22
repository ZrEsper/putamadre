# Verificación .43

No hubo cliente/servidor Minecraft real disponible. Todas las pruebas en juego están pendientes.

| Caso | Verificación realizada | Prueba en Minecraft (20 TPS) |
|---|---|---|
| Última tabla consumida | Bytecode real de .42 falla al guardar pila vacía; .43 la filtra, conserva componentes y no altera la lista original | Colocar mesa y cofre con la última unidad; revisar que no aparezca Cannot encode empty ItemStack |
| Linterna y herramientas duplicadas | 1000 mantenimientos sin expulsión; reconocimiento de suministros | Observar NPC con linterna durante varios minutos: no debe tirarla por mantenimiento |
| Comer/beber | Integración del coordinador con servicio activo; consumo original heredado | Dar comida y poción de agua, esperar necesidad, comprobar gasto de una unidad |
| Una tarea a la vez | Hooks de dueño verificados; combate lo libera | /npcdebug al trabajar y al aparecer un enemigo |
| Refugio | Regresión de recursos/recetas/bloques/rutas simuladas | NPC libre sin hogar, suelo válido y árboles naturales; esperar búsqueda y obra |
| Cadáver/cofre | Adaptador ejecutado una vez con flag liberado; depósito propio excluido | Cofre no protegido y cadáver saqueable cercano; comprobar inventarios reales |
| Charla/amigos | Pareja real simulada; ya no bloqueada por depósito/hostilidad caducada; memoria/amistad persistida | Dos NPCs compatibles a menos de 12 bloques, sin peligro; escuchar desde cerca |
| Caza/retorno/depósito | Regresión de objetivo zombi, muerte, botín y depósito sin duplicación | Zombi cercano a NPC armado con base y depósito |
| Minería | Herramienta, bloque real de fixture, botín único y veto mobGriefing | Mineral de carbón/hierro expuesto junto a su hogar y pico apropiado |
| Compatibilidad | Regresiones de diálogo, seguridad, reputación, refugio y configuración | Verificar con los mods concretos del usuario |

Instalar solo un JAR de TaCZ NPCs, iniciar con copia del mundo, `/tick rate 20`,
`/gamerule mobGriefing true` si se desea construcción. `/npcdebug` mirando al NPC
muestra tarea, refugio y navegación. No borrar identidades ni relaciones para probar.
Si faltan recursos o hay claims, la espera es esperada; revisar estado de refugio/ruta.

Cambios fuente: Inventory79.java, Life79.java, Mining79.java, Events76.java,
Ambient78.java, Activity78.java, Hunt78.java, Shelter77.java, Ops74.java; Patch79.java
modifica además CalmLoot.saveBag, Tasks.trim/useful/usable, SurvivorPatch.survey/
surveyUse y las cuatro rutinas de movimiento tranquilo de TeamBrain.
El ZIP incluye fuentes, herramientas de parche y pruebas. La base es el JAR .42.
