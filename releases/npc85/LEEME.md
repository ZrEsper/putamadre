# Tactical .51 — supervivencia, ADS, puertas y sueño

## Instalación
Retira tactical .50 y anteriores; coloca este JAR .51. Conserva Ragdollified .34, remains .28 y Gore .43. Reinicia cliente y servidor si corresponde. Prueba primero en una copia del mundo a `/tick rate 20`.

## Amenazas y supervivencia
- Tener el arma en mano y mirar al NPC no activa la intimidación por apuntado. Se exige `IGunOperator.getSynIsAiming()` de TaCZ (clic derecho/ADS) y un rayo de mirada que intersecte el cuerpo del NPC. ADS dirigido a otro sitio tampoco cuenta. Se aplica al encuentro por apuntado, la respuesta hostil por apuntado y la evaluación ambiental de arma amenazante.
- Ser hostil por facción, recibir daño o escuchar disparos sigue siendo distinto de apuntar: un hostil aún puede atacar por su comportamiento existente sin esperar ADS.
- Compara poder estimado de las armas usando los datos TaCZ de daño, número de proyectiles y RPM, más armadura. Si no hay datos, usa un valor conservador de arma equipada. No clasifica todas las armas por nombres ni simula su precisión real, adjuntos, munición o cobertura.
- Un disparo del jugador con trayectoria a menos de 3 bloques del NPC, hacia delante, dentro de 24 bloques y con visibilidad, puede intimidarlo si el jugador tiene ventaja. No exige ADS para una advertencia disparada. No es una comprobación exacta del impacto físico de cada proyectil.
- Ventaja estimada mínima: 1.15 veces para advertencia, 1.45 para ADS. Con menos de 40% de salud el NPC cede más fácilmente. El miedo dura 300 ticks desde la amenaza válida; hace intentos de retirada transitables, suspende ataques contra el jugador y reduce la resistencia al robo del encuentro de apuntado. Un NPC sin ruta puede detenerse en vez de retroceder. No garantiza que cualquier enemigo se rinda a cualquier arma.
- El encuentro existente de extorsión usa miedo efectivo de al menos 65 mientras esa amenaza siga activa, por lo que el armado intimidado acepta su rama de tregua y entrega existente. No añade una nueva interfaz de inventario ni cambia las consecuencias del robo.
- Las armas equipadas bloquean `doHurtTarget`; el objetivo melee solo puede activarse sin arma. Se conserva el objetivo de disparo. No hay sustitución automática a puñetazos por recarga.

## Puertas y rutas
- El adaptador abre puertas DoorBlock transitables a mano próximas y delante de la ruta. Respeta bloques cargados y permisos. No fuerza puertas de hierro ni abre todas las puertas del vecindario; no usa un objetivo que vuelva a cerrarlas mientras se cruza.
- Reemplaza el ciclo anterior de empujes/manual sidestep/romper obstáculos para desatascar por navegación normal, con límites. No escribe posiciones manualmente ni teletransporta para cruzar.
- Tras 3 rutas incompletas o 200 ticks sin acercarse, abandona el destino. Moverse de lado sin progreso no reinicia el contador. Alternar 6 destinos sin desplazarse también activa espera.
- Un destino fallido queda excluido durante 1200 ticks, con pausa de 100 ticks y liberación del trabajo de Base48; luego puede reintentarse. Es una espera acotada, no una prohibición permanente. El estado de rutas vive en memoria y se reinicia al recargar.
- Esta recuperación ya no rompe paredes automáticamente. La tala/minería de sus objetivos de recursos permanece; no es una solución universal a todos los edificios, trampillas, puertas de otros mods o atascos entre entidades. Falta verificar las puertas del video dentro del modpack.

## Sueño y conversación
- Solo inicia el sueño en cama válida y libre; evita reiniciar `startSleeping` mientras ya duerme. Detiene navegación/apuntado y suspende controles de objetivos de movimiento/mirada mientras duerme.
- Despierta de día o con objetivo/alerta/amenaza relevante, restaura controles y espera 200 ticks antes de volver a acostarse. Usa el sueño nativo, no una animación nueva.
- Corrige la sesión nula que el log registra en `Chat71.holdDeadline` cuando se cancela una apertura por peligro. Una sesión ya cancelada no bombea líneas fantasma.

## Evidencia y límites
El crash adjunto con fecha 17:53:32 es idéntico al ya corregido de .49. El nuevo log carga .50 y muestra la sesión nula descrita y, por separado, errores de Flashlight17 y de recursos/OpenGL. Esta entrega no modifica Flashlight ni los shaders.

Pasaron 39 controles JVM nuevos (ADS, geometría de advertencia, lectura de datos de arma simulados, miedo, melee, sesión cancelada, puertas, reintentos y sueño), los 25 anteriores de diálogo/cadáveres y la prueba de colocación real de Base48 con mundo simulado. Se verificaron 205 clases con ASM y las firmas de los helpers. Los stubs de Minecraft/TaCZ usados en las pruebas NO están incluidos en el JAR.

NO se ejecutó Minecraft real ni tu modpack. No se ha comprobado que los NPCs del video crucen esas puertas, que sus modelos duerman correctamente, ni el comportamiento completo de combate multijugador. El log muestra `/tick rate 10000` y grandes retrasos: validar primero a 20 permite distinguir aceleración del servidor de fallos de IA.

## Prueba en partida
1. A 20 TPS: sostener arma mirando al NPC; después ADS hacia otro sitio; después ADS al NPC. Solo el tercer caso debe activar intimidación por apuntado.
2. Arma superior y disparo de advertencia cerca, sin impactarle: debe retirarse/ceder si tiene salida. Disparar lejos o detrás no debe activar esa advertencia.
3. Probar la opción de robo durante la intimidación y luego con un rival superior.
4. NPC armado cerca de enemigo: debe disparar o retirarse/recargar, no dar puñetazos.
5. Puerta de madera, hierro y salida bloqueada: cruzar la primera si la ruta física sirve; no forzar hierro; abandonar el destino atascado sin repetir empujes indefinidos.
6. Noche con cama: un inicio de sueño; día/alerta: despertar y no volver a acostarse inmediatamente.

Tags útiles: `npc78_navegacion`, `npc85_state`, `npc85_fear_until`, `npc85_threat`, `npc79_tarea`. El ZIP de fuentes incluye lista exacta de clases cambiadas y código/pruebas; sus dependencias apuntan a herramientas y fixtures locales, no a un proyecto Minecraft autónomo.
