# Flashlight .11: haz unificado, radio y sombras

Construcción: `python flashlight-render11/build.py`. Reutiliza Java 21, ECJ/ASM del repositorio y base .8 fijada por SHA. Conserva los controles y la captura de terreno con exclusión de eventos secundarios de .9/.10. No requiere configuración nueva del entorno.

Validación: `test.py`, `shader_test.py`, `camera_test.py`, `appearance_test.py`, `beam_shadow_test.py`. Las fixtures Java proceden de flashlight-render6 y flashlight-render7. Los casos OpenGL usan moderngl, numpy y EGL ya disponibles. `beam_shadow_test.py --old` debe fallar por saltos del filtro de sombras .10. No se ejecuta Minecraft.

Light6.settings multiplica la tangente de apertura por 1.25 en las cuatro variantes; el muestreo de colisión utiliza la misma nueva apertura. Los controles de inventario prueban la apertura real, alcance y batería de cada variante.

Se elimina max(cono principal, iluminación cercana). El uniforme heredado NearFill ahora selecciona la orientación y apertura externa de un único cono, sin una segunda contribución. El origen físico permanece en el jugador. Se conservan las matrices de cámara y la detección de separación para isométrica.

La PCF interpola cuatro comparaciones de profundidad con pesos bilineales y corrección del plano de la superficie. El coste por píxel disminuye respecto a las nueve muestras anteriores, sin aumentar el presupuesto CPU de 48 rayos por tick. La exclusión del emisor solo ignora geometría cercana que aparece delante del terreno capturado; no elimina paredes por proximidad.

La prueba angular ejecuta las expresiones extraídas del shader de producción; la de sombra ejecuta su función PCF con un bloqueador controlado, sin oclusión de pantalla para aislar la interpolación. Los demás casos ejecutan el shader completo y mantienen comprobaciones de paredes, sombras actuales, rango, texturas y Darkness. Véase LEEME.txt para instalación y límites.
