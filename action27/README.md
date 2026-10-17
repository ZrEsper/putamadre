# Global Melee v27: TaCZ .19, slabs y movimiento amortiguado

[Guía e instalación](GUIA.txt). El ZIP reemplaza solamente Global Melee en clientes y servidor (`boxing27`). Se usa con el TaCZ .19 `wall-alt-slide` enviado por el usuario, sin modificar ese JAR.

## Fallos reproducidos

TaCZ .19 llama a `MovementCompat20.proneKey`, que resuelve exactamente `dev.zomboidmelee.boxing.Bodycam20.proneKey(int,int,int)`. V26 no exportaba esa clase: el enlace fallaba y C continuaba hacia la activación de tumbarse. `Bodycam20` ahora consume el intento válido de deslizamiento antes de esa rama. Reconoce tanto la tecla configurable nueva como la tecla privada de tumbarse de TaCZ mediante reflexión; quieto deja disponible tumbarse. La confirmación de deslizamiento lleva dirección, velocidad y duración del servidor para que el cliente no dependa de un caché de movimiento ya caducado.

La búsqueda de repisas de v26 solo examinaba dos coordenadas Y enteras. Al salir de una slab de altura fraccionaria podía omitir la slab superior situada dentro de los dos bloques permitidos. Ahora se recorre todo el intervalo, se usa el techo real de la forma de colisión y se permiten apoyos de media altura ya ligeramente por debajo de los pies durante el salto. Se conservan el límite relativo de dos bloques y las comprobaciones de espacio y recorrido.

## Cámara y arma

`Bodycam27` aplica resortes críticamente amortiguados por fotograma con integración exacta para un objetivo constante. Cámara y arma mantienen estados separados; los cambios de dirección y velocidad se filtran y el movimiento converge sin oscilación. La cámara de locomoción está limitada a ±0,45 grados por eje; las sacudidas de combate existentes permanecen separadas.

`BodycamVanilla27` sustituye el balanceo vanilla cuando esta cámara está activa. El gancho de TaCZ cancela su animación de cámara de nivel, conservando las animaciones de arma con un arma equipada. `BodycamWeapon27` añade una transformación exterior al wrapper del TaCZ .19 suministrado, con push/pop equilibrados y protección contra aplicación recursiva. Recarga, disparo, high-ready y poses ante paredes siguen a cargo de TaCZ. La inercia adicional del arma se reduce al apuntar usando su progreso ADS cuando está disponible.

Las entidades, posición real, puntería y guardia aceptada de tercera persona no se modifican desde la cámara.

## Reproducir

```sh
python action27/build.py
python action27/test.py
```

`inputs.json` fija los hashes de la base de combate, donante de parkour y clases de comprobación de TaCZ. `tacz19-input.json` identifica el JAR completo enviado y los hashes individuales; `base/tacz19-probes.jar` conserva únicamente las clases necesarias para las pruebas de compatibilidad, no se empaqueta en el mod.

La suite invoca el `MovementCompat20` real extraído de TaCZ .19, verifica descriptores de los mixins contra sus clases originales, ejecuta física con colisiones simuladas y comprueba resortes a 30/60/144 FPS, reducción al apuntar y retorno sin vibración. Los controles negativos prueban los dos fallos concretos contra v26. Continúan las regresiones de combate, caída, mano secundaria, servidor integrado y EMF 3.3.11.

Minecraft completo no se ha ejecutado. Las pruebas de código y compatibilidad no sustituyen la evaluación visual dentro del pack.
