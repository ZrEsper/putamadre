# Survivor Creator v26: pantalla de muerte minimalista

[Descargar JAR](../mods/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v26-minimal-death-first-person.jar).

Reemplaza tu Survivor Creator anterior en el cliente; conserva sus dependencias y deja una sola versión instalada. Se construye sobre v24, conservando música, profesiones, rasgos, spawn y protocolos.

Pantalla negra sencilla con causa de muerte, mecanismo registrado o probable, tiempo sobrevivido y zombis abatidos. A la derecha aparece una silueta pequeña de contorno, con marcas y notas por parte lesionada. Incluye heridas leves y graves; el contador representa los tipos de lesiones registrados por zona, porque los datos originales no conservan cada golpe individual. La rueda desplaza las notas cuando no caben. La silueta es una ilustración propia, no el skin del jugador.

No incluye encabezado de informe ni panel de autopsia. No atribuye hemorragia, paro cardíaco o sepsis sin evidencia del mod. Si falla el renderizado, recupera la pantalla original.

Al entrar o reaparecer, activa primera persona y desactiva la cámara isométrica. Después permite cambiarla manualmente. Esta actualización no modifica la salud ni la reanimación de First Aid.

Validación: 12 comprobaciones de clasificación y perspectiva con APIs simuladas, prueba del renderizado completo con heridas leves/graves, recorte y restauración del shader, verificación ASM e integridad ZIP. Solo cambian dos clases originales; se añaden helpers y una textura. No se ha probado dentro de Minecraft.

[Vista de la silueta](body-preview.png), generada fuera del juego.

```sh
python survivor-autopsy-patch/make_texture.py
python -B survivor-autopsy-patch/build.py /ruta/survivorcreator-v24-death-body-report.jar /ruta/herramientas
```

Requiere Pillow, Java 21, ECJ y ASM. Los dobles de prueba no se empaquetan.
