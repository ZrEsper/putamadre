# Chavetazo corporal v12

Reemplaza el JAR v11 por `zomboid-global-melee-animations-2.3.1-range-plus-one-ezweight-v12-body-shove.jar`; no dejes ambas versiones.

Añade al jugador local una pose corporal de un segundo: ambos brazos avanzan y el torso acompaña, con entrada y retorno suaves. Se mezcla sobre la pose normal del modelo humanoide, por lo que se puede representar en tercera persona e isométrica. La animación corporal se activa cuando se acepta la solicitud local de chavetazo y no depende de la animación nativa del arma. Conserva los renderizadores de primera persona y las reglas de alcance, daño, stamina y cooldown del v11.

En cámara isométrica calcula el giro hacia el punto del cursor y envía la rotación al servidor antes del paquete del chavetazo. Fuera del modo isométrico conserva el apuntado normal. El mod de cámara es opcional. No abre un inventario ni altera el estado de WATUT: la pose añadida es propia. Esta entrega no sincroniza la pose corporal con otros jugadores; el golpe y su dirección siguen siendo procesados por el servidor.

Pruebas: duración de un segundo, 1.001 muestras de mezcla, brazos y torso, aislamiento de otras entidades, vuelta a la pose normal, orientación al cursor y orden del envío de rotación. Verificación del bytecode: 36 clases y 168 métodos. No se ha ejecutado Minecraft.
