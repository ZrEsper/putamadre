# Higiene de cadáveres .14

[Descarga directa junto con los otros tres reemplazos](../distribuciones/Zombies-Poldo-Torretas-Inconsciente-Digital-Higiene.zip). [Guía sencilla](../recovery-digital6/GUIA.txt).

Parte exactamente del Remains .13 enviado, incluso su último reenvío (SHA256 b597d93ed7c5f10ff4c5cee57a1ef0e622111bb9341db0638eb7e21e34f8f777). Conserva byte por byte las clases de loot, equipo militar, NPC, chatarra y exclusión de zombis de jugador. Añade registro de contenido y una llamada en el tick de limpieza existente.

Moscas a los 1.200 ticks de edad; dos cuerpos dentro de una esfera de radio 6 producen náusea a los 1.200 ticks de exposición y vómito a los 1.800. Repite a los 1.200 ticks. Tras activarse, un cuerpo mantiene la enfermedad; cero cuerpos durante 200 ticks reinicia el estado. Cada vómito resta 4 puntos de comida y agua, usando Homeostatic prioritariamente y TAN como alternativa. El audio enviado se convierte a mono para sonido posicional. La mancha es un SnowLayerBlock verde sin ticks de derretimiento, blando, sin colisión ni drop.

El servidor procesa una vez por segundo y consulta solo registros de bloques cercanos cargados, sin recorrer todo el registro mundial. Las moscas se limitan a 24 cuerpos maduros por mundo/segundo y se comparten entre observadores; ese límite no afecta la enfermedad. No se borran cuerpos ni inventarios. La náusea ajena más fuerte o de mayor duración se respeta.

Reproducir: `python corpse-hygiene14/build.py` y `python corpse-hygiene14/test.py` desde raíz, con Java 21, Python 3 y ffmpeg. Inputs y herramientas fijados por hash. Los stubs api/ no se empaquetan. 540 controles ejecutan la política y adaptador de servidor contra APIs simuladas, además de comprobar recursos y clases conservadas. La colocación, partículas, sonido y convivencia real con el modpack siguen pendientes de prueba dentro de Minecraft.
