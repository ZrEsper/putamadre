# Hordes 22: limpieza gradual

Sustituye el JAR Hordes 21 por `mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.22-auto-cleanup.jar` en cliente y servidor. Mantiene intactos el multiplicador de población de 0,5, el presupuesto de hordas, la IA y las optimizaciones de rutas. No instalar dos versiones de Hordes.

## Comportamiento por defecto

- Objetos tirados: desaparecen desde los 120 segundos de edad si no hay un jugador vivo no espectador a 16 bloques. No adelanta la eliminación de objetos con nombre personalizado. No toca inventarios, cadáveres, depósitos, cofres ni objetos recientes.
- Zombis de horda: solo los marcados por este mod (`zh_spawned`) y que no sean población normal (`zh_population`). Se eliminan después de 300 segundos observados sin jugadores a 64 bloques, sin objetivo vivo cercano (96 bloques), sin memoria activa de ruido/persecución y sin ticket de horda pendiente de generar enemigos. Si vuelven a estar ocupados, se reinicia el contador.
- Se excluyen oleadas de factions (`FactionPveWave`), entidades persistentes, con nombre, montadas o que transporten pasajeros. Los zombis naturales o de otros generadores sin marca quedan intactos.
- Usa `discard`, no una muerte: la limpieza no crea cadáveres, loot ni experiencia. No disminuye contadores de kills.
- Revisa cada candidato aproximadamente cada cinco segundos, repartido por ID de entidad. Máximo ocho eliminaciones por segundo y dimensión. No recorre todas las entidades ni fuerza la carga de chunks; solo opera en entidades que Minecraft ya está actualizando. Los tickets activos se consultan como máximo una vez cada cinco segundos por dimensión.

Al primer inicio genera `config/zomboid-hordes-cleanup.properties`. Permite desactivar por separado objetos y hordas y ajustar tiempos, distancias y límite. Reiniciar el servidor tras editarlo. Los límites mínimos evitan borrar loot recién generado; la edad se mide en ticks, por lo que con TPS bajos los tiempos reales se alargan.

## Validación

`python build.py`: compila Java 21, verifica ASM, integridad ZIP y que la única entrada original modificada sea `HordeEvents.class`. Ejecuta 29 comprobaciones con dobles de API: antigüedad, distancia, objetos con nombre, zombis ordinarios/población, combate, memoria, tickets activos y vencidos, factions, persistencia, montaje, retroceso de reloj, límite por segundo, separación de revisiones, lado servidor y configuración inválida.

No hay cliente/servidor Minecraft ejecutable en este entorno: estos resultados no equivalen a una prueba en juego ni permiten prometer una mejora de FPS. El log y los perfiles privados no se publican.
