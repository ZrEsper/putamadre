# Vision nocturna estable y limite de impactos

Tres reemplazos basados en los JAR enviados: FirstAid 3.6.3 -> archivo 3.6.4, Fracture Point NVG .3 -> .4 y Tactical .4 -> .5. Se conserva el TaCZ principal .18, sin modificarlo. [ZIP directo](../distribuciones/Zombies-Poldo-Vision-N-200-Marcas-85s.zip). [Instalacion sencilla](GUIA.txt).

## Causa y comportamiento del filtro

El perfil Spark muestra FirstAid `PainVisualEffectsController.updateEffect` cargando su cadena despues del render de mundo, y NVG `NvgClient2.frame` recuperando la suya al inicio del siguiente frame. Ambos usan el mismo slot de postprocesado de GameRenderer. Tener dolor/morfina junto con NVG sustituia el filtro del casco cada frame, recreando PostChain y buffers.

El controlador cliente de Fracture Point publica `ownsPostEffect` cuando el tick valida gafas, visor abajo y bateria. FirstAid consulta un puente opcional con metodo resuelto una vez: mientras el casco pide el slot, llama a su propio `forceShutdown` (que solo cierra cadenas de FirstAid) y no carga su composite. El filtro original de Fracture Point se mantiene, incluido el modo termico o digital seleccionado por las gafas. Al subir el visor o agotarse la bateria FirstAid recupera su postprocesado normal. Sin Fracture Point el comportamiento medico anterior se conserva.

Solo se suspende el composite de desenfoque/desaturacion de FirstAid mientras NVG esta activo. Curaciones, salud, dolor calculado, animaciones y HUD siguen en sus clases originales. No se mezclan los dos shaders en una cadena nueva. Se mantienen las rutinas originales de registro, presets, shaders GLSL/JSON y realce termico: no se reemplaza el filtro por un simple efecto de pocion. Se conserva N manual, inicio arriba, bateria de 900 segundos activos, cambio de casco manual y recuperacion del filtro tras perdida de la cadena o recarga de recursos.

## Limite de marcas

Tactical recuerda como maximo 200 impactos en total por cliente y mundo, no 200 por pared. `BoundedMarks.orderedPut` retira el registro anterior de un impacto repetido antes de insertarlo al final; elimina inmediatamente la particula y temporizador mas antiguos al exceder el limite. El while nativo conserva tambien el limite 200. El contador de vida es 1700 ticks de mantenimiento cliente, equivalentes a 85 segundos a 20 TPS; no depende de que el chunk siga cargado. Las restauraciones mantienen la edad/deadline original y no repiten chispas ni luz. Los temporizadores se eliminan con cada expulsion. Sonidos lejanos de .4, precision, limites de miras y recursos de impactos se conservan.

## Verificacion

`python vision-impact5/build.py` fija por SHA256 los tres JAR de entrada y ECJ/ASM. `python vision-impact5/test.py` verifica:

- 937 comprobaciones de visor manual, modos, cambio de casco, sincronizacion y bateria de 15 minutos.
- 109 de los efectos nativos: restaurar marcas no repite brillo, chispas ni luz.
- 1707 de plazos, marcas restauradas, independencia de edades y eliminacion sin chunks.
- 3005 con `ImpactMarks`/constructor `Mark` reales: 1000 impactos, maximo 200 en cada insercion, particulas antiguas eliminadas, coordenada repetida pasa al final y no reaparecen marcas expulsadas/caducadas.
- Reproduccion con los metodos de FirstAid extraidos sin cambiar sus instrucciones y el `ShaderRegistry` nativo real: el original carga filtros 240 veces en 120 frames. El parche carga una vez al bajar el visor y se mantiene en mas de 9000 frames simulados, incluidos los seis modos, dano, perdida de cadena y retorno a FirstAid.
- 1000 consultas de FirstAid sin Fracture Point, sin excepciones ni perdida de su filtro.

Se usan APIs simuladas para Minecraft/renderizado; las operaciones de OpenGL se cuentan, no se ejecutan. Se verifican las clases parcheadas con ASM BasicVerifier y JVM `-Xverify:all`. Las comprobaciones no son una medicion real de FPS. Todos los recursos de shader y clases medicas ajenos al punto del conflicto permanecen identicos byte por byte. El aspecto, transicion N y rendimiento en partida deben confirmarse en Minecraft.

`python vision-impact5/package.py` genera el ZIP instalable con los tres reemplazos y checksums. Los JAR de base son entradas de reproduccion, no archivos adicionales que deban instalarse.
