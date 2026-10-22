Resultados automáticos: 160 clases verificadas con ASM BasicVerifier; nueve puntos
exactos de parche en siete categorías. 277 controles de regresión, 15126 de identidad,
emboscadas, privacidad y chat, y 124 de Fase 3/2.6.1. Diez escenarios de configuración en
JVMs separadas. JVM -Xverify:all; Brigadier real; APIs de Minecraft SIMULADAS.

Cubierto: bloqueo/rupturas/tokens; 5000 identidades; dos ayudantes reales en la simulación;
transferencia finita y token repetido; rendición, negativa y bolsa vacía; spam continuo,
pausa y cambio de motivo; 30 s tras última línea; UUIDs independientes; muerte del
jugador; borrado al morir NPC; decaimiento y ventanas graves superpuestas; memoria
limitada por peso; siete estados; reconocimiento/mentira; JSON inválido, recarga y
migración .37 sin duplicación; conservación de identidad; comandos y confirmación segura.

NO se ejecutó Minecraft real, NeoForge, servidor dedicado ni GPU. No se probaron dentro
del modpack el guardado real en disco, carga/descarga de chunks, SavedData real,
sincronización multijugador, ADS de TaCZ, interacción del clic derecho, poses, navegación,
combate de emboscada, shaders o coste de render. Los logs recibidos corresponden a .37.

Tabla para probar en Minecraft real — TODAS LAS FILAS ESTÁN PENDIENTES:

| Caso | Pasos | Qué debe pasar |
|---|---|---|
| Actualizar desde .37 | Copiar mundo; reemplazar JAR en cliente/servidor; entrar | Carga .38 y original JSON respaldado como .pre38; identidades iguales |
| Ayudar y volver | Hablar con cansado; usar /npcexample cansado familiar_enfermo; dar medicina; retirarse y volver | Pierdes una medicina; acuerdo; saludo recuerda la ayuda y baja el apuntado |
| Memoria en opciones | Tras ayudar, iniciar charla normal con ese NPC | Aparece ¿Te acuerdas de mí?; respuesta distinta por ayuda; nunca más de cuatro opciones |
| Traicionar y volver | Atacar al NPC sin matarlo; retirarse hasta superar su reintento; volver | Memoria ataco, menos confianza, más miedo/hostilidad y saludo de rencor |
| Violencia grave | Repetir agresiones sin matarlo; revisar /npcdebug | A hostilidad efectiva >=75 y memoria grave, no hay opciones; solo amenazas |
| Persistencia | /npcdebug y /npcmemory ver; guardar/salir; reiniciar; repetir | Misma identidad, UUID, recuerdos y relación; decaimiento por tiempo de juego |
| Descargar chunk | Alejarse del NPC hasta descargarlo; jugar un rato; volver | Recuerdos conservados; medidores ajustados al tiempo, sin forzar chunks |
| Dos jugadores | A ayuda, B ataca al mismo NPC; ambos usan /npcdebug | Relaciones y hostilidad personal distintas; miedo actual compartido |
| Decaimiento | Amenazar/atacar, anotar valores; dejar pasar minutos; consultar | Miedo/desesperacion vuelven a base; hostilidad baja lento; confianza casi estable |
| Marca grave | Comparar relación con un recuerdo peso >=75 frente a una sin él | Confianza/hostilidad personal decaen a menor velocidad durante su marca |
| Estados | Alterar un umbral de social.estados, /npcreload, /npcdebug | Estado cambia según reglas ordenadas; condiciones estado_encuentro lo usan |
| Ver recuerdos | Mirar al NPC y ejecutar /npcmemory ver | Lista tipo, texto, tick y peso de sus recuerdos sobre tu UUID |
| Borrado seguro | /npcmemory borrar; pegar confirmación antes de 10 s | Solo se borra su memoria sobre ti; medidores y datos de B se conservan |
| Confirmación inválida | Esperar >10 s o mirar a otro NPC antes de confirmar | No borra nada |
| Permisos | Probar /npcmemory sin ser operador | No permite ver/borrar |
| Migrar memoria .37 | Abrir copia de mundo con NPC que ya recuerda eventos | Un aviso de migración por NPC; no duplica eventos tras guardar/reabrir |
| JSON personalizado .37 | Cambiar una frase en schema 3 sin campos nuevos; iniciar .38 | Conserva tu frase y agrega defaults en memoria; no pierde identidades |
| JSON inválido | Poner tipo desconocido, variable prohibida o umbral 999; /npcvalidate | Reporte con archivo/campo; /npcreload conserva último válido y memoria |
| JSON roto al arrancar | Hacerlo en copia de prueba y reiniciar | Usa respaldo incluido, log de error y archivo inválido intacto |
| Spam con arma/ADS | Intentar hablar repetidamente apuntando con la mira | Un aviso por mensaje; clics continuos no lo repiten cada dos segundos |
| Spam sin arma | Insistir tras ruptura o ejecutar /npcreply sin sesión | Un aviso por motivo dentro del plazo; motivo nuevo puede mostrarse |
| Spam con peligro | Intentar hablar con zombi cerca varias veces | Aviso único; no bloquea la defensa del NPC |
| Pausa antispam | Dejar de intentar durante >10 s y volver | Puede mostrar de nuevo el aviso |
| Ritmo | Abrir charla, leer los mensajes y esperar 15 s tras opciones | Sigue habiendo tiempo; pausa de 2,5 s entre líneas; plazo total 30 s |
| Silencio | No elegir durante 30 s desde las opciones | Una reacción al silencio, cierre único y opciones anuladas |
| Robo | Arma sostenida, sin ADS y sin otros peligros; hacer clic en NPC | Opciones de intimidación, sin aviso genérico de arma/peligro |
| Robo finito | Elegir exigir con NPC temeroso que tenga bolsa; repetir token | Una unidad transferida, nada creado y token usado rechazado |
| Resistencia | Exigir a hostil de miedo bajo y luego insistir | Se niega; la segunda amenaza termina en combate |
| Bolsa vacía/lleno | Robar a NPC sin bolsa; luego con inventario del jugador lleno | No aparecen objetos gratuitos ni transferencias imposibles |
| Rupturas | Sacar arma en charla normal; ADS en intimidación; correr/disparar/recibir daño | Termina la charla, anula opciones y libera IA nativa |
| Emboscada anterior | NPC nuevo, cobertura física; /npcexample seco familiar_herido_companeros; acercarse tras advertencia | Dos compañeros reales combaten; no se duplican; resultado traicion |
| Anti-repetición NPC | Volver a elegir una rama ya vista dentro del mismo encuentro | No repite la línea; usa cierre seguro cuando agota texto |
| Morir como jugador | NPC con recuerdos de A; A muere/reaparece | La relación por UUID se conserva |
| Muerte definitiva NPC | Matarlo, sin simple derribo; observar ayudantes vinculados | Borra memoria del muerto; ayudantes testigos guardan mato_aliado |

Limitaciones y decisiones:
- Las seis voces de respaldo siguen siendo mínimas; no se reescribieron sus encuentros.
- No hay grupos, rumores, facciones ni población nueva. Solo campos y SavedData reservados.
- Un recuerdo antiguo ambiguo se conserva como otro; no se puede reconstruir un contexto
  que .37 no guardó. Descubrir mentira del NPC no penaliza al jugador como mentiroso.
- Robo de una unidad de bolsa, no de equipo ni armas equipadas. ADS sigue siendo ruptura.
- Hay 30 s para elegir en lugar de los 12 anteriores, por la petición de más tiempo.
- El estado neutral es el respaldo; las reglas anteriores tienen prioridad en su orden.
- Identidades pueden repetir nombres de bancos finitos; no se regeneran al recargar.
- El reconocimiento no añade voces grabadas ni promesas de IA generativa.
