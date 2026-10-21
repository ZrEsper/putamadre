# Corrección 56 — luz visible y arma baja contra paredes

Reemplaza solamente estos DOS mods, retirando sus versiones anteriores:

- Flashlight .23 sustituye Flashlight .22.
- TaCZ .20-wall-low-ready sustituye TaCZ .19-wall-alt-slide.

Conserva los otros mods de la entrega 55, FracturePoint .15, Iris y TP Shooting shoulder-clearance .4. No instales versiones duplicadas. Mantén TaCZ actualizado en los equipos donde ya estaba instalado; no añadas mods exclusivos de renderizado a un servidor dedicado. Conserva una copia del mundo antes de probar.

## Cambios

Luz: recupera más señal de color en superficies oscuras, aumenta la contribución del haz y amplía su cobertura isométrica. La dirección de la linterna del jugador en isométrica conserva su rumbo horizontal y apunta ligeramente al suelo. La atenuación por niebla en esa vista usa la distancia al foco, evitando que la separación de la cámara anule el haz. Mantiene la oclusión por bloques y la mezcla suave de la entrega anterior. No ilumina píxeles de cielo/vacío ni recupera texturas que el shader haya convertido en negro absoluto.

Arma: la obstrucción contra paredes usa una postura baja en primera y tercera persona. Tiene prioridad sobre ALT mientras exista obstrucción. ALT y las posturas de preparación conservan su comportamiento fuera de una pared. No cambia el daño de las armas ni el inventario.

Optimización: el perfil XdWEsZS4Gt muestra unos 326 segundos de muestreo del hilo Render, con glCopyTexSubImage2D en aproximadamente el 17,6 % de las muestras exclusivas; dos rutas principales pertenecen a Capture53.capture. Las estadísticas del servidor estaban cerca de 20 TPS, con media de tick de unos 3,65 ms.

La captura de la linterna sustituye dos copias de textura completas por un blit de color y profundidad hacia una captura auxiliar con la mitad de anchura y altura: un cuarto de píxeles. La copia de color usada para componer el resultado también utiliza blit. La escena final conserva su resolución completa. La profundidad auxiliar adopta el formato de la fuente para evitar errores de compatibilidad en la transferencia; se reutiliza mientras formato y tamaño sigan iguales.

## Validación y límites

Java 21 y verificación ASM sobre ambos JARs. Pruebas de postura baja/ALT/prioridad de pared y configuración, dirección finita hacia el suelo, gestión de formatos y reutilización de la captura. OpenGL real con Mesa llvmpipe: transferencias a media resolución con D24, D32F, D24S8 y D32FS8; valores de color/profundidad correctos. Compilación/enlace del fragment shader y dibujo sobre un plano controlado con atenuación suave. Se conserva la anotación corregida de AfterWorld53.

No se ejecutó tu modpack completo. No hay medición de FPS posterior al cambio ni garantía de una mejora porcentual concreta. Debe comprobarse el aspecto de luz y arma con Iris/MakeUp y tus modelos. La captura auxiliar reducida puede suavizar detalles pequeños en los bordes. El mapa de oclusión de bloques sigue teniendo resolución y presupuesto de rayos limitados. La captura espera una textura de profundidad: si un pipeline distinto usa otro tipo de adjunto, se registra el error y se detiene ese intento para evitar reintentos que parpadeen.
