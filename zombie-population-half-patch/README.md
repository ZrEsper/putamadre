# Hordes 21: mitad de población

Sustituir el JAR Hordes actual por `mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.21-half-population.jar` en cliente y servidor; no mantener dos versiones instaladas.

El log adjunto muestra Hordes 19, por lo que esta versión usa ese mod como base y reduce su cantidad al 50 %, sin acumular la reducción del 35 % de Hordes 20. Conserva sus optimizaciones de rutas.

Se reduce el objetivo final de población por chunk después del multiplicador de peligro y antes de descontar los zombis existentes, incluso con planes guardados. El presupuesto de eventos de hordas también se reduce a la mitad, redondeando al entero más cercano: 100 → 50; 20 → 10; 10 → 5. Para cantidades impares el redondeo puede diferir en medio zombi. No elimina zombis existentes, ni cambia generadores de otros mods o defensas PvE de factions.

Validación: compilación Java 21, verificación ASM, integridad ZIP, preservación de clases originales ajenas al cambio y 12 comparaciones del presupuesto original frente al mod modificado. No se dispone de cliente Minecraft para comprobarlo dentro del juego.
