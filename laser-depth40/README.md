# Global Melee .40: profundidad nativa del láser

El log de la prueba con Flashlight .17 y Melee .39 contiene 2462 errores GL_INVALID_OPERATION: Depth formats do not match; shaders desactivados. Flashlight recoge el error global al terminar su composición, lo que no identifica por sí solo al emisor.

En Melee .39 la selección del formato solo reconocía cuatro formatos sized de profundidad. GL_DEPTH_COMPONENT (formato sin tamaño explícito utilizado por Minecraft) y los formatos con stencil caían a glCopyTexSubImage2D con destino fijo DEPTH_COMPONENT24 y framebuffer de lectura tomado del estado activo. Ese framebuffer no tiene por qué ser el principal.

.40 conserva el formato interno original, incluidos DEPTH_COMPONENT y DEPTH_STENCIL; asigna el formato externo y tipo correctos para stencil. En OpenGL 4.3+ copia directamente la textura principal con glCopyImageSubData. En versiones anteriores lee explícitamente el framebuffer principal. Consulta glGetError inmediatamente después de la copia para atribuir futuros errores a esta operación y no dar por válida una captura fallida. La ruta Iris que reutiliza profundidad sin manos se conserva.

Solo cambia LaserLate37.class respecto al JAR .39. Se mantienen movimiento, melee, cámara y animaciones. No modifica Flashlight, Gore, Ragdollified ni paquetes de recursos. Las advertencias de textura/modelo no capturados y el error ETF de sufijo cero son problemas independientes todavía pendientes.

Validación: python laser-depth40/build.py; python laser-depth40/test.py; python laser-depth40/depth_formats_test.py. La prueba EGL reproduce GL1282 con una copia de formatos distintos y verifica el contenido copiado para siete formatos compatibles. La GPU Mesa de pruebas permite conversiones en glCopyTexSubImage2D que pueden diferir de NVIDIA; no se afirma haber reproducido la ruta completa del modpack del usuario. No se ejecutó Minecraft ni se validó visualmente en NVIDIA.

Instalación: retirar exclusivamente Global Melee v39 y poner v40, con una sola versión de Global Melee. Mantener los demás mods actuales para poder aislar el resultado. Esta corrección aborda una ruta incompatible demostrada en el código; requiere prueba en la instancia para confirmar si elimina todos los errores del log.
