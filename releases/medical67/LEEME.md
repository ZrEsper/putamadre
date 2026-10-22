# First Aid .24 — Salud integrada y férula en tres pasos

Reemplaza First Aid .23 por este .24. No dejes ambos JAR en mods. En multijugador, instala esta misma versión en cliente y servidor. En una partida individual basta con reemplazar el JAR de la instancia.

La entrada «Revisarme / Compañero» abre directamente la pestaña Salud del superviviente, con su muñeco. Los tratamientos se seleccionan allí desde las lesiones. «Examinar compañero» queda en el pie de la pestaña y conserva la atención remota existente.

Para una fractura, selecciona la pierna y «Colocar férula». Acierta tres pasos: alinear, colocar y sujetar. Pulsa ESPACIO o haz clic en la barra cuando el indicador esté en verde. Fallar devuelve al primer paso sin gastar la férula. Esc o Cancelar abandona el intento. Solo al completar los tres pasos, confirmados por el servidor, se aplica la férula y se consume una unidad (sin consumo en creativo). La pierna seleccionada se respeta, incluso si ambas están fracturadas. También funciona al usar directamente la férula: abre Salud e inicia la colocación.

Se comprueba que el jugador siga vivo, consciente, con fractura y con la misma férula en la mano antes de cada paso. Cambiarla de mano o reemplazarla invalida el intento. Cada sesión caduca a los 60 segundos y puede iniciarse otra. La cancelación y los mensajes repetidos de finalización no gastan unidades adicionales.

Base: el JAR .23 adjuntado por el usuario coincide exactamente con el SHA-256 del manifiesto de baconwithd/Steph, rama codex/medical60-leg-contact-74f17862. Se conservan byte por byte sus recursos y clases de contacto/animación; esta entrega cambia la entrada al menú y el proceso de colocación de férulas.

Validación: 305 clases con ASM BasicVerifier; 2.305 comprobaciones JVM con APIs de prueba. Incluyen los 16 códigos del paquete y su codec real, registro bidireccional del handler, las 2.197 combinaciones de tres pulsaciones, reinicios, cancelación, consumo, lados, creativo, objeto cambiado, muerte/inconsciencia/caducidad y render del panel en cuatro tamaños. Las firmas del registro de red se contrastaron con el código de NeoForge 1.21.1.

No se ha iniciado Minecraft con el modpack completo. Estas comprobaciones no prueban todos sus mixins, modelos y mods de interfaz en ejecución.
