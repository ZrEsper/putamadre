# Selfexpression .2: modelos compartidos para ETF/EMF

El JAR .1 enviado ya tenia cache por extension de prenda. No evitaba que las 876 extensiones construyeran la misma capa `selfexpression:modelcustom_model#main` repetidamente: recorrer todas las prendas horneaba 1.879 capas. EMF 3.3.11 limita la construccion repetida de una misma capa fuera de carga y termina ignorando el modelo. Eso explica el mensaje registrado; no se trata de falta de texturas.

El parche extrae las expresiones originales de construccion a cinco fabricas, agrupadas por bytecode identico. Las extensiones comparten exclusivamente la geometria que ya era identica, como hacen los modelos de armadura comunes de Minecraft. No se agrupan geometria diferentes. Se mantienen los campos/poses que actualiza cada metodo original y sus asignaciones de miembros, incluyendo las variantes existentes. Las texturas, items, recetas, estadisticas, registros y clase del modelo original quedan identicos byte por byte.

`SharedArmorModels` conserva cinco modelos. `ArmorCacheEpoch` original invalida tanto las caches individuales como la compartida al cambiar los recursos del EntityModelSet; los modelos anteriores se reconstruyen. Cada recarga necesita solo 10 horneados (1+3+2+2+2), sin quitar la proteccion de EMF ni modificar ETF. No se desactivan modelos o texturas personalizadas globalmente.

## Instalacion

[Descarga directa del ZIP](../distribuciones/Zombies-Poldo-Selfexpression-Cache-2.zip).

1. Cierra Minecraft.
2. Quita `selfexpression-2.22a-neoforge-1.21.1-armor-cache.1.jar` de mods.
3. Copia el unico JAR nuevo del ZIP a mods. No dejes ambas versiones.
4. Conserva ETF 7.2.5 y EMF 3.3.11 enviados. En servidor con Selfexpression, sustituye tambien su JAR; no hace falta instalar ETF/EMF en el servidor.

Esto corrige la creacion repetida de modelos de Selfexpression. La competencia entre filtros FirstAid/vision nocturna es otro problema y no se modifica aqui.

## Validacion y limites

`python selfexpression-cache2/build.py` construye desde el JAR enviado, con SHA256 fijado en inputs.json y herramientas ECJ/ASM fijadas por el proyecto spas-reload18. `python selfexpression-cache2/test.py` ejecuta los 876 metodos nativos en la JVM con APIs de Minecraft simuladas, antes y despues; usa el constructor real Modelcustom_model y ArmorCacheEpoch real. Compara todas las asignaciones de miembros, varias pasadas de render, postura/adulto/bebe/montura, recarga de recursos y cambio del EntityModelSet. Reproduce el limite de construcciones de EMF con un doble de su contador.

Resultado: 10.519 comprobaciones originales y 10.522 corregidas; 876 modelos/1.879 horneados antes frente a cinco modelos/10 horneados despues. Cero cambios en los mapas de geometria de las 876 prendas. Bytecode verificado con ASM BasicVerifier y JVM `-Xverify:all`. Todos los recursos y clases ajenos a las extensiones quedan intactos.

No se ejecuto Minecraft ni renderizado real: el aspecto con ropa, armadura cosmetica, zombis y cadaveres debe comprobarse en partida. La captura Spark anterior no constituye una medicion del rendimiento de este parche. Los JAR originales en base son entradas de reproduccion, no reemplazos adicionales que deban instalarse.
