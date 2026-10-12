# Taller de vehículos, Belroft y obstáculos

Instalar juntos, sustituyendo las versiones anteriores:

- Immersive Vehicles `.8` en lugar de `.7`.
- Official Pack `.4-workshop` en lugar de `.3-resistant-maintenance`.
- Belroft Motors `.1-workshop` en lugar del Mk13 original aportado por el usuario.
- Dungeons Iso `.21-ground-preserved` en lugar de `.20`.

Conservar Quietly v12 y SurvivorCreator v28. Reiniciar cliente y servidor, sin duplicados de los packs o del core.

El taller de 620×370 usa una silueta original por componentes, zonas de condición verdes/amarillas/rojas y dos columnas. Las ruedas, asientos y motores se consultan por su ranura y pieza instalada; una ranura vacía muestra «No instalado», en gris. Batería y combustible indican carga/nivel. Las zonas de carrocería continúan usando el daño localizado guardado por la integración; el chasis también refleja daño nativo. No se inventan piezas independientes de freno/suspensión donde el mod no las tiene. El PNG `workshop-preview.png` es una ilustración del diseño, no una captura de Minecraft.

Se corrige el capó: la inspección deja pasar su acción normal de apertura/cierre. Pulsar el motor inicia la revisión de 30 ticks y entrega XP de Mecánica al terminar. También hay botón de capó en el taller y en H; fuera del coche se exige acceso y alcance. El texto, inspección, caja manual, luces V y almacenamiento se aplican a los 20 vehículos de Belroft con motor `engine_car`; sus definiciones de barcos quedan intactas. Se conserva el orden de ranuras originales al añadir la maletera.

Física: el máximo probado por las cajas de ruedas se limita a 2.5 bloques, incluyendo el radio de la rueda; un valor grande del pack no permite superar ese límite. La elevación se suaviza de 0.22 a 0.12 por tick. Los ajustes de inclinación por apoyo avanzan como máximo 0.75 grados por cálculo y mantienen el objetivo de cabeceo en ±22° y de balanceo en ±14°. Un vehículo previamente inclinado se corrige gradualmente. No se deshabilitan las colisiones para atravesar estructuras.

Las hojas y los bloques/alfombras de musgo se consideran obstáculos blandos: se permite su rotura, su resistencia de choque es cero y no cobran daño al coche. El adoquín musgoso sigue siendo piedra, no musgo blando. Los materiales duros conservan su dureza y las condiciones nativas de rotura por energía/configuración del servidor; cobran más daño que la vegetación, con el límite anterior de 6% de vida total por choque. No se rompe piedra irrompible.

Iso .21 eleva el plano mínimo del recorte por encima del suelo del coche y conserva el nivel mínimo del jugador. La inclinación del volumen envolvente ya no hace descender el recorte bajo la carretera. Esto corrige el recorte visual; no restaura bloques que versiones anteriores hayan roto físicamente en el mundo.

Validación: 84 casos simulados del core (16 nuevos para materiales, límite de altura, inclinación, UI/ranuras y Belroft), 12 de vista/foco de Iso, análisis ASM de 4966 métodos del core y 886 de Iso. Integridad de JAR y comparación de recursos de ambos packs. Se mantienen todas las clases/modelos/texturas originales de Belroft; solo cambian definiciones de los coches y piezas de carretera seleccionados. No se ejecutó Minecraft: quedan pendientes la apariencia con su escala real de GUI, la física al conducir y el recorte con los optimizadores del usuario.

```sh
python mts-zomboid-patch/build.py CORE_ORIGINAL PACK_ORIGINAL JSON_ANALIZADOS TOOLS --core-only
python vehicle-workshop-patch/build.py BELROFT_ORIGINAL
python iso-vehicle-view-patch/build.py ISO_19 TOOLS
```
