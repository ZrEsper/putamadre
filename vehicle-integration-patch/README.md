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

## Versión de mantenimiento y conducción (.6 / pack .3)

Sustituir el core `.5` por `Immersive-Vehicles-1.21.1-24.0.0-zomboid-mechanics.6.jar` y el pack `.2` por `MTS-Official-Pack-1.21.1-V29-zomboid-resistant-maintenance.3.jar`. Conservar Quietly v12 e Iso .19. Reiniciar cliente y servidor.

El motor y la zona del capó/frontal no asociada a puertas permiten revisar el estado con la mano vacía, mediante el paquete nativo de interacción. La pantalla de 500×310 usa texto de tamaño normal, lista chasis, defensas, capó, puertas/ventanas por lado, motor, ruedas instaladas, batería y combustible. Los porcentajes de carrocería/motor usan también el daño nativo; las condiciones de las zonas de carrocería se guardan en variables de MTS y registran los nuevos impactos. El quad omite componentes cerrados. Una reparación nativa de carrocería reduce proporcionalmente el daño registrado por zonas. Los coches previamente usados no contienen historial de qué puerta recibió cada impacto anterior; esas zonas comienzan sin daño registrado mientras la condición global sí conserva el daño guardado.

El HUD de conducción es una franja digital de 340×66 colocada a cuatro unidades del borde inferior, con velocidad, RPM, marcha, combustible y estado legibles, independientes del oscurecimiento de los instrumentos originales. H abre controles: bloqueo del coche, puertas, ventanas, maletera, mantenimiento y el panel original para motor y controles propios del modelo. V cambia las luces delanteras y de posición con una pulsación; mantenerla no repite el cambio. Los controles exigen ser conductor del vehículo; las puertas y la maletera respetan el bloqueo. Los cristales laterales identificados en los modelos se ocultan al bajar las ventanas; los parabrisas se mantienen.

La respuesta del volante pasa de 2 a 3 grados por tick con el ajuste predeterminado, y su retorno de 4 a 5. Se conserva la reducción de giro a alta velocidad. Las ruedas ganan agarre, su capacidad de subir irregularidades pasa de 1.5 a 1.75 bloques y la recuperación vertical de los coches de 0.125 a 0.22 por tick. Se conservan las colisiones físicas.

Vida: carrocería y motores reciben cuatro veces la vida del pack .2 (carrocería: doce veces la original). Se reemplaza la rama que destruía directamente el coche por velocidad de impacto. Impactos de hasta 3 bloques/segundo no cobran daño; choques ligeros, 1%; a partir de 15 bloques/segundo se incrementa gradualmente hasta un máximo de 6% del máximo de vida por choque. La separación de 60 ticks evita cobrar el mismo choque una vez por cada caja del coche. El golpe frontal afecta defensas, capó y motor; el trasero afecta la zona trasera; los laterales afectan puerta y ventana del lado golpeado. El motor también tiene un límite de 6% por impacto frontal. La destrucción de estos seis modelos no genera explosión, y se retiran los efectos visuales de fuego/explosión del estado de carrocería destruida. Las balas, desgaste y otros daños siguen usando MTS.

Validación actual: 50 casos simulados, incluidos 6% máximo, 1% ligero, daño de motor, protección contra cobro por múltiples hitboxes y restauración de condición al reparar; 4909 métodos analizados con ASM; integridad de ambos JAR y comparación de recursos no modificados. No se ejecutó Minecraft: la conducción, el menú interactivo y la cámara necesitan comprobación en el juego con el modpack real.

```sh
python mts-zomboid-patch/build.py CORE_ORIGINAL PACK_ORIGINAL JSON_ANALIZADOS TOOLS --core-only
python vehicle-integration-patch/maintenance_pack.py JSON_ANALIZADOS
```
