Resultados automáticos: build-output.txt, test-output.txt, config-output.txt en fuentes-pruebas.zip.
APIs simuladas, no ejecución de Minecraft. La navegación del constructor se simula;
los trayectos reales y la integración visual/térmica siguen pendientes.

| Caso en Minecraft pendiente | Procedimiento | Resultado esperado |
|---|---|---|
| Ritmo | Hablar y elegir respuesta de inmediato | 1,5 s mínimo entre líneas y antes de la contestación; opciones esperan tras la última línea |
| Lectura | Completar tres turnos | Cada bloque abre línea, NPC, opciones, jugador y cierre; no se acumulan opciones viejas |
| Colores | Regalar, pedir, intimidar y hacer trueque | Verde/celeste/rojo/dorado; respuestas en negrita, hover conserva formato |
| Relación | Escuchar tema nuevo o dar regalo aceptado | Aviso verde en mayúsculas/negrita y sonido XP, sin dar XP real |
| Traición | Ganar confianza alta y atacar | Reacción propia de la voz, penalización y defensa normal; no se congela |
| Confusión/cariño | Relación alta o señales contradictorias | Respuestas contextuales, sin repetirlas en el mismo encuentro |
| Solo | NPC libre al aire libre, bosque cercano, frío o noche, terreno plano | Recoge madera, fabrica y coloca una mesa, construye interior 2x2 y se refugia |
| Grupo | Dos NPCs compatibles próximos sin dueño | Un plan compartido 4x4; aportes reales de madera, una mesa |
| Recursos | Dar pocos materiales, observar bolsa y árboles | Solo construye al disponer de materiales; cada colocación consume una unidad |
| Interrupción | Hablar, acercar amenaza o atacar durante obra | Conversación/defensa prevalecen; obra retoma después si es posible |
| Protección | mobGriefing false o claim ajeno | No destruye ni construye; diagnóstico explica bloqueo |
| Obstáculos | Ocupar la siguiente pieza o cancelar evento de colocación | No sobrescribe la estructura, no coloca sobre entidades ni pierde el material |
| Persistencia | Guardar y reiniciar durante obra | Recupera plan/avance y evita una segunda mesa/refugio |
| SereneSeasons | Cambiar estación en bioma templado | El sensor usa temperatura estacional, no una estación fija inventada |
| Homeostatic | Entrar bajo techo terminado en frío/calor | Comprobar cobertura real y efecto térmico del mod; no se promete inmunidad |
| Rendimiento | Muchos NPCs, terreno sin madera | Comprobaciones escalonadas; pausa de reintento, sin escaneo sin límite ni chunkload |

Limitaciones de esta entrega: planos fijos, acceso abierto, sin calentador/AC ni fisiología
completa del NPC. La simulación acredita lógica e inventarios, no pathfinding de Minecraft.
