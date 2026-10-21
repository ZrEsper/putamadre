# Optimización 57 — Gore .39 y Ragdoll .32

Para Minecraft 1.21.1 / NeoForge. Basado en los JARs Gore .38 y Ragdoll .31 adjuntados, contrastados con Steph medical55/56. Sustituye SOLO esos dos mods por los de este ZIP en los equipos donde estaban instalados. Retira sus versiones anteriores; estos dos nuevos JARs forman una pareja obligatoria.

Conserva First Aid .18, Quietly .19, Zombie Remains Loot .25, FracturePoint .15, Dungeons Iso .34 y Darkness .6. El log ya muestra Flashlight .23 y TaCZ .20; esta entrega no los reemplaza.

## Cambios

- Las mallas de piel, carne y perforaciones de armadura se reutilizan mientras coincidan parte del modelo, geometría y contenido de lesiones. Los cambios de lesión, venda, curación o modelo invalidan la entrada. Máximo cuatro estados por parte; claves de modelo débiles. Se conserva la geometría y textura de la versión adjunta.
- La caché de destrucción procedural conserva su límite de 24 estados y deja de crear listas e Integer temporales en cada acierto. Considera ambas mitades del torso y no invalida una pierna por un corte en otra parte.
- Las consultas opcionales de modelos/armaduras guardan metadatos por clase, incluidos campos inexistentes. Los valores se leen de nuevo; visibilidad y poses no quedan congeladas.
- El renderer de ragdolls no envía a dibujar cuerpos completos fuera del frustum de la cámara. Usa todas las partes y un margen conservador para armadura y rotaciones. Conserva cuerpos con pose desconocida o inválida. No cambia física, botín, daño, duración ni cantidad de entidades. Las extremidades independientes conservan su ruta original.

## Evidencia y límites

Ensayo de 200.000 aciertos de caché en Java 21: la versión adjunta asigna 25.600.000 bytes; la optimizada asigna 88 bytes. Es una medición de asignaciones temporales del ensayo, NO una reducción de RAM total ni un porcentaje de FPS.

Pasan 2.017 comprobaciones de geometría, lectura de campos, invalidación por lesión/venda/modelo, límites y frustum, además de las pruebas de caché. Se verificaron las clases de Gore mediante ASM y la clase de renderer modificada de Ragdoll. El control de cambios confirma que archivos de textura, física, balas, inventarios, revivir y el resto de los mods no se modifican.

No se ejecutó Minecraft ni OpenGL con este paquete. Falta medir FPS y frametimes con las armas y el conjunto de entidades del usuario; el log no contiene un muestreo del coste de renderizado actual. No se afirma haber resuelto todo el coste de modelos TaCZ/GeckoLib o shaders. La prueba de frustum usa una cámara simulada; debe comprobarse en primera/tercera persona e isométrica con Iris/MakeUp.

Para medir lo que quede, Spark de cliente: `/sparkc profiler start --timeout 60`, reproduce el lag mirando armas, gore y muchas entidades y conserva el enlace del resultado. El perfil debe ser del hilo Render del cliente; los TPS por sí solos no miden ese problema.

## Fuentes y reproducción

El ZIP de fuentes contiene los helpers, parche ASM y pruebas. Requiere Java 21 y ECJ/ASM/ASM Tree/ASM Analysis de `hordes-melee-interiors-patch/build/tools` del repositorio de trabajo. Ejecutar `python build.py --gore /ruta/al/Gore.38.jar --rag /ruta/al/Ragdoll.31.jar`; los hashes de entrada están en el manifiesto. Los objetos de pruebas bajo `tests` simulan APIs y NO se empaquetan dentro de los mods.
