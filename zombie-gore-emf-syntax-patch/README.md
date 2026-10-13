# TZP Gore 6: corregir sintaxis rechazada por EMF

En el log aportado, EMF informa que `hurt_time*.7` es inválido porque los números no pueden comenzar con punto. El paquete Gore 5 tenía 160 literales de esta forma en sus expresiones personalizadas. Las simulaciones anteriores permitían estos números y no reproducían la restricción de OptiFine activada en el cliente.

Este paquete sustituye esos literales por su forma con cero (`0.7`, `0.04`, etc.) en los cuatro modelos: zombie, husk, drowned y drowned_outer. No cambia las texturas, geometría, estados, umbrales ni el JAR Gore 1.0.5. Los materiales siguen ocultos por defecto hasta que las animaciones puedan activarlos.

## Instalación

1. Mantener el JAR Gore 1.0.5 instalado.
2. Poner `TZP-1.21.1-Gore.6-emf-syntax-fix.zip` en resourcepacks.
3. Activarlo y colocarlo arriba de TZP, Fresh Animations y cualquier otro paquete que cambie los modelos de estos zombis.
4. Desactivar los paquetes Gore anteriores. Reiniciar el cliente o recargar los recursos.

El log muestra Gore 1.0.5 instalado y un paquete activado con el nombre `TZP_1.20.1_2.7.zip`; no permite conocer todo el contenido de ese ZIP. Usar el ZIP nuevo con su nombre propio evita confundirlo con el TZP original.

## Validación

`python build.py`: reproduce y corrige exactamente la expresión rechazada en el log; corrige 160 literales en 1159 líneas de cuatro modelos, verifica integridad ZIP y conserva byte a byte las otras 3958 entradas.

`python check_animations.py`: añade una comprobación que rechaza los literales sin cero como el parser de EMF con sintaxis estricta; analiza las 1159 líneas y simula 3000 frames de poses intactas, fuego sin lesión, heridas, fracturas y distancia.

Esta comprobación reproduce la restricción encontrada en el código fuente de EMF 3.3.11 (`MathExpressionParser.readVariableOrConstant`); no ejecuta el renderer ni el cliente Minecraft. No sustituye una prueba visual en juego.
