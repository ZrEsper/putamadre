# Escasez, bandidos y atención médica · .33

Cinco parches sobre los JAR exactos de `base/`, con fuentes, entradas fijadas por SHA-256 y verificación del bytecode. [Guía de instalación y controles](GUIA.md).

Construir: `python loot-bandit-balance-patch/build.py` desde la raíz del repositorio. Requiere Java 21 y los compiladores/ASM con hashes fijados de `hordes-melee-interiors-patch/build/tools`. Las pruebas compilan las API simuladas por separado: no se empaquetan dentro de los mods. `expected-changes.json` limita exactamente las entradas alteradas; los recursos originales se conservan salvo textos médicos/NVG.

21.845 comprobaciones de lógica aprobadas. No se ha ejecutado Minecraft. Los cambios de loot solo filtran generación nueva; se conserva la carga real de NPC y equipo real de zombis. La migración inicial restablece solo los valores de loot y los nuevos controles de bandidos, una vez por mundo.
