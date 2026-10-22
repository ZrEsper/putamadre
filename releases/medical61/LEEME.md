# Corrección 61 — franjas de luz y convivencia con coches/NPCs

Minecraft 1.21.1, NeoForge. Reemplaza Flashlight .26 por **Flashlight .27**; deja una sola versión en mods. Conserva Immersive Vehicles mechanics .17, Darkness .7 y los demás mods de la entrega anterior. El ZIP incluye únicamente el mod que cambia.

## Cambios

Con Iris, la captura de luz usa profundidad a media resolución. Antes se reconstruía el punto capturado usando las coordenadas del píxel de pantalla completo; después se comparaban ambas profundidades y se descartaba la iluminación si parecían pertenecer a superficies distintas. En suelos vistos de forma oblicua, los centros diferentes de los píxeles producen filas alternas sin luz. Ahora cada muestra se reconstruye desde su centro real y se comprueba contra el plano local de la superficie. Se buscan cuatro muestras vecinas con una distancia limitada para evitar tomar texturas lejanas en los bordes. Cuando no hay una muestra válida se utiliza el color de la superficie visible, sin cancelar por completo la iluminación de esa fila.

La entrega 60 reservaba dos luces en total: al coincidir jugador, NPC y coche, entraban jugador y coche y se expulsaba al NPC. Ahora se reservan hasta dos luces personales y una plaza adicional para un vehículo. Si quedan plazas personales vacías pueden usarlas otros vehículos. La textura de sombras, el búfer de subida y los arrays del shader se amplían de dos a tres luces de forma coordinada. El brillo sigue asociado a la luz seleccionada, no a su antigua plaza. Al desaparecer un coche no cambia el índice del NPC.

El presupuesto de vehículos es compartido: como máximo 24 rayos por tick, incluso si entra otro coche entre la captura y la composición. No se amplía el presupuesto de rayos de luces personales ni se añade un segundo pase de dibujo de pantalla. El búfer de sombras aumenta 576 bytes y su textura otros 576 bytes. Hay un foco adicional por píxel cuando está activo y se comprueban hasta cuatro muestras de profundidad; no se afirma una mejora de FPS.

Se conservan la recuperación de capturas de la entrega 60, el formato coincidente de profundidad, el encendido por inventario, las baterías y las matrices de cámara de la entrega 58.

## Validación y límites

- Flashlight .26 reproduce la expulsión del NPC al entrar el coche. .27 pasa 23 comprobaciones de selección, brillo, relleno de cámara, cambios de mundo y presupuesto compartido, con helpers de producción y geometría real de MTS sobre un mundo simulado.
- OpenGL real (EGL/Mesa llvmpipe): en un plano oblicuo con captura a media resolución, la versión anterior produce filas alternas sin iluminar. La variación media entre filas baja de 0.00983 a 0.00112 con la corrección, manteniendo iluminación continua. Es una reproducción controlada del defecto del algoritmo, no una captura de Minecraft corregido.
- Shader GLSL 150 compilado y enlazado; dibujo real del tercer foco y de su región de sombras, valores finitos y caída suave del haz.
- Se conserva y prueba la invalidación de almacenamiento y el reintento acotado de captura. Controles GPU de copia D24, D32F, D24S8 y D32FS8 y de identificador reutilizado.
- Java 21, verificación ASM de 66 clases; tres cambios coordinados en atlas/búfer y un cambio de límite de subida, sin clases de pruebas en el JAR.

No se ejecutó el modpack completo, MakeUp dentro de Iris, las tres cámaras en Minecraft ni el controlador NVIDIA. Por ello estos resultados no garantizan que no quede otro origen de parpadeo. El log adjunto carga .26 y MakeUp UltraFast 9.5g; no muestra un nuevo error de copia de profundidad que justifique atribuir este caso al error de la entrega 60.

Base: entrega 60, commit f2fb69b9e8a7929fe8c6eeebfc44cd934ac82950. El archivo de fuentes incluye el parche ASM, helper, shader y pruebas; build.py usa el JAR base .26 y ECJ/ASM del entorno del repositorio.
