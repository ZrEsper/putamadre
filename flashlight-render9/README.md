# Flashlight .9: inventario y geometría de cámara

Construcción: `python flashlight-render9/build.py`. Java 21 y herramientas ECJ/ASM existentes en hordes-melee-interiors-patch/build/tools. Base .8 fijada por SHA-256. Conserva los controles X y el bloqueo de luz duplicada de Integration 1.7.11.

Pruebas: `python flashlight-render9/test.py`, `python flashlight-render9/shader_test.py`, `python flashlight-render9/camera_test.py`. Reutiliza fixtures construidas por flashlight-render6/test.py y flashlight-render7/test.py; los shaders necesitan moderngl, numpy y EGL.

El caso de cámaras usa matrices de perspectiva con cámara trasera desplazada y proyección ortográfica isométrica. Dibuja un objeto verde delante de una pared roja y comprueba que el objeto conserva su color y la pared queda iluminada en las tres vistas. `camera_test.py --old` reproduce el pintado del objeto con la textura de la pared utilizando el shader .8. No es una ejecución de Minecraft ni una prueba visual del modpack.

El motor no vuelve a dibujar entidades o block entities. Captura el terreno con su profundidad y solo utiliza el albedo si ambas superficies coinciden. En geometría dinámica conserva el color del fotograma principal. Una exclusión cercana evita que el mapa de profundidad del propio cuerpo tape el haz en tercera/isométrica. El emisor siempre está en el jugador, independiente de la posición de la cámara.

CaptureStage9 evita la redistribución de eventos de las capas de terreno durante la captura secundaria. Descriptor verificado contra `net.neoforged.neoforge.client.ClientHooks.dispatchRenderStage(RenderType, LevelRenderer, Matrix4f, Matrix4f, int, Camera, Frustum)` en la fuente oficial NeoForge 1.21.1. Fuera de Engine6.capturing el evento sigue su curso normal. Esto evita que las integraciones de render se ejecuten por segunda vez en el pase de iluminación.

Las versiones de cámaras aportadas están identificadas en camera-inputs.json y se conservan. No se cambia su control ni sus modelos. Véase LEEME.txt para instalación y límites de validación.
