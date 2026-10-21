# Corrección 60 — la linterna junto a vehículos y la captura que deja de funcionar

Minecraft 1.21.1 / NeoForge. Reemplaza SOLO Flashlight .25 por .26. Retira .25 de mods. Conserva Immersive Vehicles mechanics .17, Darkness .7, Gore .41, Zombie Remains Loot .26, Quietly .20 y Ragdoll .32 de la entrega 59, junto con tus paquetes de vehículos.

## Correcciones

La selección anterior conservaba la primera luz nativa al reservar un foco para el coche. El orden nativo podía poner un NPC delante del jugador: al aparecer el faro se expulsaba la linterna propia. Ahora el jugador tiene prioridad explícita, después entra el faro y las luces restantes rellenan plazas libres. El máximo sigue siendo dos focos.

El brillo de cada plaza se toma del propietario seleccionado, no de su antiguo índice. Se conserva el fundido de encendido/apagado y la corrección de cercanía de cámara para la linterna del jugador; solamente los faros omiten esa inclinación. Esto evita intercambiar el brillo o la postura de un NPC, un coche y la linterna propia.

El nuevo log contiene GL_INVALID_OPERATION 1282: «Depth formats do not match». Después de ese error, Capture53 bloqueaba todos los intentos mientras mundo y resolución siguieran iguales. Además, la caché de formato de profundidad podía seguir validando un identificador de textura cuyo almacenamiento había sido recreado por resize.

Ahora toda recreación de la textura de profundidad en Engine6 invalida esa caché, aunque OpenGL reutilice el mismo identificador. Los fotogramas estables siguen reutilizando la textura. Si falla una copia de escena, se permite un intento de recuperación por segundo con el estado de profundidad invalidado; no se reutiliza una captura fallida. No requiere cambiar de mundo para reintentarlo. Un problema persistente de otro renderizador puede seguir impidiendo la captura, pero no provoca reintentos a cada fotograma ni una desactivación permanente de esta ruta.

No se cambia la carga de las baterías ni se fuerzan luces apagadas a encenderse. Se conservan la corrección de matrices isométricas, la preservación de haces de Darkness, la copia GPU reducida y el presupuesto de rayos/focos.

## Pruebas

- Se reproduce con Flashlight .25 la expulsión de la linterna del jugador cuando el NPC ocupa la primera plaza y aparece un faro. La nueva versión pasa 9 controles de prioridad, brillo por propietario, cercanía, apagado del faro y presupuesto.
- Se reproduce en el helper .25 el error de profundidad cuando se reutiliza un identificador con almacenamiento diferente. El helper nuevo conserva el formato correcto y no reasigna texturas en fotogramas estables. Pasan la espera de un segundo, la repetición acotada y la salida del estado bloqueado.
- OpenGL real con Mesa llvmpipe: copia a media resolución con D24, D32F, D24S8 y D32FS8. Recrear el mismo identificador con D24 frente a una fuente D32F reproduce GL_INVALID_OPERATION; restaurar el formato coincidente recupera la copia y conserva la profundidad esperada.
- Java 21 y verificación ASM de 66 clases, con una ruta de invalidación de almacenamiento y una de recuperación.

Los ensayos de selección/reintento usan eventos, mundo y GL simulados con los helpers de producción; el ensayo GPU realiza las operaciones de framebuffer descritas sobre un contexto real separado. No se ejecutó tu modpack, ni el controlador NVIDIA de tu equipo, ni todas las cámaras dentro de Minecraft. No se afirma una mejora de FPS medida.

## Fuentes

Incluye helpers, parche ASM y pruebas. Java 21, ECJ/ASM del entorno del repositorio y EGL/Mesa para el test GPU. Base: Flashlight .25 de la entrega 59, commit ec632750923393e4c073280766a49243b5a89bf4. El perfil Spark vuelto a adjuntar es el mismo fichero que en la entrega anterior; no es una medición posterior a esta corrección.
