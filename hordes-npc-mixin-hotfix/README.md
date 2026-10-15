# Hordes .34 · Compatibilidad con el equipamiento de NPC .16

[Instalación y diagnóstico](GUIA.md). Corrige el fallo crítico `MilitaryBanditBalanceMixin` mostrado en el log: el selector del Redirect ahora incluye `originalOutfit33`, conservando `outfit` para NPC .15. No cambia código de juego ni relaja el requisito de inyección.

Desde la raíz: `python hordes-npc-mixin-hotfix/build.py`. Java 21, ECJ y ASM fijados por SHA-256 del entorno existente. Reproduce como prueba negativa la incompatibilidad original y comprueba las tres conexiones Hordes/NPC contra ambas versiones de NPC. Comprueba que solo cambia una entrada y que los métodos ejecutables siguen idénticos. `validation.json` registra hashes y alcance. No sustituye una ejecución del transformador Mixin dentro de Minecraft.

`python hordes-npc-mixin-hotfix/package.py` genera el paquete completo .34 usando los cuatro JAR sin cambios del paquete .33 y Hordes .34.
