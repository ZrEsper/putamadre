# TZP Gore 3: modelos de aparición normal

Sustituye el pack Gore 2 por `TZP-1.21.1-Gore.3-normal-spawn-model-fix.zip` y actívalo por encima de Fresh Animations y los demás packs que cambian modelos de zombis. Desactiva las versiones anteriores de TZP/Gore para evitar mezclar modelos. Conserva Gore 1.0.4, Remains 4 y Hordes 20. No cambia los JAR ni los datos de lesiones guardados.

## Causa y corrección

El generador anterior colocó animaciones de visibilidad dentro de las subpartes. El código de EMF v3.3.11, `EMFJemData.prepare`, solo recoge animaciones de las entradas superiores de `models`. Las cajas de quemadura, heridas y fragmentos tenían geometría visible por defecto y sus condiciones no se ejecutaban. No era prueba de que todos los zombis estuvieran quemándose en la lógica del juego.

Todas las animaciones de subpartes se trasladan a la entrada superior, conservando el orden de las variables y referencias. Las capas de quemadura necesitan `zg_burn` suficiente; heridas, muñones y fragmentos requieren sus flags correspondientes. Un estado nuevo de cero no activa ninguna de estas capas.

Los modelos personalizados se añaden como hijos de las partes vanilla en `EMFModelPartRoot.addVariantOfJem`. El pack anterior volvió a aplicar el desplazamiento de hombros y caderas como si los hijos fueran las partes raíz. Ahora todos los grupos usan traslado local cero: la posición y rotación del hueso vanilla sigue colocando el brazo o la pierna. Los cubos conservan las coordenadas locales de Minecraft.

Las extremidades izquierdas usan las regiones clásicas de textura (brazos 40,16; piernas 0,16) con espejo, igual que los modelos conectados del TZP proporcionado. No dependen de regiones de segunda capa de las skins que pueden estar vacías o corresponder a otros detalles.

Referencias comprobadas: [EMF v3.3.11](https://github.com/Traben-0/Entity_Model_Features/tree/v3.3.11), `src/main/java/traben/entity_model_features/models/jem_objects/EMFJemData.java` y `models/parts/EMFModelPartRoot.java`.

## Validación

1620 comprobaciones estructurales sobre los 20 modelos: animaciones solo en entradas superiores, cada capa condicional registrada, traslado local cero y UV de extremidades compatibles. Se conservan byte a byte las 3938 texturas y sonidos del pack recibido. ZIP íntegro. **No se ejecutó Minecraft ni se comprobó la apariencia mediante un render real.**

Para probar: reinicia o recarga recursos después de sustituir el pack, genera un zombi nuevo sin dañarlo y comprueba que sus brazos/piernas están unidos y que no lleva capas de heridas o carbón. Luego prueba disparos y fuego. Los zombis que ya tengan lesiones reales guardadas conservan esas lesiones.

Reconstrucción: Python 3 y Pillow, `python zombie-model-fix-patch/build.py /ruta/a/TZP_1.20.1_2.7.zip`. Texturas originales de Tissou; animaciones compactas inspiradas en FreshLX.
