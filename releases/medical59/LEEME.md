# Corrección 59 — linternas, faros y remains looteables

Minecraft 1.21.1 / NeoForge. Reemplaza estos seis mods, retirando sus versiones anteriores:

- Flashlight .24 → .25.
- Hardcore True Darkness .6 → .7.
- Immersive Vehicles mechanics .16 → .17.
- Gore .40 → .41 (requiere conservar Ragdoll .32).
- Zombie Remains Loot .25 → .26.
- Quietly .19 → .20.

Conserva Ragdoll .32, Dungeons Iso .34, Belroft Motors y MTS Official Pack. No necesitas reemplazar los paquetes de modelos ni MakeUp UltraFast por esta entrega. Mantén los mods en los equipos donde ya estaban instalados; no añadas mods que antes no usaba tu servidor.

## Qué fallaba

Darkness conservaba el marcador de emisión del láser (alfa 251/255) pero no el del haz de la linterna (250/255). Después de componer la luz, volvía a oscurecerla. El nuevo shader conserva ambos marcadores; las zonas de ambiente sin marcador mantienen su oscurecimiento original.

Los conos visibles de MTS son geometría con una textura translúcida. Ese efecto por sí solo no proyecta iluminación de las texturas del suelo en el compositor de Flashlight. La nueva integración registra los haces de los faros que MTS realmente está dibujando y los convierte en focos de iluminación de superficies.

## Faros

Se usan las posiciones y ejes de los componentes de luz de MTS y su transformación animada, más la posición de su entidad. El brillo llega después de las reglas nativas de interruptor/batería. Los haces de una misma entidad se agrupan en un foco para limitar el coste; los faros independientes de modelos complejos pueden ocupar focos distintos.

El compositor mantiene un máximo de DOS focos: si hay un faro activo, reserva una plaza para el vehículo más cercano y conserva como máximo una linterna personal/NPC. Sin linternas personales puede mostrar dos vehículos. Se filtran componentes que solo son destellos, luces sin haz y distancias superiores a 64 bloques. Al dejar de renderizarse/apagarse un faro, el registro caduca en 250 ms. Las luces de vehículos que dejan de renderizarse fuera de la vista también caducan; no se realiza una búsqueda de todos los vehículos cargados.

El mapa de oclusión sigue usando colisiones de bloques. Presupuesto adicional máximo de 24 rayos por foco de vehículo y tick, sin repetirlo entre captura y composición del mismo tick. La actualización es gradual y conserva las limitaciones de resolución del compositor actual. El foco no recibe la inclinación automática de linterna personal al cambiar de cámara.

MTS conserva sus controles, modelos, mecánica, inventarios y protocolo. Si Flashlight no está instalado, el puente de MTS omite la integración y conserva su render nativo. No se cambia la emisión/volumen original de los faros, ni se añade iluminación universal al mundo.

## Validación y límites

OpenGL real con Mesa llvmpipe: los shaders originales y nuevos compilan, enlazan y dibujan. Un píxel de haz de 0,078431 acababa en 0,013261 con Darkness .6; con .7 conserva 0,078431. El ambiente sin marcador sigue en 0,013261; el láser conserva su brillo y un haz débil se conserva. No es una medición de FPS.

Pasan 13 comprobaciones del helper de vehículos usando las clases reales Point3D, TransformationMatrix y JSONLight de MTS: transformación/orientación, agrupación, brillo, límite de focos/rayos, ausencia de doble muestreo, filtro de destellos, caducidad y cambio de mundo. El mundo y el muestreador de rayos son fixtures simulados. Verificación ASM: 65 clases de Flashlight, 873 de MTS y 29 de Darkness.

No se ejecutó el modpack completo, ni se verificaron visualmente todos los coches y las tres cámaras en Minecraft. La integración requiere que el modelo tenga un componente de haz nativo de MTS y que dicho haz se esté renderizando; un paquete que solo defina un destello no obtiene un foco. Con shaders, no se pueden recuperar detalles de textura que el shader haya convertido en negro absoluto. Se conserva la corrección de matrices de Flashlight .24 y las copias de escena optimizadas.

## Reproducción

Fuentes, parche ASM, shaders y pruebas incluidos en el ZIP de fuentes. Requiere Java 21, ECJ/ASM del entorno del repositorio y EGL/Mesa para la prueba GPU. Los hashes de las entradas/salidas están en el manifiesto. Los fixtures de tests no se incluyen en los JARs. Base: Corrección 58, commit 355244b343a50a972cd24eb498d2d8d936cf1c4e de ZrEsper/putamadre, y los JARs MTS/Darkness adjuntados.


## Remains looteables

La conversión ahora crea el registro persistente del bloque y mueve las pilas reales del ragdoll a su inventario antes de que el retiro original derrame los objetos. El contenedor anterior solo se vacía después de una copia completa; ante un fallo de escritura se revierte el destino y se conserva la carga original. Si no cabe un remains en el espacio permitido, continúa la caída de objetos original.

El acceso directo y la búsqueda de Quietly reparan registros ausentes de remains antiguos al hacer clic. Conservan contenido que ya estuviera guardado. Los restos antiguos que nunca tuvieron registro se abren vacíos: no se inventa ni regenera botín que ya pudiera haberse recogido como objetos caídos. No modifica cofres/barriles del jugador ni regenera loot existente.

Pasan 12 controles de transferencia, reparación, conservación de inventario, repetición, fallo de colocación y rollback, con helper real y almacenamiento/colocación simulados. También se verifica el bytecode de las rutas nativas de expiración, apertura y búsqueda. Falta la comprobación del menú dentro del modpack completo.

## Perfil Spark adjunto

Perfil del hilo Render de unos 76 segundos; estadísticas de servidor próximas a 20 TPS. Las rutas de Gore aparecen en aproximadamente 27,5 % de las muestras, Ragdoll 26,5 %, TaCZ 15,7 %, Flashlight 5,2 % y MTS 1,9 %. Estas rutas se solapan entre mods y NO se deben sumar. Aproximadamente 6,25 % de todas las muestras exclusivas se atribuyen a la preparación extendida de vértices de Iris bajo Organ29.vertex. También hay búsquedas de clases y excepciones repetidas en las integraciones de cámara/armas.

El coste principal pendiente es el renderizado de geometría y armas con Iris, no una caída general de TPS. Esta entrega limita los focos y rayos nuevos y conserva las optimizaciones previas; no cambia el renderer de TaCZ ni elimina órganos para esconder su coste. No hay un perfil posterior al parche ni una mejora porcentual de FPS medida. El informe resumido va incluido; no se publica el perfil bruto ni el log del usuario.
