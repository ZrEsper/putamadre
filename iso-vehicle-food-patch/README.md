# Isométrica .19: controles de vehículos y alimentos

Reemplazar Dungeons Iso .17/.18 por `dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.19-vehicles-food.jar`. Se construye sobre .18 para conservar mochilas y recorte con CTRL. Minecraft 1.21.1 / NeoForge.

Cuando el jugador está montado, se evita que la cámara transforme su movimiento, sobrescriba las teclas de interacción o fuerce el apuntado/giro/ataque contextual. La detección cubre pasajeros vanilla y monturas internas de MTS. Se mantienen la cámara isométrica y su tecla de activación.

Con alimentos o bebidas en cualquiera de las manos, el botón de uso y la pulsación sostenida conservan su estado original: no se convierten en clic para desplazarse o interactuar con objetos del escenario. Esto permite que el sistema original de consumo complete su duración. Fuera de esos casos se conserva el comportamiento previo de apuntado e interacción. Se limpian los estados antiguos de apuntado/clic capturado al pasar a un consumible o montura. El hook de sesión se mantiene antes de la comprobación.

El log enviado no llega a iniciar una partida: se detiene por `ManualMouseMixin is missing an @Mixin annotation` de MTS Mechanics .1. La anotación de clase `@Mixin` exige retención CLASS (RuntimeInvisibleAnnotations), pero estaba en RuntimeVisibleAnnotations. También se entrega `Immersive-Vehicles-1.21.1-24.0.0-zomboid-mechanics.2.jar` con la retención corregida. El verificador reproduce el rechazo de .1 y acepta .2. Conservar el Official Pack Dashboard/Trunks .1 anterior; no necesita descargarse de nuevo.

Instalar ambos reemplazos en cliente y servidor, retirar sus versiones anteriores y no duplicarlos. Usar una copia del mundo para probar: arrancar, conducir con la cámara normal/isométrica, pasar de automático a manual, cambiar con mano vacía y consumir alimentos/bebidas manteniendo clic derecho hasta terminar. Las mochilas y el recorte al mantener CTRL siguen incluidos.

Verificación: once casos simulados de pasajero, alimentos/bebidas en ambas manos, objetos normales y ausencia de jugador; análisis de bytecode y comparación de las correcciones anteriores. MTS .2 conserva los treinta casos simulados de mecánica y ahora comprueba la retención de `@Mixin`. **No se ha ejecutado Minecraft en este entorno:** estas comprobaciones no sustituyen la prueba de conducción y consumo dentro del juego.

Compilar la cámara con Java 21 y las herramientas ECJ/ASM verificadas del repositorio:

```bash
python iso-vehicle-food-patch/build.py /ruta/herramientas
```

La base .18 se toma de `mods/`. Fuentes y verificación de MTS en `../mts-zomboid-patch/`. El argumento opcional `--core-only` de su constructor recompila el núcleo sin alterar el Official Pack.
