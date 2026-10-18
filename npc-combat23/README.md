# NPC .23: combate nocturno y prioridad de enemigo

Base .22 enviada por el usuario, SHA fijado en build.py. Compilar con `python npc-combat23/build.py`; comprobar con `python npc-combat23/test.py` desde la raíz. Reutiliza Java21/ECJ/ASM y las fixtures existentes, sin descargar dependencias.

TeamBrain.vision ignoraba el efecto NIGHT_VISION y la linterna: imponía 8 bloques de distancia en oscuridad. El nuevo guarda acepta la línea de visión nativa para roles hostiles con NVG activo o linterna encendida; las paredes siguen bloqueando. La adquisición conserva los cinco segundos de inspección y excluye creativo, espectador y miembros de la facción.

Ante un enemigo vivo, los roles hostiles limpian los flags civiles de refugio, huida y orden del inventario antes del control de armas. Merodear e investigar ruidos dejan de competir con un enfrentamiento activo. No interrumpe la conversación/inventario abierto ni permite actuar a derribados. Se mantienen disparo, aliados en la trayectoria, recarga, reserva militar infinita, radio, equipo y perfiles originales.

La llamada de NightCombat a LightEventProcedure se desvía a NpcLamp23: ya no coloca puntos de luz cuadrados mediante ese camino antiguo. Requiere Flashlight .15 para el cono cliente. Las luces de otros sistemas independientes no se alteran.

ASM verifica cinco enganches y una sustitución de llamada. Nueve pruebas ejecutan Combat23 y TeamBrain real con mundo/entidades simulados. Las regresiones de .22 (114/23/15/36/13 comprobaciones) pasan con las fixtures heredadas. No equivalen a una prueba de IA en Minecraft: falta validación del modpack real y del aspecto visual de MakeUp.
