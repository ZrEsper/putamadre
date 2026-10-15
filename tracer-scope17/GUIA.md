# TaCZ .17: trazadoras, marcas de bala y rueda de la mira

## Instalar

1. Cierra Minecraft y el servidor.
2. Quita el TaCZ `.16-first-person-scope-ratio` y el Tactical `scope-range64` anteriores de `mods`.
3. Copia los **dos JAR** de este paquete a `mods`, tanto en clientes como en servidor. No dejes versiones duplicadas.
4. Conserva `taczaddon-1.1.8.3-neoforge-1.21.1.jar` y tus packs de armas. Se revisó el addon adjunto; no necesita cambios para esta corrección.

Instala los dos reemplazos juntos: Tactical usa el ayudante visual nuevo de TaCZ. Las versiones internas y el protocolo de red se conservan para respetar las dependencias del conjunto de mods.

## Trazadoras

El modelo de algunas municiones aplicaba sus giros antes de guardar la transformación. Después, la trazadora volvía a aplicar el giro de la bala sobre esa transformación que seguía girada. Ahora el modelo guarda su transformación antes de girar y la restaura antes de dibujar la trazadora.

También se inicializan los ángulos anteriores al recibir una bala por red. Así, su primer fotograma no interpola desde una orientación de cero.

Las líneas de hitscan usan la transformación activa de la etapa de render, que se vuelve a consultar en cada fotograma al mover o girar la cámara.

Cada bala mantiene su propia trayectoria. Las trazas de hitscan conservan el inicio y final enviados por el servidor. No se cambia el daño, la dispersión, la precisión, el cálculo del impacto ni los paquetes de disparo. No se añade asistencia de apuntado.

## Marcas de impacto

Tactical sustituía la textura de la marca por una del atlas de bloques, pero el render dibujaba usando el atlas de partículas. Por eso sus coordenadas podían acabar mostrando trozos de cofres u otros bloques.

Ahora se usan sprites registrados explícitamente en el **atlas de partículas**, con transparencia y cuatro categorías visuales:

- Pistola / subfusil: marca pequeña.
- Fusil: marca intermedia.
- Calibre pesado: marca más grande.
- Perdigones / escopeta: marca pequeña por impacto de perdigón.

Se reconoce primero el calibre conocido del identificador de munición, y se usan los datos del arma y número de proyectiles como respaldo cuando el pack tiene nombres propios. Un pack con identificadores desconocidos puede usar una categoría genérica; esto no cambia su daño real. El tipo de superficie no se modifica.

Las marcas persistentes conservan el tamaño elegido para su categoría. Las partículas viejas desaparecen al reiniciar la sesión.

## Zoom con la rueda

La rueda funciona mientras apuntas y usa los valores declarados por la mira instalada, incluyendo miras integradas:

- Un punto rojo de **1×** permanece en **1×**.
- Una mira fija de **6×** permanece en **6×**.
- Una mira variable declarada como **4×–25×** permite ajustar dentro de ese rango y nunca supera **25×**.
- El mensaje muestra el aumento actual y el máximo: `Mira: 12.0× / 25.0×`.

Se retiran el máximo global artificial de 64× y el multiplicador adicional de 1,35 de la cámara. Las miras con aumentos legítimos mayores no quedan recortadas por el antiguo máximo global. La rueda no modifica los datos originales de la mira ni añade aumentos a una mira que no los declare.

## Prueba recomendada

En una copia del mundo, dispara contra una pared en cuatro direcciones y hacia arriba/abajo con distintas municiones. Haz que otro jugador dispare mientras ambos giran: comprueba la dirección de las trazadoras. Mira las marcas de cerca, cambia de zona y vuelve. Prueba un punto rojo, una mira fija y una variable con la rueda en ambos sentidos. Reinicia también el servidor para verificar que todos usan los dos JAR nuevos.

Validación: 310 comprobaciones de zoom y selección de marcas con APIs simuladas; 332 comprobaciones del bytecode y del aislamiento de transformaciones, incluidas 325 combinaciones simbólicas de giro. Recursos y puntos de mixin verificados. **Falta probarlo visualmente dentro de Minecraft con tu pack completo.**
