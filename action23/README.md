# Global Melee v23: guardia con codos y pesado x2

[Guía de instalación](GUIA.txt). Reemplaza únicamente Global Melee en clientes y servidor; protocolo `boxing23`. Conserva Gore 1.0.10 y Hordes .49.

La captura mostraba brazos rectos y cruzados a la altura del pecho. Ahora la piel y las mangas se dibujan como dos segmentos con codo, sin cambiar el tipo de los campos de `HumanoidModel` ni reemplazar sus instancias. Se respetan visibilidad, color, overlay, partes hijas, skins slim, la mano secundaria y el cambio a armas. Un hook opcional cubre `EMFModelPart.render`, que omite el render base de Minecraft en Entity Model Features 3.3.11.

Primera persona usa las mismas articulaciones directamente. La pisada usa una cadena cinemática con pie plano y la inclinación visual de la cámara. El pesado usa dos veces el atributo de ataque normal del jugador, manteniendo el procesamiento posterior de habilidades y armadura. Las cadencias no cambian.

```sh
python action23/build.py
python action23/test.py
python action23/preview.py
python action23/guard_preview.py
```

Base v20 y herramientas fijadas por hash; cambios acumulados de v21/v22 incluidos. Las clases de compilación no entran en el JAR. Los fixtures anteriores se amplían dentro de `build/`; la nueva prueba de articulación reemplaza la expectativa antigua de llamadas a `PlayerRenderer.renderRightHand` porque ya no se utiliza ese renderer para el puño. Se conserva la regresión de campos GeoBone y el control negativo del cierre de v21.

La prueba de compatibilidad busca el EMF 3.3.11 enviado en `/workspace/attachments` y verifica su SHA-256 (`emf-input.json`). Inspecciona el método real que debe interceptarse, no ejecuta Minecraft ni demuestra por sí sola el resultado visual.

Las imágenes de `assets` son diagnósticos matemáticos de las articulaciones, no capturas del juego. Falta validación visual en Minecraft y con los modelos de armadura del pack; no se ha articulado automáticamente cada modelo de armadura externo.
