# Global Melee v22

[Instalación y controles](GUIA.txt) · [Diagnóstico del cierre](CRASH.txt).

Corrige el cierre de v21 que intentaba enviar paquetes del servidor desde `LocalPlayer`. `Stomp21.tick` ahora valida lado y clase antes de leer la acción pendiente; `Net20.cue` también valida ambas condiciones antes de enviar. La prueba de regresión reproduce dos entidades cliente/servidor que comparan igual por ID. La versión original cancela la acción del servidor; esta versión la conserva y aplica un único contacto.

Retoca jab y pesado con preparación, extensión, guardia de la mano opuesta y recuperación distintas, además de más sacudida visual limitada. No altera la posición ni la rotación real del jugador. Añade gestos de alcanzar/girar la mano para puertas, trampillas, contenedores reconocidos y cadáveres; los eventos no cancelan las interacciones y respetan objetos y armas. Conserva la pisada articulada, mano secundaria, colisiones y recuperación de v21.

Reproducir desde la raíz:

```sh
python action22/build.py
python action22/test.py
```

Base v20 y herramientas fijadas por SHA-256. Se aplican de nuevo los cambios acumulados de v21 más la corrección y animaciones de v22; no es necesario instalar ambas versiones. `compile-api` contiene stubs excluidos del JAR. Los fixtures de `action20` se amplían en `build/` para simular los contratos relevantes. El protocolo `boxing22` requiere actualizar clientes y servidor.

Compilación y pruebas de código aprobadas; no hay ejecución de Minecraft ni validación visual final. Los archivos de vista previa geométrica heredados de v21 solo describen la pierna de esa animación y no son capturas del juego.
