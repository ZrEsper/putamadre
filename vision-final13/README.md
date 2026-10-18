# NVG .13: incluir manos y arma en el filtro

`python vision-final13/build.py` compila con Java 21, ECJ/ASM existentes y base Fracture Point .12 fijada por SHA. Se conserva la caché de filtros y todos sus assets byte por byte.

La .12 procesaba el filtro en AFTER_LEVEL, antes de que Minecraft dibujase las manos de primera persona. La .13 elimina ese suscriptor y aplica el mismo PostChain en TAIL de GameRenderer.renderLevel(DeltaTracker), después del render de manos y antes de la interfaz. Se agrega client.NvgFinalPass13 al paquete Mixin ya existente; Vision12 y sus auxiliares están fuera de ese paquete. El efecto se aplica una vez, también con cámara externa. Conserva control manual de N, audio, estática, transparencia térmica, color del láser y prioridad frente al efecto de dolor.

`test.py` comprueba 2000 frames sin aplicar el filtro en la fase antigua, caché, activación, tamaño, efectos ajenos, apagado y descarga, audio y estampado de texturas. `shader_test.py` ejecuta los ocho filtros en OpenGL. `hands_test.py` introduce piel y metal en el frame completo y comprueba el tinte celeste y el contraste del filtro híbrido. Las fixtures GPU se toman de render-perf12 y las comunes de nvg-feedback10 e hybrid-mask8. No se ejecutó Minecraft ni Iris transformado por Mixin aquí; la integración visual requiere prueba en el modpack.
