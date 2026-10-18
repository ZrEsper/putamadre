# Melee .39: reutilizar profundidad de Iris

Con shaders activos, Iris ya guarda una textura sin manos (`RenderTargets.getDepthTextureNoHand()`, depthtex2). .38 seguía creando otra copia de profundidad para el láser mediante glCopyImageSubData. .39 usa directamente la textura de Iris y retorna antes de capturar estado GPU, reservar texturas o copiar profundidad en esa ruta.

La textura se consulta cada frame y no se almacena como recurso propio: no se elimina ni redimensiona. Se manejan recargas del shaderpack, pipeline vacío o vanilla y render targets destruidos. La textura propia del fallback mantiene dimensiones de asignación independientes del viewport para evitar reutilizar un tamaño anterior tras alternar shaders. Sin Iris se mantiene el fallback .38.

Contratos consultados en el checkout Iris 1.21.1 eb7afb99f747cc8ed5ee4072119539035d33cefd: IrisRenderingPipeline.renderTargets, PipelineManager.getPipeline, RenderTargets.getDepthTextureNoHand y DepthTexture.getTextureId. MixinLevelRenderer llama a pipeline.beginHand antes del paso de manos; IrisRenderingPipeline.beginHand ejecuta copyPreHandDepth. Esa textura excluye las manos y conserva el relieve del mundo. El latest.log confirma que el usuario tenía .38, no una versión antigua.

`python laser-iris39/build.py` y `python laser-iris39/test.py`. Las pruebas de ciclo de vida consultan 1000 identificadores de profundidad recreados, conservan la propiedad de la textura propia y comprueban pipeline vacío/destruido/descargado. Pasan regresiones de ejes, colores, vegetación, cola, movimiento, cadáveres y firmas de Mixin. El JAR cambia solo LaserLate37.class respecto a .38.

No hay benchmark posterior de la instancia Minecraft. El perfil anterior identificó la ruta de copia como costosa, pero no permite asegurar un porcentaje de mejora con esta versión. El nuevo log emite `[Melee39] Using Iris pre-hand depth: no additional laser depth copy` al tomar la ruta nueva. Se conserva el láser físico y su prueba de profundidad, sin cambiar modelos o animaciones.
