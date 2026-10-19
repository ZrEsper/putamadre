# Gore .14 — anatomía conectada, sangre arterial y luz de cadáveres

Basado en los JAR aportados: Gore1.0.13 y Ragdollified gore-loot.2, fijados por SHA en inputs.json. Los cinco JAR adjuntos coinciden con el manifest aportado (supplied-manifest-audit.json). LEEME y manifest se trataron como documentación del paquete; la solicitud del usuario define estos cambios.

Compilar desde la raíz con `python gore-anatomy14/build.py`; verificar con `python gore-anatomy14/test.py`. Usa Java21, ECJ/ASM existentes. `python gore-anatomy14/preview.py` genera una vista técnica de la geometría, sin piel, a partir del JSON exportado por las pruebas; requiere matplotlib. La imagen no es una captura del juego.

## Luz

Ragdollified calculaba la luz a partir de la posición central del torso. El nuevo puente toma muestras en las transformaciones físicas de las partes humanoides y otras rutas de partes de animales, conservando la luz ambiental de cada punto. Además entrega esas muestras a `net.flashlight.battery.EntityLight14.light`, la API de Flashlight .16 identificada en latest.log. Reutiliza su cono, oclusión, luz de bloque y presupuesto global de24rayos/tick. No fuerza fullbright ni añade luces de bloques al mundo. Conserva la luz de cielo, los materiales y el renderizador de entidades para que Iris pueda tratarlos.

La caché propia se limita a64muestras. Sin Flashlight compatible conserva luz ambiental; informa una sola vez de la ausencia del puente. Armaduras o renderizadores especiales que no pasan por estas rutas pueden conservar el muestreo central del cuerpo. No se ejecutó una prueba visual con MakeUp.

## Anatomía

Se sustituye la construcción de mallas internas de Mesh13 por Anatomy14. La piel y sus UV originales se conservan en las porciones no eliminadas. Las heridas siguen usando el estado regional acumulado y las rutas existentes de EMF/ragdoll, sin restaurar tejido al cambiar de fase.

Cráneo con región occipital, bóveda, mandíbula en U y ramas unidas, paladar, dientes, órbitas, nervios ópticos, lóbulos cerebrales y conexión cervical. Torso con columna, esternón, clavículas, costillas que rodean el tórax, pelvis, pulmones, corazón, diafragma, hígado, estómago y asas intestinales conectadas mediante tejidos de soporte. Extremidades con hueso proximal, articulación, dos huesos distales, tendón continuo y músculos de volumen escalonado, no cubos sueltos. Órganos y músculos usan perfiles de cinco secciones para dar forma al volumen.

La destrucción progresiva puede retirar regiones de bóveda/temporal, abrir costillas y mostrar una fractura en el hueso proximal con tejidos que mantienen la unión. Todo se construye en el marco local de la parte dañada y dentro de sus límites. Se comprueba que cada estado interno forma un conjunto conectado y que el esqueleto humanoide ensamblado en pose base une cabeza, hombros y caderas. Se eliminan los pequeños cubos decorativos independientes de Shards13, que podían confundirse con partes flotantes. Las amputaciones físicas reales por explosión se conservan.

En la jerarquía de EMF se distinguen raíces anatómicas y piezas decorativas: una raíz vacía no inventa un cubo de piel; sus hijos se conservan, pero no generan otro cráneo u órganos. El contexto se limpia al terminar el render. Esto elimina una fuente de anatomía duplicada en accesorios.

La geometría es de estilo voxel, no anatomía médica fotorrealista. Las uniones comprobadas son locales y de la pose humanoide base: la separación de cuerpos rígidos bajo fuerzas extremas sigue dependiendo del solver de Ragdollified. No se puede garantizar que todos los modelos personalizados y poses físicas mantengan juntas perfectas. Una cabeza que el estado anterior marque como amputada sigue amputada; no reaparece para mostrar la mandíbula.

## Arterias y sonido

Se usa un bit libre del impacto sincronizado para señalar una lesión arterial: cuello, impactos repetidos profundos en extremidades o repetidos en torso. El código de región existente se mantiene en tres bits. Es una clasificación por zona/severidad, no simulación de vasos individuales.

El cliente genera pulsos direccionales con presión que decae durante cuatro segundos mientras la criatura está viva. Máximo12partículas arteriales/tick, dentro del presupuesto total de48 de Blood12. En cuerpos muertos se sustituye el chorro vertical por gotas lentas, reduciendo la emisión inicial a20ticks; los impactos posteriores conservan drenaje breve. No se simula presión cardiaca permanente en un cadáver.

El audio arterial se recorta del archivo de arteria aportado (1,0–2,1s), con compresión, ganancia y fades para que resulte audible. El impacto húmedo usa0,1–0,6s del audio Gorebox aportado. Ambos son mono44,1kHz. El sonido inicial se envía desde el servidor en el impacto, incluso si resulta mortal; el cliente evita duplicar ese inicio y puede repetir después un pulso acotado; no hay una espera de2s añadida. Se limita a dos inicios/tick, cuatro por segundo globales y al menos1,5s entre repeticiones del mismo cuerpo. No se ponen pistas largas en bucle.

## Compatibilidad, loot y log

Solo cambian Gore y el renderizador/metadata de Ragdollified. Se conservan sus redes de inventario, físicas, loot y persistencia. Mantener Zombie Remains physical-corpse-loot.16, Mob Dismemberment gore13-compat.3 y Splatter blood-budget.2 adjuntos. Reemplazar Gore y Ragdollified tanto en cliente como servidor para compartir la interpretación de impactos. No mezclar varias versiones.

El log no muestra una excepción de Gore durante la sesión. Sí advierte de modelos no capturados para tacznpcs:npc, mutants_l4d2:screamer y undeadnights:horde_zombie, dibujados sobre un humanoide vanilla; eso puede provocar UV incorrectos en criaturas concretas y no se corrige inventando su modelo. Los avisos de overlays de resourcepacks y texturas ausentes de otros mods no se modifican aquí.

## Verificación

199 comprobaciones: conectividad y límites de malla en42combinaciones de región/daño, vertices finitos, presupuesto geométrico, mandíbula/órganos presentes, ensamblaje humanoide, clasificación arterial, sonido con límites, presión direccional y drenaje muerto, luz ambiental de segmentos y llamada al presupuesto existente de Flashlight. Java `-Xverify:all` y ASM BasicVerifier para las clases de Gore y el renderizador de Ragdollified modificado. Los recursos previos y Mixin configs se conservan; se añaden dos sonidos. Las fixtures no se empaquetan.

No se inició Minecraft ni un servidor completo. No se ha medido FPS/TPS ni se ha verificado visualmente el resultado con el modpack. Las pruebas de API no garantizan el aspecto final ni las juntas de toda simulación física.

## Dependency loading hotfix

After `python gore-anatomy14/build.py` and `python gore-anatomy14/test.py`, run
`python gore-anatomy14/compat.py`. This pins the three consumers to Gore
`[1.0.14,1.0.15)`, checks the real packaged TOML version and preserves every
non-metadata entry. It produces the complete four-mod compatibility ZIP.
Mob Dismemberment is client-only. Keep Splatter unchanged.
