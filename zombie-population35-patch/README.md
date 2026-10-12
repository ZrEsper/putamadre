# Hordes 20: 35 % menos zombis

Sustituye Hordes 19 por `mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.20-minus35-percent.jar` en cliente y servidor. Conserva Gore 1.0.4 y Remains 4.

El objetivo final de población por chunk se multiplica por 0,65 después de aplicar el multiplicador de zona peligrosa y antes de descontar los zombis existentes. Esto también afecta planes que ya estuvieran guardados. El presupuesto de cantidad de los eventos de Hordes se multiplica igualmente por 0,65. Se redondea al entero más cercano: 100 pasa a 65, 20 a 13 y 10 a 7.

El cambio limita las nuevas apariciones y reposiciones; no elimina ni mata zombis que ya estén en el mundo. Los spawns independientes de otros mods y las defensas PvE de Faction Friction tienen sus propios sistemas de cantidad.

Se preservan las clases de IA, cachés, limitador de rutas y el resto del contenido de Hordes 19. Solo cambia Zones.missing y HordeRules.budget, más el helper aritmético. ZIP íntegro, ASM valida los métodos y 12 comparaciones ejecutan el presupuesto real original y parcheado en JVM separadas. No se ejecutó Minecraft ni se midió el rendimiento posterior.

Reconstrucción: `python zombie-population35-patch/build.py`, usando Java 21 y ECJ/ASM en `/workspace/zombies-only-patch/tools`.
