Pruebas en Minecraft pendientes: esta tabla indica cómo comprobar la .40 en juego.

| Caso | Cómo probar | Resultado esperado |
|---|---|---|
| Confianza | Preguntar varios temas, repetir uno; /npcdebug | +2 por tema nuevo escuchado, repetición sin premio; límite 12/día |
| Regalos | Ayuda/apoyo -> regalar; dar comida tres veces | Consume objetos reales; +3/+1/+0; máximo 18/día |
| Historia negativa | Agredir e intentar regalar | Rechaza regalar; conserva recuerdo |
| Registro | Intimidar un NPC que ceda, revisar bolsa | Objetos/cantidades reales; una unidad por clic; no duplicación |
| Distancia | Alejarse y usar una opción de registro vieja | Se rechaza, sin pérdida/creación de objetos |
| Giros | Rodear al NPC durante una charla, cruzar su espalda | Cuerpo acompaña; cuello no da vueltas completas; transiciones suaves |
| Visitas | NPC neutral con comida/medicina sobrante y jugador desarmado | Puede acercarse y proponer trueque; no insiste tras rechazar |
| Trueque | Aceptar con el recurso pedido; repetir clic viejo | Intercambio real de una unidad por parte; token viejo inválido |
| Cancelación | Aparece amenaza durante acercamiento | Abandona visita y recupera su IA normal |
| Emboscada | Encuentro familiar herido/compañeros ocultos en terreno con cobertura | Dos compañeros reales; sin error Player.getSensing ni duplicados |
| Persistencia | Guardar y reiniciar mundo | Mantiene identidad, relaciones y contadores del día |
| Config | /npcvalidate y /npcreload | Valida JSON; invalida sesiones, mantiene datos persistentes |

Resultado automatizado: build-output.txt, test-output.txt y config-output.txt incluidos
en fuentes-pruebas.zip. Son pruebas JVM simuladas; no son pruebas de Minecraft.
Limitaciones: registro en chat solo de bolsa, no equipo; intercambios de suministros,
no economía general. Eventos solo dentro de entidades/chunks activos; aleatoriedad no
garantiza una visita inmediata. Sin prueba visual GPU de las animaciones.
