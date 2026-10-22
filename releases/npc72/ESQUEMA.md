# JSON de diálogos — schema 2

Se mantiene `config/tacznpcs/dialogue-phase2.json`. El lector acepta schema 1 y 2.
El archivo incluido en el JAR es un ejemplo completo y validado; se puede extraer para editarlo.
No se ejecutan comandos de datos. Los campos desconocidos en encuentros, opciones,
respuestas, condiciones y efectos se rechazan y activan el respaldo.

## Estructura

- `voces[]`: `id`, `nombre`, `descripcion`, `rasgos[]`, `muletillas[]`, `evita[]`,
  `ejemplos[]`, `groserias_nivel` (0-3), `respaldo`, `cierre_repeticion[]` (2-4 líneas).
- `historias`: plantillas con `puede_mentir`, `miente`, `datos`.
  No se permite `miente=true` si `puede_mentir=false`.
- `encuentros[]`: `id`, `voz`, `situacion`, `condiciones` opcional, `peso` (1-100),
  `historia`, `inicio`, `turnos_max` (3-5), `nodos[]`.
- `nodos[]`: `id`, `lineas_npc[]` (2-4), `opciones[]` (2-4), `reaccion_silencio`.
- `opciones[]`: `id`, `texto`, `tono`, `requiere` opcional, `respuestas[]`.
  Cada nodo conserva al menos una opción sin `requiere`.
- `respuestas[]`: `condicion` opcional, `lineas_npc[]`, `efectos`, `siguiente`.
  Se evalúan en orden y gana la primera condición válida. La última es obligatoriamente
  incondicional. `siguiente=null` exige un resultado final. Un resultado exige `siguiente=null`.
- `reaccion_silencio`: `lineas[]` (2-4), `efectos` con resultado final obligatorio.

Las líneas de una respuesta son el turno que lleva a `siguiente`: sustituyen el texto
inicial de ese nodo, no se concatenan con él. Sus opciones aparecen al terminar las líneas.
Así no se envían dos turnos seguidos por cada clic. Las salidas tempranas son válidas.

## Condiciones

Ejemplo de `requiere` o `condicion`:

```json
{
  "inventario": [{"categoria": "medicina", "cantidad": 1}],
  "medidores": {
    "confianza": {"min": 30, "max": 80},
    "miedo": {"max": 70},
    "desesperacion": {"min": 20},
    "hostilidad": {"max": 60}
  },
  "voz": ["cansado"],
  "estado": ["tension"],
  "miente": false,
  "revelado": ["familia"]
}
```

Todas las condiciones presentes deben cumplirse. Se puede usar `objeto` en lugar de
`categoria`, por ejemplo `{"objeto":"minecraft:bread","cantidad":1}`. Nunca ambos.
`inventario` consulta el del jugador, también si el objeto no está en la mano.
Los requisitos y los efectos se comprueban al mostrar la opción Y al elegirla.
`estado` identifica `situacion` del encuentro activo. Los iniciadores naturales actuales
son encuentro/tension/necesidad; cualquiera sirve como respaldo. Los identificadores
personalizados se pueden probar con /npcexample, pero no añaden detectores de situaciones.
Cada voz debe tener un respaldo sin condiciones que cubra los tres iniciadores actuales.

## Efectos

```json
{
  "confianza": 30,
  "desesperacion": -25,
  "quita_item": {"categoria": "medicina", "cantidad": 1},
  "evento_memoria": "recibió medicina para su familiar",
  "revela": ["familia", "profesion_pasada"],
  "resultado": "acuerdo"
}
```

Los únicos campos admitidos son confianza, miedo, desesperacion, hostilidad,
evento_memoria, revela, da_item, quita_item, resultado. Los deltas son enteros -30..30.
Confianza se limita a -100..100; los otros a 0..100. Las ganancias gratuitas repetidas
por la misma opción no suben confianza de nuevo hasta un día de juego después.
Las entregas reales no tienen esa restricción: cada una paga un ítem real.

`da_item` y `quita_item` usan objeto/categoria y cantidad 1-64. Son transferencias entre
inventarios reales, conservan componentes, simulan capacidad antes de escribir y retiran
el token antes de aplicar efectos. No crean ítems ni los dejan caer por falta de espacio.

Resultados: acuerdo, tregua, separacion_pacifica, ruptura_combate, huida, traicion.
Revelaciones: nombre, familia, profesion_pasada, necesidad_actual, objeto_personal,
lugar_hogar, companero, mentira. Se guardan por NPC y jugador. `nombre` mantiene la
presentación de fase 2; antes de eso el encabezado usa ???.

## Variables y privacidad

Solo se admiten: `{player}` `{name}` `{familiar_nombre}` `{familiar_rol}`
`{objeto_personal}` `{lugar_hogar}` `{profesion_pasada}` `{companero_nombre}`.
Cualquier otra variable o llave mal formada invalida el JSON.

Datos de familia, hogar y profesión requieren confianza media (30 por defecto) y una
revelación explícita. Objeto personal/compañero requieren confianza alta (60). Si una
respuesta no alcanza la confianza necesaria para sus revelaciones, no se ofrece esa opción.
Si la confianza cae después, el formateador tampoco expone el valor privado.
Usa variables para datos privados; el lector no puede reconocer nombres escritos a mano.
El marcador antiguo `{intro}` solo se expande al importar schema 1; no es válido en schema 2.

Cada línea tiene hasta 200 caracteres, incluida la expansión al enviarse. Las historias
se guardan al primer uso para no cambiar los datos ya contados por ese NPC. Editar una
plantilla afecta a historias nuevas. No se generan familiares como entidades en esta fase.

## Configuración

- `pausa_lineas_ticks`: 30 por defecto, rango 10-100.
- `confianza_media`: 30; `confianza_alta`: 60.
- `tregua_ticks`: 1200; `salida_segura_ticks`: 200.
- Los 12 s de silencio siguen en `tacznpcs-decision-phase1.properties` (`silenceTicks=240`).

Reinicia para recargar el JSON. `categorias` son listas explícitas de IDs; puedes añadir
comida o suministros de otros mods. No existe ejecución de Lua, JavaScript ni comandos.
