Adiciones opcionales sobre schema 3:

- config.conversacion_abierta: bool, defecto true.
- config.registrar_cierres: bool, defecto true.
- config.reanudar_ticks: 600–24000, defecto 12000.
- encuentro.modo_libre: bool, defecto false. Hasta cinco opciones por nodo;
  turnos_max se conserva en el archivo por compatibilidad pero no cierra ese modo.
  Un encuentro estructurado conserva el límite de cuatro opciones y sus resultados.
- requiere/condicion.entorno: objeto de booleanos. Campos permitidos: herido,
  comida_en_bolsa, llueve, noche, bajo_techo, armado. Solo sensores, no efectos.
- respuesta.variantes: hasta ocho bloques {condicion?, lineas_npc:[2–4 líneas]}.
  En modo libre se escoge un bloque válido con al menos dos líneas nuevas; después se
  prueba la línea base. No aplica efectos extra. El contenido incluido usa principalmente
  ramas de contexto para que un cambio del entorno desbloquee otra respuesta.

No se repite una línea del NPC dentro del mismo encuentro. Si no quedan frases nuevas
para un tema, el menú continúa. Una navegación puede funcionar sin otro discurso del NPC.
No vuelve a gastar objetos ni a premiar confianza al pulsar un tema agotado.
Las líneas privadas siguen pasando por las revelaciones y umbrales existentes.

Temas incluidos por voz: heridas, comida en bolsa, ánimo, apoyo, necesidad de suministros,
destino, equipo, compartir camino, nombre, origen, familia, profesión, clima, hora,
cobertura y amenazas. Los temas narrativos no son objetivos de navegación ni tareas
futuras automáticas. No implementan las etapas de necesidades y construcción.

Persistencia nueva: npc75_tema_UUID (último menú), npc75_ultimo_cierre (diagnóstico).
Las sesiones de pausa son temporales con referencia débil al NPC. No fuerzan chunks.
El NBT y SavedData sociales de .38 no cambian.
