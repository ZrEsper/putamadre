# Entrega 64 — compat corregido y propuesta visual para revisar

## Arreglo instalable

Reemplaza **tacz-firstaid-compat 1.0.5** por **tacz-firstaid-compat 1.0.6**. Conserva First Aid .19, Survivor Creator v36 e Isometric .35 de la entrega 63. No instales dos versiones del compat.

El compat .5 exigía exactamente la cadena de versión de First Aid .17. El .6 acepta la rama 3.6 desde .17 y pone un límite antes de 3.7. La comparación con Maven VersionRange acepta .17, la .19 instalada y el identificador del borrador .20, y rechaza 3.7. Se conservan todas las clases del compat byte por byte: solo cambia neoforge.mods.toml. Se comprobaron 46 llamadas a métodos de First Aid y la existencia de cuatro métodos objetivo de los mixins contra la base de la entrega 63. No se ejecutó la carga completa de NeoForge/Mixin en Minecraft.

## Previews solicitados: primera y tercera persona

Los dos GIFs son **renders offline de revisión**, no capturas del juego ni pruebas de una sesión de Minecraft. Proponen un vendaje con preparación, aplicación, ajuste, presión y retirada; el borrador conserva el ciclo de 66 ticks y los nombres de huesos del modelo. Se elimina el estiramiento permanente de los brazos a escala 1.5. La posición de primera persona se muestra con una cámara de inspección del rig; el encuadre final dentro de Les Raisins/Minecraft necesita comprobarse en juego.

- Primera persona: geometría del modelo consumible de First Aid y keyframes del JSON candidato.
- Tercera persona: modelo de revisión con proporciones Minecraft y poses extraídas del helper Java candidato.
- Lesiones: propuesta de atlas transparente con herida irregular, mordida distinguible y gasa tejida. Se generó la propuesta de textura mediante image generation y se proyectó sobre los modelos de los previews. No es una captura del renderizador de Gore en juego.

El JAR del compat **no incluye estos cambios visuales**. Las texturas y animaciones quedan pendientes de tu revisión. Las imágenes y el código candidato están preparados para corregir el estilo que indiques antes de entregar una versión visual instalable. La textura generada está a resolución de autoría; su exportación final debe ajustarse al presupuesto de texturas del mod.

## Pruebas del borrador

Se verificaron 291 clases del JAR First Aid candidato con ASM y una sustitución de la llamada de pose. Los nombres de los huesos usados existen en el rig, el uso sigue durando 3.3 segundos y todas las escalas de brazos quedan en 1. Las curvas de poses devuelven valores finitos y sin saltos en los ocho segmentos corporales y cinco tipos de acción del helper. Eso no valida contacto exacto de manos, skins, armaduras ni otras animaciones dentro del modpack; el preview que se entrega es del vendaje.

El archivo de fuentes incluye el helper candidato, parche ASM, JSONs de animación, rig, textura propuesta, muestras de poses y script del render offline. El JAR First Aid candidato usado para analizar el borrador no se entrega como actualización instalable, a la espera de tu visto bueno.
