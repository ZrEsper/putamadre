# Survivor Creator v25: informe de autopsia y primera persona al aparecer

[Descargar JAR](../mods/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v25-autopsy-first-person.jar).

Sustituye el Survivor Creator v24 del cliente; conserva sus dependencias y no instales dos versiones. Se construye sobre el JAR v24 enviado por el usuario, preservando recursos de audio, profesiones, rasgos, perfiles, spawn y protocolos. El servidor puede conservar v24: los cambios de interfaz y perspectiva son del cliente.

## Informe

Nueva pantalla con panel oscuro, encabezado de informe post mortem, figura de referencia anatómica con cabeza redondeada y detalles de rostro/costillas/articulaciones, colores de estado y marcas en las áreas graves. El atlas es una ilustración propia; no representa el skin ni la ropa del jugador.

Los hallazgos se muestran solo cuando la salud de la zona es ≤35%, hay fractura, proyectil, mordedura o sangrado activo. Laceraciones leves y contaminación aislada se omiten del listado. Los hallazgos se ordenan por salud de la zona. Vendajes y férulas se conservan como anotaciones. La rueda desplaza el listado si no cabe.

El mecanismo se deriva del agente fatal y los datos guardados por v24. Hemorragia o asfixia se describen como registradas solo si la fuente las identifica. Sangrado en zonas críticas permite un mecanismo probable de hemorragia/shock hipovolémico; heridas de proyectil permiten traumatismo penetrante probable. No se inventa un paro cardíaco: se muestra solo ante una fuente explícita. Si no hay datos suficientes, indica mecanismo no determinado. Las infecciones no se convierten automáticamente en sepsis. Este es un resumen de mecánicas del juego, no un informe clínico real.

No cambia el daño, el estado corporal ni la revivificación. Conserva los tiempos de continuación y el audio existentes. Si falla el renderer nuevo, registra el error y vuelve al informe original para mantener utilizable la pantalla.

## Perspectiva

Al detectarse un jugador local nuevo al entrar/reaparecer, cambia a primera persona y desactiva el modo isométrico si está instalado. Recupera el mouse si estaba liberado por esa cámara. Después permite cambiar de perspectiva manualmente: no fuerza primera persona en cada tick.

## Validación

12 comprobaciones de clasificación y perspectiva con APIs simuladas; ejecución de renderer completo con datos de heridas graves/leves y comprobación de filtrado, recorte y restauración de color de shader. Verificación ASM e integridad ZIP: solo cambian `DeathPanel.class` y `CreationAudio.class`; se añaden helpers y una textura. No se ejecutó Minecraft. Aspecto, APIs de blit, eventos del modpack y continuidad de la música requieren una prueba real.

[Vista de la figura](body-preview.png), generada fuera del juego.

```sh
python survivor-autopsy-patch/make_texture.py
python -B survivor-autopsy-patch/build.py /ruta/survivorcreator-v24-death-body-report.jar /ruta/herramientas
```

La textura requiere Pillow; la compilación, Java 21, ECJ y ASM. Los dobles de prueba no se empaquetan.
