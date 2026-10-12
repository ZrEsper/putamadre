# Interacciones y cámara de vehículos

Instalar conjuntamente en Minecraft 1.21.1 / NeoForge, sustituyendo las versiones anteriores:

- `Immersive-Vehicles-1.21.1-24.0.0-zomboid-mechanics.5.jar`
- `MTS-Official-Pack-1.21.1-V29-zomboid-invisible-trunk.2.jar`
- `quietly-1_0_0-cooldown-v12-vehicle-interactions.jar`

La integración isométrica usa Dungeons Iso `.19-vehicles-food` del repositorio, que conserva la selección de mochilas, comida, controles de vehículos y el recorte con CTRL. Las correcciones de cámara están en el core de MTS: su ajuste de posición y de ángulos se omite mientras está activo el modo isométrico. El modo sigue siendo opcional.

La maletera conserva su inventario y sus cajas de interacción; sus dos objetos de modelo tienen una condición de visibilidad que está desactivada por defecto. No se alteran las texturas ni los inventarios guardados.

En caja manual se desactiva el acelerador simplificado que seleccionaba neutral/primera/reversa y se bloquean seis emisores nativos de selección de marcha. El modo guardado se aplica en la actualización del motor. Los cambios se solicitan con un clic izquierdo (subir) o derecho (bajar), con la mano principal vacía; mantienen el tiempo de cambio y sonido del motor. En automático se conserva la lógica automática. El chequeador permite elegir el modo.

En isométrica, el clic derecho consulta las cajas reales de las piezas de MTS usando el rayo del cursor, limitado a cinco bloques desde los ojos del jugador, con comprobación de paredes entre el jugador y la pieza. Las solicitudes usan los paquetes nativos de MTS; mantienen sus restricciones de vehículo bloqueado, asiento ocupado y reparación. Se evita la segunda interacción que producía el lector nativo de botones del ratón.

Quietly muestra un panel para la pieza seleccionada: asiento/conducir/pasajero, maletera/almacenar, motor/kit y chequeador, u otra pieza. Respeta la tecla asignada a usar. Es un panel adicional porque los vehículos MTS usan entidades y cajas propias, no las entidades de inventario que consulta Jade. Se conserva el HUD de búsqueda y el alcance de cofres de Quietly v11. Si MTS no está instalado, el panel no se carga.

Validación: 37 comprobaciones simuladas de mecánica, selección de acción y límites de alcance; análisis ASM de 4873 métodos del core y de las clases de Quietly; prueba JVM de la corrección anterior de reparación; integridad de los tres ZIP. Comprobación de que el pack cambia únicamente la definición de la maletera y Quietly únicamente su gancho del HUD. No hay prueba dentro de Minecraft en este entorno: quedan por verificar la cámara al conducir, el clic en asientos, las marchas bajo carga y la interfaz con la colección de mods del usuario.

Reproducción (Java 21, ECJ/ASM y entradas originales indicadas en los manifiestos):

```sh
python mts-zomboid-patch/build.py CORE_ORIGINAL PACK_ORIGINAL JSON_ANALIZADOS TOOLS --core-only
python vehicle-integration-patch/build.py TOOLS QUIETLY_V11
```
