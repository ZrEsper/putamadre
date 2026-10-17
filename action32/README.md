# Global Melee v32 y Fracture Point .9

[Instalación y controles](GUIA.txt). Compilación con `python action32/build.py`, regresiones con `python action32/test.py`. El parche complementario de los filtros se reproduce mediante `python nvg-laser9/build.py` y `python nvg-laser9/test.py` (requiere moderngl y EGL).

El láser usa la matriz real del accesorio, calibra coordenadas de primera/tercera persona e isométrica, traza desde la boca del accesorio al objetivo y recorta contra bloques. Extiende los picks MISS hasta 256 bloques. Sustituye ambos renderizadores nativos de longitud fija en la entrada común. Una textura opaca y alpha 251/255 conservan el color original en los ocho filtros NVG; la máscara térmica de entidades sigue reservada a 253/255. No se clasifica terreno por RGB.

`FinalPose32` se ejecuta después del tail de TaCZ de prioridad 500, restaurando deslizamiento/escalada. `ShakeCamera32` aplica el impacto después de la cámara isométrica. `Step31` recupera locomoción sin depender exclusivamente de eventos de audio; `Run29` incluye salto sin carrera. Se corrige la base lateral de Lean30, LeanPose31 y Bodycam27. `Lower32` baja también el arma que la pose wall-ready eleva.

Se mantienen los cambios de combate y parkour anteriores. No se alteró la guardia de tercera persona aceptada ni la lógica de daño del arma. Las clases de compilación Minecraft no se empaquetan.

Pruebas de bytecode y simulación, y ocho filtros reales compilados/renderizados en OpenGL. No se ejecutó Minecraft: los resultados no equivalen a validación visual con el modpack completo.
