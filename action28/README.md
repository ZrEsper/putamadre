# Global Melee v28: deslizarse bajo un bloque y referencia Bodycam

[Guía](GUIA.txt). Reemplaza únicamente Global Melee en cliente y servidor (`boxing28`); conserva TaCZ .19.

El usuario confirmó que el obstáculo significa un **hueco de un bloque de altura libre**. V27 mantenía `Pose.CROUCHING`, cuya colisión de 1,5 bloques impedía pasar. V28 usa la dimensión vanilla de 0,6 bloques de `Pose.SWIMMING`, actualiza inmediatamente pose y dimensiones en ambos lados y conserva la física de fricción y choque. Al soltar la pose forzada se comprueba el espacio disponible; permanece bajo si no cabe y se incorpora después de salir. No se usa noclip ni teletransporte.

`SlideSwim28` impide que esa elección de dimensiones añada rotaciones de natación al render del jugador mientras la postura baja pertenece al deslizamiento. `SlidePose28` sustituye la pose visual sentada por un cuerpo reclinado, bajo, con piernas extendidas y brazos de apoyo. Sombrero, ropa y pantalones acompañan las partes. Las posiciones y rotaciones anteriores se conservan como valores numéricos en un mapa débil; `SlidePoseReset28` las restaura antes de `HumanoidModel.setupAnim`, evitando contaminar la guardia o animaciones siguientes. La salida visual no levanta el cuerpo bajo un techo.

Se revisaron los tres videos enviados. El tercero se usa como referencia de la postura baja del arma y del retraso amplio y amortiguado al girar, en lugar de añadir sacudidas aleatorias. La posición baja cede al apuntar. El muestreo compartido evita que la llamada de render del arma, inmediatamente posterior a la de cámara, sustituya el giro por una segunda muestra de velocidad angular cero. Los ajustes no sustituyen los gráficos ni la animación de arma de TaCZ.

```sh
python action28/build.py
python action28/test.py
```

La nueva regresión recorre un túnel con techo real en la API simulada usando el tamaño de la pose; verifica que se para dentro sin incorporarse, sale y vuelve a altura normal, y no atraviesa un hueco de medio bloque. El control negativo reproduce el fallo de v27. Se comprueban también la pose baja, capas, restauración, acceso público de los ayudantes de mixins, muestreo común y alineación al apuntar. Se conservan las suites anteriores de combate, slabs, TaCZ .19, servidor integrado y EMF.

Los archivos `base/tacz19-probes.jar` son solo dependencias de comprobación y no se incluyen en el mod. Los hashes de entrada siguen fijados en `inputs.json` y `tacz19-input.json`.

No se ha ejecutado Minecraft completo. El resultado visual y la sensación del movimiento requieren evaluación dentro del pack; las pruebas simuladas no acreditan una coincidencia visual exacta con el video.
