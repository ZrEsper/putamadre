# Gore .23 y Ragdollified .19: tejido visible y empuje reducido

Problemas observados en los videos: heridas que dejan huecos sin tejido visible, piezas que desaparecen sin restos, sonido húmedo ausente y empuje excesivo con distintas armas.

## Cambios

- El agujero de la piel ya no se aplica también a músculos, órganos y huesos. La piel conserva su recorte y UV; el tejido interior queda visible detrás. Las etapas de daño siguen retirando sucesivamente tejido y hueso hasta la destrucción completa.
- Los modelos heridos con visibilidad desactivada por animaciones EMF pueden dibujar la herida nativa y su anatomía. Se restaura la visibilidad anterior después del dibujo; las partes completamente destruidas siguen desapareciendo.
- Shards13.burst había quedado desactivado. Ahora emite fragmentos pequeños de carne, cerebro y hueso según zona y etapa, compartiendo la física de órganos existente: gravedad, colisión y rebote. Se conservan un máximo de 24 piezas visuales; las nuevas sustituyen a las más antiguas. No son entidades de servidor ni objetos recogibles.
- Las heridas letales que pasan directamente al cadáver también emiten restos, sin depender de que el zombi reciba otro tick vivo. No se repiten en cada frame del render.
- Ojos y órganos detectan etapas >=3; antes solo detectaban exactamente 3, de modo que los saltos de daño podían suprimir la caída.
- Sonido húmedo inmediato en el impacto, con límite de repetición por perdigones. El sonido arterial del cliente deja de esperar 30 ticks al empezar una lesión.
- Las ráfagas de perdigones no pueden saltarse más de dos etapas visuales en cuatro ticks. Se conserva el daño a la salud y el daño acumulado; las siguientes ráfagas pueden continuar destruyendo el cuerpo.
- El empuje añadido por Gore a zombis vivos se reduce al 10% del anterior. Los impulsos de golpe de Ragdollified se reducen al 12%, con un presupuesto por cuerpo durante 200 ms que limita la suma de perdigones y un límite vertical. No modifica gravedad ni posición de los cuerpos.

## Instalación

Cerrar Minecraft y reemplazar solamente:
- Gore layer-capture-fix.21 por visible-tissue-debris.23.
- Ragdollified module-capture-fix.18 por low-impact.19.

Una versión de cada mod. Mantener Flashlight gl-error-isolation.18, Global Melee v40, Zombie Remains Loot gore20 y Mob Dismemberment gore20. Gore conserva su versión interna 1.0.20 para las dependencias actuales. Probar zombis nuevos: no reconstruye tejido previamente destruido.

## Pruebas y límites

python gore-impact23/build.py
python gore-impact23/test.py

Se prueba la entrada real Shards13.burst, emisión y repetición del sonido, fragmentos físicos, primera herida letal en cadáver y deduplicación entre frames, apertura de piel con anatomía detrás, progresión de perdigones, impulso con el Vector3f real de Ragdollified, límite agregado de impulsos, memoria, destrucción total, superficies y UV, balística, órganos y resolución real de módulos JVM. No se han ejecutado Minecraft ni el modpack en NVIDIA: la comprobación visual sigue pendiente.

El error OpenGL de formatos de profundidad continúa presente en el último registro. Este paquete no modifica la linterna ni el láser y no afirma resolver ese problema independiente.
