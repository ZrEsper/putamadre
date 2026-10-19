# Reparación de captura de modelos, capas y errores de luz

Entradas: los tres JAR subidos por el usuario, verificados por SHA256 contra los artefactos locales. Los hashes y rutas están en validation.json. El log analizado carga Gore .20, Flashlight .17 y Ragdollified gore-light .16, con shaders apagados; registra 2462 errores de profundidad y texturas/modelos no capturados.

## Cambios

Ragdollified .17:
- EntityRenderCaptureHandler delegaba la captura del modelo exclusivamente a GuardVillagersCompatibilityHelper, que solo registra modelos del namespace guardvillagers y con ese mod cargado. Ahora, además de conservar esa compatibilidad, registra el modelo real cuando es HumanoidModel. No fuerza modelos no humanoides a una anatomía humana.
- ClientMobTextureCache.cleanup vaciaba todo el caché al pasar de 200 entradas; ahora elimina solo el excedente. Mantiene el mismo límite de memoria.
- El renderer guardaba permanentemente la textura de sustitución después de un primer fallo de captura. Ahora solo guarda una textura capturada real y vuelve a consultar el caché si anteriormente no encontró ninguna.
- El adaptador original de ETF ya contiene manejo de excepciones. No se elimina ni se parchea. El error de sufijo cero en archivos de recursos es independiente.

Gore corrección .21 (versión interna 1.0.20 conservada):
- Las capas hat/jacket/sleeves/pants reciben el mismo recorte localizado que la piel, sin duplicar órganos/esqueleto dentro de la ropa. Antes, la capa exterior podía tapar el agujero de la piel por completo.
- Se vuelve a registrar el mapa de partes como máximo dos veces por segundo por modelo para recoger piezas EMF añadidas después del primer enlace. El registro anterior se hacía una sola vez.
- Las superficies sin geometría legible conservan el render nativo. No se inventan cajas para modelos desconocidos.

Flashlight .18:
- El indicador glGetError es global. Antes, un error GL anterior al dibujo de la linterna hacía que esta aplazara su siguiente captura un segundo. Se separan los errores previos, con aviso limitado, de los errores de su captura y composición, que siguen produciendo fallo y reintento. Se comprueba la captura en su propia etapa para no atribuir errores a una pasada posterior.
- No modifica shaders, intensidad, batería ni teclas. No oculta los errores propios del render de la linterna ni elimina el error que genere otro mod.

Se incluye Global Melee .40 ya publicado para evitar mantener su ruta de copia de profundidad incompatible. Es la misma versión anterior, sin más cambios.

## Instalar

Cerrar Minecraft. Reemplazar los cuatro mods incluidos: Gore .20 anterior, Ragdollified gore-light .16, Flashlight .17 y Global Melee .39 (o la misma .40). Una sola versión de cada mod. Conservar Zombie Remains Loot gore20 y Mob Dismemberment gore20 actuales: Gore mantiene versión interna 1.0.20 y los mismos contratos, por lo que no requieren cambio de dependencias. Probar con zombis nuevos: no restaura capturas o lesiones de cadáveres ya existentes.

## Validación y límites

python render-repair21/build.py
python render-repair21/test.py
python render-repair21/regression_flash.py

Pruebas de captura para humanoides fuera de Guard Villagers, exclusión de geometría no humanoide, preservación de 200 entradas al superar el límite, capas heridas sin duplicar anatomía y registro tardío de geometría. Inspección de las clases finales de Ragdollified, regresiones de superficies/UV, daño, destrucción, órganos y luces. Prueba de errores GL previos frente a propios. Regresiones de batería, inventario/X, reanimación, NPC y restauración de enlaces GPU.

No se ejecutó Minecraft ni el conjunto de mods en NVIDIA. Estas correcciones reparan las rutas indicadas; no se afirma validación visual de los vídeos ni compatibilidad de cualquier modelo no humanoide. Los shaders de iluminación y el diseño del esqueleto no se han rediseñado en este parche.
