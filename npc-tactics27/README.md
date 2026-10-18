# NPC .27: amenazas, cobertura y lesiones

Base reproducible: NPC .26 fijado por SHA en `inputs.json`. Compilar desde la raíz con `python npc-tactics27/build.py`, comprobar con `python npc-tactics27/test.py`. Usa Java 21, ECJ/ASM y las fixtures existentes de npc-social17/npc-roles22; no necesita iniciar Minecraft para estas pruebas. No cambia Flashlight .16, Global Melee .39, Fracture ni TaCZ.

## Decisiones

La adquisición de los roles hostil/militar deja de usar solo distancia. Un disparo de jugador oído en 32 bloques crea memoria por identidad de seis segundos, limitada a 16 identidades por NPC; reacciona sin otra espera de inspección si después tiene visión. Un zombi ya a menos de 2,5 bloques es una emergencia inmediata: tras resolverla, vuelve a evaluar al jugador. El resto conserva cinco segundos de inspección y las excepciones de facción, creativo y espectador. La observación tolera pérdidas breves de vista; oír no autoriza disparar a través de paredes. Al perder contacto conserva la última posición, sin seguir las coordenadas de un jugador oculto.

Adquisición cada diez ticks, como antes; los disparos fuerzan la siguiente evaluación. Cada consulta procesa hasta 48 monstruos y 32 jugadores. Las posiciones de cobertura conservan el límite anterior de cuatro pruebas de navegación y la caché de 60 ticks. Las celdas revisadas al buscar se limitan a 32 por NPC; solo se usan chunks cargados.

## Cobertura y abastecimiento

Heridas, inferioridad numérica local, impactos recientes o que el jugador mire hacia quien acaba de dispararle provocan un ciclo breve de retirada. Recargar mantiene la prioridad de cobertura. No se inicia tratamiento en un destino que no tape la línea desde el enemigo. Ya cubierto puede tratarse durante tres segundos gastando una venda real; sigue usando el sistema existente de partes del cuerpo. No gana suministros ni curación infinita. Las decisiones tienen intervalos para impedir retirada permanente.

Durante búsquedas, una celda nueva con obstrucción lateral activa una revisión corta de izquierda/derecha, mirando agachado. Es un reconocimiento local de esquinas, no planificación completa de edificios. Los impactos activan alerta/miedo según heridas y un gesto corporal breve sin lanzar al NPC por los aires.

En calma, o protegido en cobertura, come cada cinco minutos y bebe cada tres si dispone de provisiones. Consume un alimento con componente FOOD o una poción de agua vanilla del inventario; conserva la botella vacía. Una amenaza interrumpe la acción sin consumir. Hay gesto de brazo y sonidos vanilla; no hay un nuevo objeto de comida renderizado en la mano ni un sistema de inanición. No se beben pociones de otros tipos. No se genera agua si no la tiene.

## Armadura y voces

El método original de lesiones restaba todo el daño de entrada a una parte, aunque la vida real hubiera perdido mucho menos por la armadura. El nuevo enganche registra la vida antes de `hurt` y aplica a la parte solo la pérdida real posterior, limitada al daño original. Conserva la mitigación, durabilidad y penetración que resuelven Minecraft/TaCZ, sin aplicar dos veces la armadura. Un disparo absorbido no crea una lesión fantasma. Armas potentes y disparos letales todavía pueden derribar al NPC; no se concede invulnerabilidad por llevar cuatro piezas.

Las respuestas ambientales se seleccionan por cada oyente. Un enemigo recibe frases de enemigo, incluyendo ante acciones que antes disparaban agradecimiento. No se emiten comentarios amistosos de ambiente mientras se está combatiendo con otro objetivo. La reacción sonora genérica tampoco usa alegría en combate. Se conservan las líneas de radio existentes de detección/búsqueda/patrulla.

## Animación y regresiones

Se mantienen el seguimiento de cabeza/cuerpo tras los controles nativos, el fogonazo, la visión nocturna bajada y los perfiles de linterna/láser de .26. El deslizamiento conserva suelo, gravedad, colisiones, alcance y cooldown. Ahora acelera/frena con easing; el modelo interpola entrada/salida durante 150 ms desde una base agachada, sin utilizar la rotación de nado como animación de deslizamiento. Añade reacción corporal de impacto y gestos de comer, beber y tratarse. Ropa y extremidades reciben las mismas transformaciones.

118 comprobaciones numeradas, más prueba del modelo, con Java `-Xverify:all`; ASM BasicVerifier revisa nueve enganches nativos. Se comprueban prioridad/retorno al jugador, paredes, inspección, creativo, daño efectivo en el método real de lesiones, diálogo contextual, easing, consumo/interrupción de comida, agua/botella, tratamiento desde cobertura y regresiones de .26. Los 93 recursos/data anteriores y la configuración Mixin quedan idénticos. Las fixtures no entran en el JAR.

No se ejecutó Minecraft ni se midieron FPS/TPS. Las pruebas simulan las APIs, no demuestran calidad visual con el modpack o MakeUp. El aspecto del deslizamiento y la conducta en los edificios del mapa necesitan comprobación dentro del juego.
