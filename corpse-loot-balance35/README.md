# Loot por cadáver y equipo real · .35

[Guía](GUIA.md). Parche sobre Hordes .34 y Remains .10: un solo sorteo de arma/melee y pieza militar (1/13), munición compatible condicionada al arma (35%), captura de equipo antes de procesar drops, inventarios militares íntegros y dos vendas, restos temáticos de mutantes y posiciones aleatorias incluyendo NPC después de decodificar su entrega pendiente.

Construir desde la raíz: `python corpse-loot-balance35/build.py`. Java 21 y los ECJ/ASM fijados por SHA-256 del entorno existente. Entradas comprobadas contra los informes de los parches .33/.34. El informe registra los hashes de salida y el alcance de pruebas. `expected-changes.json` limita las entradas que pueden cambiar. Las API simuladas solo se compilan para las pruebas; no se añaden a los mods.

16.221 comprobaciones de lógica, 7.800 cuerpos simulados. Sin ejecución de Minecraft: pendiente comprobar su bus de eventos real, renderizado, guardado y multijugador. El mixin militar de .34 se conserva byte por byte.

`python corpse-loot-balance35/package.py` prepara los dos reemplazos, la guía sin conexión y hashes para descarga.
