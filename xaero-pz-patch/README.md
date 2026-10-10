# Xaero: exploración y HUD estilo Project Zomboid

NeoForge 1.21.1. Modificaciones sobre los dos JAR exactos proporcionados por el usuario.

## Instalación

Con Minecraft cerrado, sustituir ambas versiones originales de Xaero en `mods` por:

- `xaeroworldmap-neoforge-1.21.1-1.47.0-pz-exploration.jar`
- `xaerominimap-neoforge-1.21.1-26.6.0-pz-exploration.jar`

**Instalar los dos juntos, sin duplicados.** El minimapa utiliza el helper incluido en World Map. Son modificaciones de cliente; no requieren convertir el mundo ni modificar sus regiones. Mantener XaeroLib y las demás dependencias de las versiones originales.

## Cambios

- Descubrimiento en celdas de **2 × 2 bloques**, con un radio de **6 bloques** alrededor del recorrido. Cargar/ver un chunk no revela su superficie completa.
- La máscara funciona en el minimapa FBO, el modo seguro del minimapa y el mapa grande. Los saltos de más de 12 bloques no descubren el trayecto del teletransporte. Volar en creativo y el modo espectador no descubren terreno.
- Minimapa un **20 % menor**, conservando el mínimo nativo de 55; posición arriba a la derecha, margen de 8 unidades GUI. Borde beige y oliva, con zonas desconocidas oscuras.
- Hora del mundo en formato **24 horas**, debajo de las coordenadas. Debajo, la estación real de **Serene Seasons**, traducida al español. Sin ese mod dice «Sin estaciones»; no inventa estaciones por bioma o calendario. La visualización nativa de hora se suprime para evitar duplicados. Las coordenadas deben estar activadas en los indicadores de Xaero.
- Exploración independiente por mundo, dimensión y submundo de Xaero. Guardado cada 5 segundos y al finalizar la sesión/cerrar el proceso. Los archivos se guardan en `pz-exploration` dentro de la carpeta de la dimensión de Xaero.

**El mapa empieza oculto al instalar este parche**, incluso si Xaero ya tenía terreno almacenado. Conserva ese caché y tus waypoints. No transforma las texturas del terreno en un plano de calles ni cambia el radar/waypoints: estos siguen siendo los de Xaero. La máscara cubre las vistas de juego; el exportador PNG original no está modificado.

## Verificación

`build.py` comprueba los SHA-256 de los JAR originales, compila con Java 21/ECJ, verifica el bytecode modificado mediante ASM BasicVerifier y conserva todas las entradas no modificadas. Pruebas de recorrido continuo, hueco de teletransporte, coordenadas negativas, persistencia, separación de mundos/dimensiones, máscara exacta y acotada al alejar zoom, colores del modo seguro, reloj, estaciones y posicionamiento sobre la clase real `ModuleRenderContext` del JAR proporcionado.

No se ha ejecutado un cliente Minecraft en este entorno. Falta verificar dentro del modpack la mezcla visual con shaders y la estación de la versión instalada de Serene Seasons.

```sh
python build.py /ruta/xaeroworldmap-neoforge-1.21.1-1.47.0.jar /ruta/xaerominimap-neoforge-1.21.1-26.6.0.jar /ruta/tools
```

`tools` debe contener `ecj.jar`, `asm.jar`, `asm-tree.jar` y `asm-analysis.jar`.
