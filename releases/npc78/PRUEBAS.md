Todas las pruebas de Minecraft de esta tabla están pendientes; son instrucciones para comprobar .42.
Los resultados automáticos corresponden a JVM con APIs simuladas, no a una sesión de Minecraft.

| Caso | Procedimiento en juego | Resultado esperado |
|---|---|---|
| Error de refugio | NPC al aire libre con clima adverso/noche, terreno libre | No aparece BlockPos.containing; busca cobertura o inicia plan |
| Seguir | Confianza >=25, Objetivos -> compartir camino -> Ven conmigo | Sigue físicamente, figura aliado, no exige facción |
| Esperar/reanudar | Ordenar detenerse y después volver a seguir | Orden persistente, sin caminar detrás al esperar |
| Propietario | Segundo jugador intenta darle órdenes | No puede controlar la escolta ajena |
| Liberar | Seguir por tu cuenta | Recupera autonomía sin perder inventario |
| Presentación | Preguntar nombre y volver a hablar/reiniciar | No vuelve a ofrecer preguntar el nombre conocido |
| Privacidad | Pedir familia con confianza baja y luego alta | Rechaza al principio; retira pregunta solo tras revelarla |
| Objetivos | Cambiar comida/salud/actividad y preguntar qué necesita | Respuesta corresponde al estado real, sin medicina fija |
| Chat NPC/NPC | Dos NPCs compatibles, próximos, sin peligro | Charla pausada visible como ENTRE SUPERVIVIENTES; sin opciones del jugador |
| Interrumpir charla | Amenaza o interacción del jugador | Cancela charla entre NPCs y respeta defensa o charla del jugador |
| Compartir | Uno sin comida y otro con raciones sobrantes | Transferencia real de una unidad, sin duplicar |
| Puerta | Seguimiento a través de puerta de madera | Ruta con apertura de puerta; no queda empujando cerrada |
| Ventana | Jugador detrás de cristal sin acceso | Rechaza ruta incompleta; no atraviesa cristal ni gira indefinidamente hacia él |
| Calma | Observar NPC sin peligro | Sin escanear tácticamente cada esquina ni apuntar por cualquier paso |
| Combate | Ruido de disparos/ataque/enemigo real | Conserva alerta, puntería y defensa nativa |
| Depósito | Base cubierta, madera disponible, NPC armado y sano | Usa o fabrica/coloca cofre consumiendo ocho tablones |
| Caza | Zombi cercano, base y depósito disponibles | Selecciona objetivo, combate con IA nativa, recoge y vuelve |
| Cadáver | Zombi con botín en cadáver compatible | Llama al adaptador existente y después regresa; verificar con ragdoll/remains del modpack |
| Botín | Objetos tirados tras muerte y cofre con espacio | Recoge stacks reales, conserva componentes y deposita excedente |
| Cofre lleno | Llenar el depósito | Conserva lo que no cabe; no destruye ni duplica el botín |
| Persistencia | Guardar/reiniciar con compañero y base | Mantiene orden, dueño, historial, base y depósito |
| Configuración | /npcvalidate y /npcreload | Valida vida y mantiene memoria; invalida tokens previos |

No se promete solución de todo atasco en todos los bloques modificados. Sin reemplazo del
motor de rutas, minería subterránea nueva ni sociedades completas. La integración real de
combate, saqueo con mods de cadáveres y clima todavía requiere probar Minecraft.
