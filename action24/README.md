# Global Melee v24: encuadre de primera persona y derribos direccionales

[Instalación y controles](GUIA.txt). Reemplaza solo Global Melee en clientes y servidor; protocolo `boxing24`. Conserva Gore 1.0.10 y Hordes .49.

La primera persona tiene una postura independiente de la tercera: antebrazo y puño con pivote de muñeca, sin colocar el codo delante de la cámara. La implementación aceptada de guardia de tercera persona se conserva; `build.py` comprueba que el prefijo de `Arms23` anterior a la delegación de primera persona sea idéntico a v23.

La pisada usa un encuadre nuevo para la pierna, con la rodilla hacia el lateral y el pie plano descendiendo sobre el blanco. Se conservan la confirmación del servidor, la cadencia y los sonidos.

`Fall24` guarda en NBT la dirección de un derribo nuevo, selecciona caída frontal o de espaldas según la orientación del zombi y comunica un ángulo cuantizado a 256 pasos a sus observadores. Un evento de seguimiento envía también ese estado a jugadores que llegan más tarde. La selección de la cabeza usa la misma dirección y admite su caja fuera del torso. No hay escaneos periódicos del mundo ni una caché común de acciones de jugadores cliente/servidor. Se conserva la regresión del cierre de v21.

```sh
python action24/build.py
python action24/test.py
python action24/view_preview.py
```

Base v20 y herramientas fijadas por hash; incluye cambios acumulados anteriores. Los stubs de `compile-api` se excluyen del JAR. La prueba EMF usa el archivo 3.3.11 enviado y comprueba su hash. Las vistas previas son diagnósticos geométricos, no una ejecución de Minecraft.

Se aprobaron compilación y pruebas con API simulada y bytecode. Falta prueba visual dentro de Minecraft. Los modificadores de habilidades y armadura siguen funcionando después del cálculo de daño base del pesado.
