# Combate 2.3.1 compatible con eZWeight stamina v11

Reemplazar `zomboid-global-melee-animations-2.3.1-range-plus-one.jar` por `zomboid-global-melee-animations-2.3.1-range-plus-one-ezweight-v11.jar`. Conservar el eZWeight 1.8.4-stamina-v11 adjuntado por el usuario. No instalar las dos versiones del mod de combate. Usar el reemplazo en cliente y servidor si corresponde.

La dependencia estaba fijada a `[1.8.4-stamina-v9]`, por lo que NeoForge rechazaba iniciar con v11. Se cambia exclusivamente ese requisito a `[1.8.4-stamina-v11]`; clases, animaciones, mixins y demás recursos son idénticos al original. No se afirma compatibilidad con otras versiones de eZWeight.

Se verificaron mediante ASM las seis llamadas externas del mod de combate contra las clases del JAR de eZWeight v11 recibido: `StaminaCommon.canSpend(Player,double):boolean` y `consume(Player,double):boolean`, públicas y estáticas con firmas exactas. Se verificó el TOML y que todas las demás entradas del ZIP fueran idénticas. No se ejecutó Minecraft ni se validó el combate completo en juego.

Fuentes: `python3 build.py ORIGINAL_MELEE.jar`, Python 3.11+ y SHA-256 de la base comprobado. `Verify.java` requiere Java 21 y ASM/ASM-tree para repetir la comprobación de enlaces contra ambos JAR originales. Las herramientas están disponibles en el entorno de desarrollo en horde-spawn-filter-patch/tools.
