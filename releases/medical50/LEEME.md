# Corrección 50 — NeoForge 1.21.1

Base: [baconwithd/Steph, revisión medical49](https://github.com/baconwithd/Steph/tree/af11cf3b584927f0eb958bf64043b1f6421d0356). El log confirma Gore .33, Ragdoll .26, NPCs .32 y FirstAid 3.6.16; esta entrega continúa esa combinación.

## Instalar

Cierra el juego y el servidor. Sustituye las versiones anteriores de los siete mods incluidos por los JAR de `mods/`; deja **una sola versión de cada mod**. Zombie Remains Loot se vuelve a añadir: no estaba cargado en el log recibido. Instala la misma combinación en los clientes y el servidor donde se utilicen estos mods.

- Gore .34 reemplaza .33.
- Ragdollified .27 reemplaza .26.
- TaCZ NPCs .33 reemplaza .32.
- FirstAid 3.6.17 reemplaza 3.6.16.
- TaCZ FirstAid Compat 1.0.5 reemplaza 1.0.4; su dependencia coincide con FirstAid 3.6.17.
- Zombie Remains Loot .24 recupera el inventario de zombis.
- Petrichor depth-copy .1 reemplaza Petrichor 1.4.0.

Conserva Corpse .5, Splatter y los demás mods de tu instalación. La base de Gore requiere EMF. Zombie Remains Loot necesita Doomsday Decoration y NeoForge 21.1.255, que aparecen en tu log. Prueba inicialmente con supervivientes y zombis nuevos: los cuerpos visuales antiguos que carecen de registro del servidor no se convierten retroactivamente en cadáveres válidos.

## Cambios

- Derribo de supervivientes: crea un ragdoll **vivo** antes de imponer la postura tumbada; conserva la piel, el tipo slim y la armadura. Oculta el modelo original mientras cae, sigue la posición física y retira el ragdoll al revivir o crear el cadáver definitivo. El derribo no crea ni traslada un inventario.
- Saqueo de zombis: restituye el módulo que guarda el inventario físico. Clic derecho sobre el cuerpo; con un arma TaCZ, agáchate y usa clic derecho para no sustituir el apuntado. Los NPCs quedan excluidos de este generador de botín para conservar su único inventario de Corpse. El sistema conserva un límite de hasta 48 inventarios físicos por dimensión y retira los antiguos; respeta el plazo más corto entre el de descomposición y el del ragdoll. Al retirarlos, suelta el contenido restante y utiliza la conversión a remains con comprobación de espacio, paredes y sonido existente.
- Desfibrilador: el sangrado ejecutado desde la rutina médica no cancela el intento; los ataques y otros daños externos sí lo interrumpen. Se conservan los cinco usos, el consumo únicamente al completar y el rearme del estado del cliente.
- Impactos: el contenedor invisible de Corpse deja pasar la selección de la superficie física, tanto para TaCZ como para golpes. La coordenada del golpe conserva el punto realmente alcanzado, en vez de desplazar la lesión hacia un centro aproximado. La intersección consulta la carne y anatomía supervivientes, de modo que se puedan seguir dañando los restos visibles. Se conserva el punto de entrada antes de que el daño mueva o gire a la víctima.
- Geometría: las capas de piel y ropa dejan de conservar una esquina sin apoyo; el recorte mantiene las unidades de modelo corregidas en medical49. Se retira el desplazamiento decorativo por vértice de los órganos que separaba su superficie de la base. Los órganos desprendidos conservan su ruta de física independiente. Esto aborda causas de fragmentos y estiramientos; **la raya negra descrita necesita comprobarse visualmente en el juego**.
- Petrichor: la copia de escena asigna al destino el formato de profundidad real del origen, incluidos profundidad con stencil y cambios de resolución. Se conserva una sola copia por fotograma y se restablece la textura y el destino de dibujo.
- Rendimiento: caché limitada de 16 estados de lesiones por pieza de armadura para evitar reconstruirla al alternar cadáveres; memoria de raíces de modelo limitada a 2048 entradas; también se recuerdan las integraciones opcionales ausentes. No se añaden cargas de chunks.

## Evidencia y límites

El log contiene **24 446 errores** de OpenGL por formatos de profundidad incompatibles. El perfil es del cliente, Render thread, durante unos 318 segundos; no mide TPS del servidor. El recorrido de renderizado de ragdolls acumula aproximadamente el 34 % del tiempo muestreado, y hay reconstrucción de mallas y consultas de modelos repetidas. Los porcentajes inclusivos se solapan: no deben sumarse.

Se compilaron los cambios con Java 21 y se analizaron los métodos de 1586 clases de los seis JAR modificados. Se ejecutaron las funciones de producción con APIs simuladas: copia de escena con seis formatos y resoluciones distintas; cinco autorreanimaciones con consumo de usos e interrupción por ataques; derribo/revivida y modelo slim; límites y reutilización de caché. También pasan los controles anteriores de geometría, coordenadas locales, persistencia médica y ejecución de Bullet13.tick con un contenedor delante.

**No se ejecutó Minecraft ni una GPU en este entorno.** Las pruebas de profundidad comprueban las llamadas y estados con una API OpenGL simulada; las de reanimación simulan el modelo médico y el mundo. No prueban la apariencia final, la raya negra ni una mejora concreta de FPS. El recorte de armaduras cubre modelos humanoides ModelPart; los renderizadores GeoBone externos conservan las limitaciones de la base.

Los resultados y SHA-256 se adjuntan. El archivo de fuentes contiene los parches, las APIs de compilación y las pruebas nuevas; las pruebas de regresión reutilizan las fuentes publicadas en medical49. `build.py` documenta las rutas de los JAR de entrada y de ECJ/ASM usados en este entorno; no incluye dependencias de Minecraft simuladas dentro de los mods.
