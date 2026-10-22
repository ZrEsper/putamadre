# Corrección 62 — faros unificados y brillo de personajes

Minecraft 1.21.1 / NeoForge. Reemplaza **Flashlight .27 por .28** e **Immersive Vehicles mechanics .17 por .18**. Retira las versiones anteriores de ambos; no los dupliques en mods. Conserva Belroft workshop .3, MTS Official Pack workshop .6, Darkness .7 y el resto de la instalación.

## Faros

Los faros compatibles de MTS con blendableComponents (beamLength mayor de 2 y diámetro positivo) se registran en el renderizador de Flashlight. Cuando la entrega se realiza correctamente, se omite el dibujo del cono transparente nativo del coche. Antes se ejecutaban ambos: la luz calculada de Flashlight y la geometría translúcida de MTS. Ahora el faro y la linterna comparten cálculo de luz, profundidad, sombras y composición. Se conservan el cristal, la carcasa, el brillo de la bombilla y los destellos del faro. Sin el puente de Flashlight, o si la llamada falla, se conserva el dibujo nativo del haz.

Intensidad de los faros multiplicada por 1.8; alcance multiplicado por 1.25 con un máximo de 64 bloques. El encendido y la alimentación eléctrica siguen dependiendo de MTS. No se modifican los paquetes de modelos. Se mantiene el máximo de tres focos seleccionados, con prioridad para hasta dos luces personales, y 24 rayos por tick compartidos por todos los vehículos. Con más vehículos se seleccionan los próximos dentro del límite; no se promete iluminación ilimitada de todos los coches a la vez.

Los focos de vehículos seleccionados participan también en la iluminación del personaje. Su visibilidad se guarda por foco, no por propietario: el faro usa al jugador como contexto de raycast y antes compartir esa clave con la linterna podía mezclar comprobaciones de obstrucción. Los contextos de raycast siguen limitados a bloques; esto no añade sombras físicas del modelo del coche.

## Personajes

Se corrige una ruta independiente del cambio de cupos de la entrega 61: al agotar los 24 rayos de visibilidad por tick, un movimiento de más de 0.2 bloques de la fuente podía invalidar una comprobación reciente y devolver oscuridad de golpe. Ahora se admite una comprobación previa durante menos de seis ticks con movimiento limitado a 1.5 bloques. Un raycast nuevo bloqueado prevalece sobre esa conservación temporal. Puede haber una demora breve al cambiar de obstrucción; no se elimina la comprobación contra paredes.

Se guarda un nivel adicional de luz por personaje y tick, con subida máxima de dos niveles y bajada máxima de tres. La piel, la armadura y los renders repetidos del mismo personaje comparten ese nivel durante el tick. Se conserva la luz natural si es mayor, las cachés están limitadas a 128 personajes y ocho fuentes por personaje y se limpian al cambiar de mundo.

Con Iris activo, la textura usada para responder a la luz se obtiene del color visible a resolución completa. Ya no cambia entre muestras de una captura reducida y el color final del personaje: eso podía amplificar diferencias del sombreado al iluminarlo. La corrección de franjas de la entrega 61 se mantiene para la captura de albedo sin shaders. No se modifica el shaderpack MakeUp.

## Pruebas y límites

- La versión .27 reproduce la pérdida de luz en movimiento al agotar rayos. .28 pasa 68 comprobaciones de brillo temporal, capas, presupuesto, visibilidad independiente de faro/linterna, incorporación de faros al personaje y cambio de mundo, usando los helpers de producción en un mundo simulado.
- 24 comprobaciones de selección jugador/NPC/coche, brillo aumentado, alcance aumentado y presupuesto de vehículos; geometría real de MTS para registrar los faros.
- Cinco controles del puente MTS: registro correcto sin dibujar el cono nativo, consumo único de esa entrega, conservación de componentes no compatibles y vuelta al dibujo nativo si falta el puente o falla el registro. Se simula el dibujo nativo en estas pruebas; el aviso de fallo incluido en el resultado es intencional.
- OpenGL real EGL/Mesa llvmpipe: una superficie visible fija cambia su brillo cuando varía la captura reducida en el shader .27; con la ruta de .28 la diferencia medida es cero. Se mantienen las pruebas de franjas, tercer foco, caída suave y copia de profundidad.
- Java 21, verificación ASM de 67 clases Flashlight y 873 clases MTS, con exactamente una sustitución de la llamada al renderizado del haz. Las clases de pruebas no entran en los JARs.

No se ejecutó Minecraft con el modpack completo, MakeUp bajo Iris ni el controlador NVIDIA del equipo del usuario. Las pruebas reproducen defectos concretos y validan las rutas modificadas, pero no certifican que se haya eliminado todo origen de parpadeo o toda interferencia de vehículos en las tres cámaras. No se afirma una mejora de FPS medida.

Base: entrega 61, commit 96a7b91767f7209d8f892181c77e2d4461886f53, y el MTS .17 adjunto. Las fuentes y pruebas se incluyen en un ZIP separado.
