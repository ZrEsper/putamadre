# First Aid 3.6.11 — lesiones, ejercicio y HUD

Reemplaza First Aid 3.6.10 (o 3.6.9) en mods. No conserves dos versiones. Para el ejercicio, instala este JAR también en el servidor y conserva Survivor Creator y eZWeight compatibles. El menú de salud sigue disponible sin esas integraciones; el servidor explica si no puede iniciar entrenamiento.

## Salud
La lista derecha muestra todas las zonas lesionadas y omite las sanas. Al abrir, selecciona la primera afectada. Clic izquierdo O derecho en una zona lesionada de la silueta o en su entrada de la lista abre los tratamientos. La rueda desplaza la lista. El esqueleto visual completo sigue visible para localizar las lesiones, pero las zonas sanas no abren tratamientos.

Las opciones de vendaje usan la misma comprobación de salud visible que el tratamiento original: no ofrecen aplicar vendas cuando el sistema médico ya considera la parte completa. Se inicia el tratamiento después de cerrar la ventana, en el siguiente tick, para separar la selección de la entrada del juego. No se cambió la cantidad curada, duración ni coste de los tratamientos. Las restricciones médicas originales de Creativo no se eliminan.

## Ejercicio progresivo
Ejercicio abre tres opciones: flexiones (fuerza), sentadillas (condición física) y detener. Sesiones de 30 segundos, +6 XP de la habilidad correspondiente al completarlas, 12 unidades base de energía por segundo y un pequeño gasto de comida. eZWeight aplica sus reglas de capacidad; Survivor Creator guarda la XP y sus niveles. Hay 10 segundos de descanso tras completar una sesión.

Requiere Supervivencia, estar en el suelo sin montar, comida suficiente, no estar curándose, ni inconsciente, ni tener fracturas o lesiones importantes en las zonas ejercitadas. Se interrumpe al desplazarse más de 0,25 bloques desde el inicio, recibir daño o quedarse sin energía. No concede XP por sesiones interrumpidas. El servidor controla tiempo, validación, gasto y recompensa; no requiere OP. No añade una animación corporal nueva de flexiones/sentadillas: la sesión se indica en la barra de acción.

Comandos alternativos:
```
/firstaidexercise pushups
/firstaidexercise squats
/firstaidexercise stop
```

## HUD
La silueta blanca sustituye al muñeco de colores del modo de HUD «modelo». Se fija cerca de la esquina superior izquierda, conservando la escala fija existente. La silueta se rellena discretamente al haber lesión; corte/pérdida de salud, sangrado, bala, fractura, mordida, infección, vendaje y férula tienen marcas. La información se lee del mismo modelo de lesiones que el menú. La geometría se prepara una vez y los estados se actualizan una vez por tick. Los modos de HUD por corazones o texto conservan su presentación.

## Compilar y probar
Java 21 y Python 3:
```
python build.py "ruta/al/firstaid-1.21.1-ZOMBOID-v3.6.9-injured-only-body-care.jar" "firstaid-1.21.1-ZOMBOID-v3.6.11-injuries-exercise-hud.jar"
```
La base de compilación es v3.6.9, aunque el resultado reemplaza v3.6.10. `src/`, `Patch.java` y `ExtraPatch.java` contienen la implementación completa compilada y las transformaciones ASM reproducibles. `lang/` contiene claves añadidas; `full-lang/`, los JSON finales. Las pruebas usan dobles de Minecraft para verificar el flujo de ejercicio; no sustituyen una partida real.

Dentro de una copia de prueba en Supervivencia, con permisos para preparar heridas:
```
/give @s firstaid:bandage 8
/damagePart LEFT_ARM 2
/damagePart RIGHT_LEG 2
```
Comprueba que ambas partes aparecen sin seleccionar cada una. Abre sus tratamientos con ambos botones, aplica un vendaje y verifica el progreso y la recuperación. Comprueba cancelación, objetos en inventario/barra/mano secundaria, marcadores del HUD y las otras pestañas. Prueba entrenamiento sano, cancelación al moverte y XP al completar una sesión. En LAN o dedicado, comprueba que servidor y clientes usan esta versión.

Validado: compilación, bytecode ASM, filtros de lesiones, geometría del HUD, sesión completa, energía/XP, rechazo de duplicados, descanso, y cancelación por movimiento/daño/agotamiento mediante dobles de prueba. No ejecuté un cliente Minecraft: presentación y flujo real multijugador pendientes de prueba.
