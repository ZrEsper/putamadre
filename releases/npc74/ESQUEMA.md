El archivo completo editable está en dialogue-phase2.json; estas son las adiciones.

`config`: `respuesta_ticks` (240–2400, defecto 600), `mensajes_cooldown_ticks`
(20–2400, defecto 200), `robo_distancia` (2–16, defecto 10). Pausa entre mensajes:
`pausa_lineas_ticks`, nuevo defecto 50 (2,5 s). Usa tiempo de juego, no reloj externo.

`social.bases`: perfiles normal/hostil con miedo, desesperacion y hostilidad (0–100).
Se eligen al inicializar el NPC, según su disposición; no dependen de la voz. Cambiar
las bases del JSON afecta a nuevos NPCs. Las fórmulas y umbrales sí afectan a existentes.
El estado actual empieza igual a la base y los efectos lo modifican.

`social.formula`: por cada medidor, constante y coeficientes de fuentes `base`, `actual`
y `relacion`. Es una suma ponderada seguida de redondeo y límite 0–100. No acepta código,
expresiones ni comandos. Fuentes permitidas: base/actual = miedo, desesperacion,
hostilidad; relacion = confianza, hostilidad. Defectos, escritos en el propio JSON:

| Efectivo | Fórmula |
|---|---|
| confianza | relación.confianza |
| miedo | actual.miedo + 0,1 × relación.hostilidad |
| desesperacion | actual.desesperacion |
| hostilidad | 0,25 × actual.hostilidad + 0,75 × relación.hostilidad |

Los efectos confianza/hostilidad modifican la relación personal. Miedo/desesperacion
modifican el actual compartido del NPC. La confianza .37 se importa desde soc_trust_UUID,
limitada a 0–100; sus cambios nativos posteriores se siguen incorporando. Los antiguos
valores personales de hostilidad se importan al crear la relación. Las comparaciones
negativas de confianza de JSON antiguos se aceptan por compatibilidad, pero el valor
nuevo nunca baja de cero.

`social.decaimiento_por_1200_ticks`: miedo=2 y desesperacion=1 por minuto de juego,
hacia su base; hostilidad actual=0,1 y personal=0,2 hacia cero; confianza=0,01 hacia
su valor inicial de relación. Se usan fracciones, conservadas en NBT. Cálculo cada
6 ticks y al consultar/aplicar efectos. Guardado periódico cada 200 ticks; los eventos
y efectos se guardan inmediatamente en NBT. Un chunk descargado recupera el decaimiento
según el tiempo de juego transcurrido, sin forzar su carga. Mundo cerrado: no corre reloj.

`peso_grave=75`, `marca_grave_ticks=168000`, `factor_decaimiento_grave=0.1`:
los recuerdos graves ralentizan al 10 % el decaimiento de confianza/hostilidad personal
por su duración. Las ventanas superpuestas se cuentan una sola vez. Miedo/desesperacion
continúan volviendo a su base normalmente. Los recuerdos importantes se retienen por
peso aunque caduque su protección temporal. `limite_eventos=20` por NPC y UUID: se
elimina primero el menor peso y, en empate, el más antiguo.

Tipos de memoria técnicos (ASCII): `ayudo`, `ofrecio_ayuda`, `mintio_y_descubierto`,
`amenazo`, `ataco`, `mato_aliado`, `dejo_ir`, `ignoro`, `otro`.
Cada registro tiene tipo, texto (hasta 120 caracteres), at (tick) y peso (0–100).
`social.tipos_memoria` define pesos y efectos seguros de hechos externos como un ataque
o intimidación. Los efectos explícitos de una respuesta JSON se aplican una sola vez:
guardar su evento no vuelve a sumar sus medidores. No hay comprobación libre de mentiras
del jugador: el guion local define cuándo se registra mintio_y_descubierto.

`evento_memoria` conserva cadenas antiguas y añade forma explícita:
```json
{"tipo":"ayudo","texto":"Ayudó con medicina.","peso":90}
```
La migración de cadenas usa clasificación conservadora. Descubrir una mentira DEL NPC
no convierte al jugador en mentiroso; un pacto de no atacarse no cuenta como ataque.
Frases antiguas que no se reconocen se conservan con tipo `otro`, sin inventar contexto.

Condición nueva en `requiere` o `condicion`:
```json
{"memoria":{"tipo":["ayudo"],"min_eventos":1,"peso_min":75,
"antiguedad_min_ticks":0,"antiguedad_max_ticks":168000},
"estado_encuentro":["amistoso","neutral"]}
```
Todos los filtros deben cumplirse. Edad mínima/máxima y tipo son opcionales.
`estado` conserva su significado anterior de situación; `estado_encuentro` usa los
siete estados calculados. `social.estados` se evalúa en orden, primera coincidencia:
quebrado, rencoroso, emboscador, desesperado, cauteloso, amistoso, neutral. Cada regla
admite rangos de medidores, memoria y miente; neutral debe ser el respaldo incondicional.
Se recalcula al abrir, aplicar efectos, evaluar condiciones o usar /npcdebug.

`reconocimiento.<voz>`: ayudo, rencor, desconfianza y rechazo_grave. Se usa el recuerdo
significativo más reciente; ayudar requiere peso grave. Son saludos del reencuentro,
no nuevas penalizaciones repetidas cada vez que aparece el jugador. Las agresiones
aplican sus consecuencias al ocurrir. Con hostilidad efectiva >=75 y ataque/muerte de
aliado grave, no se abre conversación: solo amenaza y la IA base queda libre.
El agradecimiento baja el apuntado nativo durante el saludo, sin eliminar agresiones.
No se añade un sistema nuevo de avisos de peligro: se conservan los sensores existentes.

`social.opcion_reconocimiento`: opción y respuestas completas en JSON. Aparece solo
si hay recuerdos, al inicio de un encuentro normal y con un turno posterior disponible.
La respuesta según ayuda usa una condición de memoria. `siguiente:"actual"` se resuelve
al nodo inicial de ese encuentro. Si ya hay cuatro opciones, ocupa el lugar de ofrecer
comida. No se agrega una quinta ni se elimina la retirada. Un guion personalizado con
cuatro opciones sin ofrecer_comida conserva sus opciones; puede dejar un espacio libre
para esta opción. No se agrega durante intimidación.

La identidad, historia privada, validación de variables y revelaciones de .37 siguen
vigentes. No hay servicio externo de IA ni motor de comandos dentro del JSON.
