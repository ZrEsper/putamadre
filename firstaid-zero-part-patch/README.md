# First Aid v3.4.9 — vida general, partes agotadas y reanimación

Parche sobre `firstaid-1.21.1-ZOMBOID-v3.4.8-bandages-flu-head.jar` enviado por el usuario. Conserva los sistemas de vendajes, infecciones, gripe, sueño, medicina, objetos y perfiles. Se verifican en `validation.json` los SHA-256 y los archivos modificados. Se conservan todos los mixins originales byte por byte.

## Instalar

Cerrar Minecraft y servidor. Reemplazar el JAR v3.4.8 por `firstaid-1.21.1-ZOMBOID-v3.4.9-global-health-revival.jar`, sin duplicar First Aid, en clientes y servidor. Conservar las dependencias y Survivor Creator: se usa su nivel de Fitness para mejorar la auto-reanimación. Si Survivor Creator no está disponible, la probabilidad queda en 25 %.

## Vida y partes del cuerpo

La vida general se calcula como `suma de vida actual / suma de vida máxima de las partes × vida máxima del jugador`. Se ignora la selección anterior que podía tomar solo las partes críticas. Agotar cabeza, torso o una extremidad no mata ni provoca el coma crítico de antes si aún queda vida en otras partes.

Cuando una parte llega a 0 queda una penalización propia hasta que esa parte alcance al menos 1 punto, aunque primero recupere una fracción. Se guarda la máscara de partes afectadas en los datos persistentes del jugador. Las penalizaciones se aplican mediante modificadores transitorios propios de atributos y se retiran sin borrar efectos o modificadores de otros mods.

| Parte agotada | Penalización adicional |
|---|---|
| Cabeza | −35 % velocidad de ataque |
| Torso | −20 % movimiento y −15 % velocidad de ataque |
| Cada brazo | −25 % daño de ataque |
| Cada pierna | −25 % movimiento |
| Cada pie | −15 % movimiento |

El conjunto de penalizaciones de movimiento se limita a −70 %. Son adicionales a las lesiones normales del mod; curar el estado de 0 no elimina otras enfermedades, heridas ni efectos ajenos. Se limpian estados críticos antiguos cuando no corresponden al agotamiento de la vida general.

Se corrigen también los caminos de muerte inmediata por cabeza agotada en ClinicalHooks (falta de sueño) y TreatmentV33 (heridas infectadas/vendajes sucios), para que respeten la vida general y el estado de reanimación.

## Reanimación

En el 95 % de las caídas por daño normal al agotarse la vida general se entra en estado derribado durante 2 minutos. El 5 % restante conserva la muerte definitiva. Los comandos de muerte y el vacío conservan su comportamiento; rendirse, ser ejecutado mediante el sistema original o agotar la cuenta atrás también pueden causar muerte definitiva.

Para mantener al jugador técnicamente vivo mientras está derribado, First Aid conserva 1 punto de vida vanilla como reserva interna. No restaura las partes: siguen en 0, y la interfaz lo presenta como derribado. Los controles, inmovilización, ayuda de compañeros, desfibrilador y opciones de tratamiento originales se conservan mediante el estado crítico/reanimable nativo. La muerte definitiva sucede después de este estado, no tras agotar una sola parte.

Hay **una sola tirada de auto-reanimación por caída**. A los 10 segundos, si la tirada tuvo éxito, se realiza la reanimación nativa sin asistencia y se restauran las partes agotadas a 1. Si falla, continúa el tiempo para recibir ayuda. Salir y entrar no vuelve a tirar: los indicadores se guardan en datos persistentes y el estado/timer nativo del modelo se serializa.

Probabilidad: `25 % + 2 puntos porcentuales × Fitness`, con Fitness limitado a 0–10. Nivel 0: 25 %; nivel 5: 35 %; nivel 10: 45 %. La tirada y el nivel se calculan en el servidor; el cliente muestra la probabilidad enviada mediante el motivo del estado sincronizado.

El panel añadido muestra estado, tiempo restante, barra, probabilidad y espera para la auto-reanimación; tras fallar indica que hace falta asistencia. Muestra la tecla de rendirse asignada actualmente (mantener 3 segundos) y conserva las barras de ayuda/reanimación nativas.

## Validación

47 casos simulados de salud/partes/penalizaciones + 14 casos del ciclo de reanimación: cabeza/torso a 0, cuerpo completo agotado, recuperación parcial hasta 1, reducción de atributos, estado derribado, límite de probabilidad, éxito/fallo de auto-reanimación, una sola tirada, Fitness 10, muerte definitiva y ausencia de tiradas en el cliente. ASM verifica las clases modificadas y todas las nuevas; integridad ZIP y conservación de entradas ajenas comprobadas. No se empaquetan los dobles API de pruebas.

**No se ejecutó Minecraft ni el modpack real.** Falta comprobar efectos de atributos, sincronización del estado, interfaz, reanimación entre jugadores y compatibilidad con PlayerRevive si se instala. Las pruebas simuladas no sustituyen esas pruebas de juego.

Reconstruir: `python -B firstaid-zero-part-patch/build.py RUTA_V3.4.8.jar CARPETA_ECJ_ASM` con Java 21. La modificación conserva las versiones internas del mod para respetar las dependencias existentes.
