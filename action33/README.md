# Global Melee v33

[Instalación y cambios](GUIA.txt). `python action33/build.py`, `python action33/test.py`, `python action33/package.py`. Usa las herramientas y entradas verificadas por SHA256 de la versión anterior. Protocolo boxing33. No incluye APIs de compilación Minecraft en el JAR.

La revisión de los cuatro vídeos identifica geometría del láser cruzando la cámara. `Laser32` conserva ahora la matriz del mundo de AFTER_SKY/AFTER_LEVEL, sin sobrescribirla al entrar en EntityRenderDispatcher. La dirección se calcula desde el eje -Z real del accesorio y su matriz completa, en lugar de forzar la mira a un objetivo seleccionado que puede ser vegetación. El batch se vacía dentro de su contexto. Se recorta la geometría contra el plano cercano y se suprime el punto a menos de 0,30 unidades de profundidad.

El trazado de colisión continúa después de vegetación hasta el siguiente bloque no vegetal. Avanza hasta la salida de cada celda, sin pasos mínimos repetitivos ni recursión; máximo 128 celdas vegetales por haz y 256 bloques. Los sondeos cortos de Lean30 usan el mismo filtro. El cambio de lado necesita persistencia y la aplicación de cámara es idempotente. El roll también evita sumar su propia contribución dos veces al mismo evento.

Bodycam27 aumenta traslación, guiñada y balance lateral, con límites de ADS menores. Mantiene algo de inercia bajo la postura elevada nativa sin sumar dos veces la posición de reposo. El wrapper abarca el render del arma con sus brazos.

`StableLaserChecks33` prueba vegetación sólida con una pared detrás, cámara repetida, sondeos alternantes, conservación de la matriz mundial, recorte cercano y el eje de accesorio rotado. Se mantienen todas las regresiones anteriores. Fracture Point .9 del ZIP no cambia respecto al paquete v32.

No se ha ejecutado Minecraft. Las pruebas funcionales simuladas y de bytecode no sustituyen la revisión visual con el modpack completo.
