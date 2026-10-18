# NVG .10: interferencia y respuesta del visor

Base: JAR .9 aportado por el usuario y fijado por SHA-256. Construcción: `python nvg-feedback10/build.py`. Requiere Java 21, ECJ/ASM en hordes-melee-interiors-patch/build/tools y los API de compilación construidos por hybrid-mask8/build.py. No incorpora esos API en el JAR.

`python nvg-feedback10/test.py` reutiliza las fixtures construidas por hybrid-mask8/test.py y añade dobles propias de sonido, estado del visor y RenderType. Ejecuta el método nativo parcheado NvgClient2.key, no solamente un helper. El sonido se solicita después del envío aceptado del paquete de animación y se confirma sin duplicarlo en Tactical7.tick. La animación, el control del filtro manual y el servidor no se cambian.

`python nvg-feedback10/shader_test.py` utiliza moderngl, numpy y EGL para compilar/dibujar los ocho filtros con la configuración de uniformes de sus programas. Compara cada filtro con/sin interferencia común y verifica contraste de las texturas bajo el tinte rojo. Comprueba también que los píxeles del láser conservan su RGB exacto.

El tipo de render térmico conserva la textura extraída de CompositeState.textureState.cutoutTexture y utiliza cutout para mantener el marcado de entidad alpha 253 sin el borrado de su textura. Los tipos personalizados sin textura accesible se conservan sin sustituirlos por blanco. No se altera el alcance del marcador ni se marca el terreno por su color.

No se afirma ejecución de Minecraft, Sodium ni del modpack completo.
