# TaCZ NPCs .44 — contexto de chat y errores observados

Base: fix4 recibido; conserva puertas, abandono, ayudantes y gathering.next.
Sustituir el único JAR de TaCZ NPCs en cliente/servidor, no instalar junto con fix4.

Cambios:
- Al reabrir una conversación, si el historial filtró todas las frases, se reproduce
  el contexto del nodo antes de las respuestas. No se borran identidad ni relaciones.
- /npcreply en una conversación activa vuelve a mostrar el contexto y opciones con
  token nuevo; no renueva su plazo ni aplica efectos de las respuestas. Es un resumen
  de contexto ya escuchado, no una nueva respuesta narrativa. La reanudación de una
  conversación en pausa mantiene el envío pausado de líneas.
- Tres llamadas de CalmBrain recompilada enviaban un array varargs null al intentar
  setTarget(null). Se reemplaza ese argumento por un array con un elemento null.
  Esto aborda los NPE registrados en stop/decisions; no certifica todo el movimiento.
- Si el tronco seleccionado se descarga, se descarta el objetivo y espera sin pedir
  su estado ni forzar carga de chunks. También se comprueba carga del bloque inferior
  durante escaneo de raíces. Estado: madera_chunk_descargado.

Validación: ver pruebas incluidas. Minecraft real NO ejecutado. Los videos muestran
NPCs desplazándose, detenidos y junto a árboles/edificios; no permiten identificar
por sí solos qué tarea los controla. El log confirma /tick rate 10000 y pausas de
conversación: usar /tick rate 20 para probar tiempos de lectura y movimiento.
Pendientes: navegación real, terreno irregular, arbitraje completo de Life79,
pruebas visuales con el modpack. No se afirma haber solucionado todos esos síntomas.

Validación adicional: pasó la ejecución final de las regresiones previas, 9 pruebas
de contexto/token/chunk y 34 de fix4. Una ejecución previa del escenario de refugio
completo falló y otra pasó: esa prueba usa identidad aleatoria y no permite certificar
construcción estable. No se oculta esa limitación ni se considera resuelta la navegación.

Archivos: Chat71.java, Flow73.java y Shelter77.java; Patch80.java aplica cambio mínimo de bytecode
sobre CalmBrain de fix4. Conserva el resto de clases/recursos. Fuentes de compilación
solo para este parche; build.py usa fix4 y las declaraciones/API de la entrega .43.
No se incluyen registros del usuario ni videos.
