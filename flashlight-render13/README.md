# Linterna .13: apertura y sombras visibles actuales

Construir con `python flashlight-render13/build.py`; Java 21 y ECJ/ASM existentes. La captura aislada de Iris y restauración de GL de .12 se conservan. No hay configuración nueva del entorno.

Las cuatro variantes pasan de un multiplicador de tangente de 1.25 a 1.55: radio del cono 24 % mayor a igual distancia. Conservan alcance, baterías y un solo cono. El radio de trazado de colisiones usa la misma apertura que el shader.

El mapa CPU de 12×12 se completa por partes dentro de un presupuesto de 48 rayos por tick. Su proyección atrasada sobre la escena visible provocaba sombras cuadriculadas y cambios por lotes. Ahora, si el segmento de luz está en pantalla, su oclusión se resuelve con la profundidad del frame actual: ocho pasos con dos muestras suaves, sesgo según distancia y espesor limitado para no convertir geometría de primer plano lejana en una sombra falsa. El mapa CPU queda de respaldo para tramos fuera de pantalla; la transición depende de una cobertura continua. No se añaden historiales de imágenes ni entidades renderizadas dos veces.

Pruebas: `test.py`, `iris_test.py`, `mrt_test.py`, `shader_test.py`, `camera_test.py`, `appearance_test.py`, `beam_shadow_test.py`, `temporal_test.py`. La prueba temporal usa el shader completo en OpenGL: una trayectoria móvil de 24 frames y cambios del mapa atrasado reproduce diferencias de 0.484 en .12 y 0.0 en .13 en la zona visible examinada. No es una medida de FPS. La prueba de oclusión conserva un bloqueador visible real y un caso fuera de pantalla; no trata un mapa atrasado contradictorio como evidencia de un obstáculo visible.

Estas son pruebas de fixtures y de GLSL por EGL; no ejecutan el modpack ni shaderpacks reales. Las sombras de pantalla tienen limitaciones con geometría oculta y muy fina; el respaldo de colisiones sigue siendo de baja resolución fuera de pantalla. Hay que validar el aspecto en primera, tercera e isométrica dentro del juego.
