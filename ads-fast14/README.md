# Integration 1.7.14: apuntado rápido del jugador

`python ads-fast14/build.py` usa Java 21 y ASM existentes; base Integration 1.7.13 fijada por SHA. Conserva la corrección del paquete Mixin y el orden acotado de Iris. No reemplaza el JAR de TaCZ.

Dos Mixins opcionales para TaCZ 1.1.8 limitan el tiempo base de ADS a 0.05 segundos, después de aplicar los modificadores de accesorios: LocalPlayerAim.getAlphaProgress y LivingEntityAim.tickAimingProgress. En el segundo caso solo se aplica a Player; los NPC conservan sus tiempos. Los valores más rápidos se conservan. No se saltan validaciones de arma, desenfundado, recarga o paquetes. La interpolación nativa entre ticks permanece; 50 ms es el límite del parámetro base, no una garantía del tiempo total de respuesta de entrada ni de la red.

Los Mixins solo contienen Mixins; no hay auxiliares de runtime en su paquete reservado. `test.py` verifica los dos puntos nativos Math.max(FF)F contra las clases de TaCZ, analiza el bytecode y ejecuta los métodos generados con tiempos rápidos/lentos, jugadores y entidades normales. La base nativa usada para comprobar el contrato es TaCZ 1.1.8 .18 del repositorio; conserva las mismas clases de apuntado que la línea 1.1.8 objetivo. No se ejecuta el modpack completo ni se mide latencia real.
