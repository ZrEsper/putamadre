# Planks correctas: tres tablones -> seis refuerzos

El bloque de tres tablas rugosas se llama `doomsday_decoration:board` (Planks). Las recetas anteriores apuntaban a los bloques decorativos `planking_*`; no habia receta para `board`.

Ahora una fila horizontal de tres ingredientes del tag `minecraft:planks` produce seis `board`. La receta se desbloquea al conseguir tablones y permite combinar maderas. Se conserva su modelo/textura original. Nombre en espanol: Tablas de refuerzo; ingles: Planks; aleman: Verstärkungsbretter.

`Boards.near` reconoce el bloque correcto: duplica la resistencia de ventanas y puertas contiguas, incluyendo ambas mitades de puertas. La velocidad del jugador se reduce a la mitad y el puente nativo de Hordes .44 duplica el tiempo de asedio. No acumula multiplicadores al poner mas refuerzos ni carga chunks. Se mantiene el soporte anterior a los `planking_*`. El propio refuerzo tiene dureza 6/resistencia a explosion 12. Camas, aires, almacenamiento e inventarios se conservan.

Construir: `python doomsday-board5/build.py`. Probar: `python doomsday-board5/test.py`. 30 comprobaciones sobre los helpers reales, incluido el puente de Hordes .44, camas sin cambiar respawn y velocidad de rotura; validacion de receta, desbloqueo y modelos/texturas. APIs simuladas; partida/JEI pendientes. [Instalacion conjunta](../iso-audio31/GUIA.txt).
