Comprobaciones automáticas realizadas, NO ejecutadas dentro de Minecraft:
- 163 clases ASM BasicVerifier; dos enganches exactos de diagnóstico de cierre.
- 277 controles de arbitraje, disparo real trasplantado y rupturas.
- 15126 controles de identidad, privacidad, dos actores de emboscada y chat.
- 124 controles de memoria, medidores, robo finito, persistencia simulada y comandos.
- 2249 controles de charla abierta: ocho voces, más de cien elecciones consecutivas
  por voz sin reinicio/cierre, ausencia de frases repetidas, sensores de contexto,
  revelaciones privadas, donación real en inventario simulado, pausas, cambio de token,
  bloqueo de reanudación insegura y motivos específicos de cierre.
- 12 casos de configuración en JVMs independientes: creación, relectura, originales
  anteriores, .38, personalizaciones, schema 1/2/3 y respaldo de archivo inválido.
- JVM -Xverify:all; Brigadier real. Las APIs del mundo, entidades y cliente son simuladas.

No se comprobó Minecraft real, NeoForge, GPU, red cliente/servidor, distancia real de
interacción, sensores de bioma/clima/cielo del motor, terceros que cierren la charla,
guardado real/chunks ni rendimiento del modpack. El log recibido era de .38.
No se ha identificado en vivo la causa exacta de su cierre anticipado: .39 añade el
motivo al log y al aviso para distinguir peligro, arma, distancia, plazo y cierre externo.

Tabla de prueba en Minecraft — PENDIENTE EN TODOS LOS CASOS:

| Caso | Pasos | Qué debe pasar |
|---|---|---|
| Actualización | Reemplazar .38 en ambos lados; copia del mundo/config | .39 carga; original se respalda como .pre39; identidad/memoria intactas |
| Charla normal | Acercarse desarmado a neutral/pasivo | Menú con cinco áreas; no encuentro mínimo genérico de dos respuestas |
| Continuidad | Elegir más de diez preguntas y volver entre menús | No termina por número de turnos |
| Tema agotado | Repetir una pregunta sin cambios | Marca ya tratado; no repite discurso ni termina la charla |
| Volver al menú | Entrar y salir de un tema ya visto repetidamente | Siempre permite navegar, incluso sin frases nuevas |
| Ocho voces | /npcexample seco libre_seco y equivalentes de las ocho voces | Estilo propio y biblioteca completa para cada voz |
| Heridas | Preguntar salud, herir al NPC fuera de la charla y volver | Respuesta cambia con su salud real; daño durante charla la interrumpe |
| Suministros | Preguntar por comida antes/después de una donación | Bolsa vacía y bolsa con comida dan respuestas distintas |
| Clima | Preguntar con y sin precipitación en la posición del NPC | Selecciona la rama correspondiente |
| Hora | Preguntar de día y de noche | Respuesta según hora del mundo |
| Techo | Preguntar bajo techo y con cielo expuesto | Cambia la respuesta sin prometer que el lugar sea seguro |
| Privacidad | Preguntar origen/familia/profesión con confianza baja y luego media | Reserva detalles primero; después revela datos persistentes |
| Nombre | Presentarse | Revela el nombre aleatorio y lo muestra en el chat |
| Ayuda | Ofrecer una unidad de comida o medicina | Transfiere unidad real; guarda recuerdo, sin curación inventada |
| Inventario vacío | Intentar ofrecer sin tener ese recurso | Opción no disponible; no crea objetos |
| Pausa | Dejar pasar 30 s desde las opciones | Aviso de pausa, opciones anuladas y NPC vuelve a IA nativa |
| Retomar | /npcreply cerca, visible, desarmado y sin peligro | Vuelve al mismo tema con token nuevo |
| Token antiguo | Pulsar una opción de antes de la pausa | No ejecuta efectos |
| Retomar con peligro | Reanudar con amenaza cerca | No pausa defensa ni fuerza conversación |
| Retomar armado | /npcreply sosteniendo arma | No reanuda charla normal; interacción manual puede iniciar robo |
| Carrera | Correr en una charla | Cierre por carrera visible y reason=sprint en log |
| Arma/ADS | Sacar arma en charla normal o ADS durante intimidación | Cierre por arma, manteniendo seguridad anterior |
| Distancia | Alejarse fuera del radio | Aviso de distancia y reason=distance |
| Amenaza | Acercar una amenaza real | Cierre con aviso de peligro y reason=danger |
| Diagnóstico | /npcdebug después de un cierre | Muestra ultimo_cierre; log incluye nodo, ticks y plazos |
| Despedida | Elegir Seguimos en otro momento | Cierra realmente, sin dejar una pausa reanudable |
| Configuración | /npcreload y /npcvalidate | Valida nuevas secciones; reload anula pausas/tokens sin borrar memoria |
| Compatibilidad | conversacion_abierta=false y recargar | Regresa a selección de encuentros anterior |
| Emboscada/robo | Probar ejemplos estructurados anteriores | Conservan actores, efectos, consecuencias y límites propios |
| Reinicio | Guardar/salir y volver al NPC | Conserva identidad, memoria y último menú; comienza nueva sesión textual |

Siguiente etapa acordada: necesidades y rutinas individuales. Construcción, perímetros
y sociedades aún no están implementados en esta entrega. El contenido es finito y no
se presenta como garantía de horas de diálogo único por NPC.
