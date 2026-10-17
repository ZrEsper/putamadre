# Láser v34: mirilla, distancia y Darkness

[Instalación](GUIA.txt). Reconstrucción: `python action34/build.py`, `python action34/test.py`, `python darkness-laser3/build.py`, `python darkness-laser3/test.py`, `python action34/package.py`. Las pruebas GLSL requieren moderngl con EGL.

`Sight34` captura el punto de división de BedrockAttachmentModel en el mismo fotograma; usa el hijo division_illuminated cuando pertenece a la misma ruta. Los ganchos del modelo y renderTempPart dan soporte al render estándar y un respaldo para modelos acelerados. Equipment29 establece un marco que permite ordenar el haz después de los accesorios. Los rayos salen de la vista óptica a la geometría sólida y se verifican desde el emisor, conservando la exclusión de vegetación.

La posición de impacto se proyecta con las matrices reales del mundo y se lleva al espacio de render de las manos. Su profundidad visual se limita a 8 unidades para no exceder el volumen del primer plano; se mantienen la posición en pantalla y el trazado físico hasta 256 bloques. El punto y haz tienen anchura mínima en píxeles, con un máximo que evita cuadrados enormes cerca de la cámara. No cambia el disparo ni el daño.

El shader Darkness original apaga píxeles tenues de láser; el parche `.3` omite exclusivamente su etiqueta alpha 251/255 y deja idéntica la escena normal. Prueba EGL real con caso negativo contra `.2`. Las clases Java del JAR Darkness se conservan byte por byte.

`ReticleChecks34` ejecuta el render diferido y comprueba sus vértices, no sólo el helper matemático. `Entry34` comprueba los descriptores de métodos contra la clase TaCZ aportada. Se conserva toda la batería anterior. Minecraft no se ejecutó en este entorno: no se afirma validación visual del modpack.
