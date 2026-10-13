# Perfil BPwWLJGPOj

Perfil del hilo Render thread, aproximadamente 260.45 segundos. TPS de un minuto 19.86; MSPT mediano 4.59, percentil95 10.14 y maximo 156.25. El servidor suele completar el tick a tiempo, aunque hay picos puntuales.

Instantanea: 1027 entidades, incluidas 754 mobdismemberment:gib, 153 objetos y 76 zombis vanilla. No es la misma carga que el perfil anterior con 6317 fragmentos o el otro con 475 zombis vanilla; las cifras no bastan para atribuir todos los cambios de coste a un parche concreto.

Categorias del hilo de renderizado, sin sumar descendientes cuando ya se conto un ancestro coincidente: Splatter 7.40%, EMF 3.31%, Quietly 1.15%, cadena PostShadersHelper de Moonlight sin muestras relevantes, borrado de framebuffers sin muestras relevantes. El muestreo de luz LevelRenderer.getLightColor representa 3.67%, parte incluido en Splatter; NO sumar esas categorias para estimar un total. Class.forName suma 3.91%, repartido entre varios llamadores. Las categorias incluyen llamadas hijas/nativas y son tiempo muestreado del hilo; no son medidas de GPU ni coste exclusivo por mod.

Splatter aparece con emitFan y muestreo de luz de las marcas de sangre. Sigue siendo candidato para limitar/cachear decales con su JAR y configuracion reales. El pack nuevo corrige modelos/variables, no parchea ese mod. El perfil aun muestra 754 gibs: si se usa el presupuesto anterior, revisar su configuracion, que este instalado en el host/servidor y el reparto entre dimensiones. El limite de 256 es POR dimension, no un total mundial. Este perfil no incluye un log de arranque con el que confirmar la instalacion/version efectiva del presupuesto.

No se ha medido un perfil posterior al cambio del pack Gore9. Conservar el recorrido y ajustes permite comparar el coste tras instalarlo.
