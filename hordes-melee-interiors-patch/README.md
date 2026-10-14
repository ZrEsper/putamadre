# Hordes .29 y Global Melee v13

[Guía completa en lenguaje sencillo](GUIA.md): instalación, todos los controles, ejemplos de cooldowns, spawn interior y categorías de loot. El paquete de distribución incluye `GUIA.html` para abrir con doble clic.

Cambios sobre Hordes .28 y el Global Melee v12 exacto adjunto:

- Cooldowns configurables por ataque y familia, con sincronización a todos los clientes al entrar o cambiar la revisión del mundo. El canal existente usa un mensaje identificado para que recibir los ajustes no abra el panel.
- Reemplazo opcional de spawns nuevos cercanos por un lugar interior de otra habitación conectada por puertas, en la misma planta y zona. Inicio desactivado, probabilidad preparada de 20 %. No añade entidades extra; conserva el exterior si falla. No asigna objetivo ni pista artificial del jugador.
- Diez pestañas con ayuda sencilla para todos los controles. Interruptor interior permanente, diferenciado visualmente de las acciones de una sola ejecución.
- Catálogo de loot reorganizado conservando las entradas originales, cantidades y datos personalizados. Dos entradas nuevas vanilla para supervivencia. `loot-catalog.json` registra los movimientos y el catálogo completo.

99 comprobaciones ejecutables con dobles de mundo, cliente y red: habitaciones, puertas, exclusión del mismo cuarto, límites, agua/cueva/chunks descargados, restauración exterior, eliminación de objetivo artificial, cooldowns y familias, codificación y validación de sincronización, conexión nueva, envío a todos/jugadores nuevos/cambio de revisión, y limpieza de cachés. ASM BasicVerifier valida los métodos modificados. El build verifica hashes de entradas/herramientas, CRC, ausencia de duplicados, inventario de cambios y conservación de las entradas originales de loot. No incluye dobles de prueba en los mods.

**No probado en Minecraft.** La identificación de casas es geométrica y conservadora, no una consulta a identificadores de estructuras. No promete detectar cualquier construcción, arcos sin puerta ni otras plantas. Hay un intento de reemplazo por tick y mundo, búsqueda acotada, caché corta y ausencia de carga forzada de chunks. Cliente y servidor deben actualizar los dos mods; Hordes usa protocolo de panel 29. Global Melee sigue funcionando con valores originales cuando Hordes no está instalado.

`python3 build.py` con Java 21 reconstruye y valida los dos mods. Descarga ECJ/ASM desde Maven Central con hashes fijos; los dos JAR base están en `base/`. Los scripts no acceden a credenciales ni publican nada por sí solos.
