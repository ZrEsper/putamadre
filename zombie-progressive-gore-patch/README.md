# Gore 1.0.5 + TZP Gore 5: lesiones progresivas

Sustituir el JAR Gore 1.0.4 por `mods/zomboid-zombie-gore-1.0.5-neoforge-1.21.1-progressive-trauma.jar` en cliente y servidor. En el cliente sustituir TZP Gore 4 por `TZP-1.21.1-Gore.5-progressive-trauma.zip` y colocarlo por encima de otros paquetes que cambien los modelos de zombis. Requiere EMF/ETF, como el paquete anterior. No instalar dos versiones de Gore ni activar dos paquetes Gore simultáneamente. Remains 4 y Hordes 20 siguen compatibles.

## Cambios

- Todo daño positivo de melee o TACZ registra inmediatamente una herida visible. El contexto del disparo acepta fuentes de daño modificadas por otros mods; antes el filtro de entidad directa podía descartar estos disparos. Si no llega el evento de daño, se comprueba la pérdida real de salud al finalizar ese impacto.
- Heridas mayores con músculo expuesto, perforaciones, cráneo y mandíbula abiertos, costillas, tejido abdominal y órganos estilizados. Se conservan las texturas originales de TZP y las poses base de Fresh. Ninguna capa quemada se activa al aparecer.
- Fractura de extremidades desde 3 puntos de daño acumulado en esa zona. Una pierna inutilizada reduce velocidad a la mitad; ambas fracturadas obligan a gatear. Perder una pierna también obliga a gatear. Los brazos inutilizados quedan colgando: uno reduce daño al 60%; ambos dejan ataques de cabeza al 45%, o cero si falta también la mandíbula.
- Pérdida de tejido de cabeza/mandíbula desde 3 puntos; brazos/piernas desde 9 (5 con escopeta o sniper); torso desde 18 (9 con escopeta o sniper). Los umbrales son por zona y no crecen con la vida máxima. No todos los golpes provocan amputación: las primeras lesiones ya son visibles.
- Triple vida máxima mediante modificador persistente e idempotente: un zombi normal pasa de 20 a 60 puntos, conservando su porcentaje de salud al aplicar el cambio. También se aplica al cargar zombis anteriores que aún no tengan el modificador. Las lesiones guardadas conservan sus bits y se sincronizan las nuevas fracturas con `zg_broken`.
- Las nuevas heridas siguen limitadas a 16 bloques de detalle; no hay búsqueda de entidades ni geometría generada por frame. Los modelos particulares de infectados especiales no están adaptados por este paquete.

## Comprobación

`python build.py`: compilación Java 21, verificación ASM de 162 métodos, 19 comprobaciones con dobles de las APIs y preservación de las entradas originales ajenas al cambio (incluida la recogida de objetos en cadáveres).

`python pack.py /ruta/TZP_1.20.1_2.7.zip`: partir del TZP conectado a Fresh original; 438 comprobaciones estructurales, 3938 imágenes/sonidos originales idénticos. Todas las lesiones tienen escala cero por defecto y control de visibilidad en las animaciones raíz que EMF lee.

`python check_animations.py`: 1159 líneas de animación y 3000 frames simulados, incluidos estados intacto, fuego sin lesión, herido, cuatro fracturas y distancia. Poses finitas y materiales ocultos cuando corresponde.

`python preview.py /ruta/TZP_1.20.1_2.7.zip`: previsualización geométrica offline; imagen intacta idéntica a la original. **No es una captura de Minecraft ni una prueba de ejecución en EMF.** El entorno no dispone de cliente de juego, por lo que la apariencia final, la compatibilidad con shaders y los FPS deben comprobarse en juego.

Prueba en juego: activar solamente Gore 5 como paquete de modelos, aparecer un zombi nuevo, golpear una zona y comprobar la primera herida; repetir sobre una pierna y después la otra, y disparar con TACZ. No convertir las simulaciones en una afirmación de validación dentro del juego.
