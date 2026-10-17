# First Aid 3.6.10 — pestaña Salud contextual

Sustituye el JAR First Aid 3.6.9 por este, sin conservar dos versiones en mods. Conserva tus dependencias y configuración actuales. Esta actualización cambia la interfaz del cliente; no modifica daño, salud máxima, recuperación, red ni los tratamientos del servidor.

Clic izquierdo selecciona parte; clic derecho abre tratamientos. Las opciones grises explican el requisito al pasar el ratón. Los objetos compatibles se buscan en el inventario y se equipan con los paquetes normales de inventario. Clic cancela el tratamiento usando el flujo existente. Escape cierra primero el menú contextual. La rueda desplaza el panel derecho.

Incluye contorno blanco, lesiones reales sobre la silueta, barra vertical de salud con corazón vanilla, estado general/dolor, lista concreta de lesiones y traducciones es_es/es_mx/en_us. El botón inferior de equipar un objeto se elimina de Salud. Las otras pestañas conservan su código.

## Límites reales
El modelo existente tiene ocho partes: cabeza, torso, dos brazos, dos piernas y dos pies. No se inventan manos/antebrazos separados ni un diagnóstico de rasguño. Una pérdida de salud sin diagnóstico se muestra como «Salud reducida». Ejercicio y limpiar herida aparecen desactivados porque no hay mecánicas independientes. El sistema actual entablilla primero la pierna izquierda cuando ambas están rotas; el menú explica esta limitación. La pantalla independiente de tratar compañeros/cadáveres no se modifica.

## Compilar
Java/JDK 21 y Python 3. Desde la carpeta de fuentes:

```
python build.py "ruta/al/firstaid-1.21.1-ZOMBOID-v3.6.9-injured-only-body-care.jar" "firstaid-1.21.1-ZOMBOID-v3.6.10-health-context-ui.jar"
```

El build compila las tres clases nuevas y aplica Patch.java al GuiHealthScreen original con ASM incluido en JDK 21. No necesita descargar dependencias ni alterar las clases clínicas. El JAR original se suministra por separado. `src/` y `Patch.java` contienen el código completo usado para compilar. `reference/` contiene la clase GuiHealthScreen completa reconstruida del bytecode, con los hooks nuevos, para lectura e integración: no es el proyecto fuente original y no se compila con este script. `lang/` contiene todas las claves añadidas; `full-lang/` contiene los JSON completos resultantes.

## Comprobar dentro del juego
En una copia de prueba con permisos de operador:

```
/give @s firstaid:bandage 8
/give @s firstaid:plaster 8
/give @s firstaid:splint 2
/give @s minecraft:shears
/damagePart LEFT_ARM 2
```

Abre Salud, selecciona el brazo, abre el menú derecho y elige Vendar. Comprueba barra, cancelación con clic y consumo del objeto. Sin el objeto, verifica la opción gris y su tooltip. Comprueba vendaje y retirada, heridas de bala con tijeras, y fracturas reales en una copia de prueba. Repite con los objetos en barra rápida, inventario y mano secundaria. Revisa las otras cuatro pestañas y los idiomas español/inglés. Prueba escalas GUI 2, 3 y 4 en las resoluciones que admita Minecraft, y repite tratamientos en LAN/servidor.

## Validación realizada y pendiente
Compilación Java 21; análisis de bytecode ASM; pruebas automáticas de etiquetas, severidad/dolor, geometría, hit testing y límites de salud. Comparación del JAR original: todas las clases originales salvo GuiHealthScreen permanecen idénticas. No se ejecutó Minecraft aquí: renderizado, inventario y tratamientos multijugador requieren la comprobación en juego anterior. No se afirma una validación visual ni multijugador completada.
