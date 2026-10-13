# Hordes 26: sonidos de barreras y mordidas

Reemplaza Hordes 25 por `mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.26-action-sounds.jar` en cliente y servidor. Conserva el panel administrativo, la limpieza programada y el resto de funciones del JAR enviado.

- Puertas y trampillas: audio de golpes al iniciar BarrierBreakGoal, no al escanear candidatos. No se reproduce para ventanas al empezar.
- Ventanas/cristales: audio de vidrio únicamente después de que destroyBlock devuelve true, y solo si la barrera original era vidrio/ventana según la clasificación existente del mod. No suena cuando la rotura falla o se interrumpe. Conserva los sonidos de impacto y rotura vanilla existentes.
- Mordida al jugador: audio de tela en LivingDamageEvent.Post con daño final positivo, atacante zombi o entidad controlada por Reactive y atacante directo igual al causante. No suena en un intento bloqueado, ataque fallido, proyectil o daño recibido por un zombi.

Los tres OGG enviados se convierten a mono Vorbis para sonido posicional. Conservan su duración: golpes ~0,81 s, vidrio ~4,53 s y tela ~8,08 s. Hay un límite por posición de 1 segundo para golpes y 5 segundos para vidrio, y por jugador de 8,1 segundos para tela, evitando superponer el mismo clip. Solo el servidor emite los sonidos hacia jugadores cercanos; no hay reproducción adicional local. Los caches usan claves débiles; el cache de posiciones se limpia al usarlo y tiene un límite de 256 posiciones por mundo. Los clips están dentro del JAR y tienen subtítulos en español e inglés.

Construcción: `python hordes-sounds-patch/build.py`. Pruebas: `python hordes-sounds-patch/test.py`. Requiere herramientas ECJ/ASM preparadas en el entorno y ffmpeg para reconvertir los originales si falta audio/. Base de entrada incluida con SHA-256 en validation.json. No añade nuevos mixins ni modifica sus configuraciones.

Validación: 15 pruebas ejecutan el helper real con dobles de Minecraft y verifican inicio de rotura, ausencia de vidrio al inicio, rotura fallida/exitosа, límites, solo servidor y mordida con daño final/proyectil. ASM verifica los 872 métodos del JAR. No se ejecutó Minecraft con este parche ni se escuchó dentro del juego con el modpack completo.
