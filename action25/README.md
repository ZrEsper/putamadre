# Global Melee v25: integrar parkour en v24

[Instalación y controles](GUIA.txt). Instalar un solo Global Melee, en clientes y servidor; protocolo `boxing25`.

Porta `Slide20` y `Parkour20` del JAR enviado, con su SHA-256 fijado en `inputs.json`. La base sigue siendo el combate acumulado de v24; no se reemplazan sus clases por las de la variante v20. Se conservan la guardia, puños, pisada, derribos y daño pesado de v24.

Las peticiones usan el payload existente: acción 2 para deslizarse y 3 para la escalada. El servidor valida movimiento real, estado del jugador, altura y colisiones. Las poses y la cámara de parkour son temporales; el movimiento de cámara de combate permanece. El gesto se activa con correr + agacharse y con una segunda pulsación de salto en el aire frente a una repisa de hasta dos bloques.

Los mapas del código recibido se sustituyen por `SideMap25`: separan entidades cliente y servidor que comparan igual por ID. Los cambios de mundo limpian solo el estado cliente y restauran la pose local; las acciones del servidor no se borran desde el cliente. Dimensiones, poses ajenas, techo y obstáculos invalidan el recorrido. Las mangas, pantalones y chaqueta siguen las partes durante el movimiento.

```sh
python action25/build.py
python action25/test.py
```

Los fixtures de `action20` se amplían en `build/` con bloques, cajas y un jugador móvil. La prueba de parkour recorre realmente la trayectoria contra esas colisiones simuladas; no sustituye la ejecución dentro de Minecraft. La prueba EMF usa el archivo 3.3.11 enviado y comprueba su hash.

Compilación, bytecode y pruebas simuladas aprobadas. Falta validar el movimiento y aspecto final en Minecraft, con los demás mods del pack. Los archivos gráficos heredados son diagnósticos geométricos de versiones anteriores y no representan una captura de parkour en el juego.
