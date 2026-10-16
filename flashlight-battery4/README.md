# Flashlight .4 y SurvivorCreator .34

[Descargar ZIP](../distribuciones/Zombies-Poldo-Linternas-Baterias-4.zip) · [Guía y recetas](GUIA.txt)

Parche reproducible sobre los dos JAR adjuntos. Corrige la búsqueda del ítem inexistente `flashlight:flashlight_on` en el equipo inicial, usando el identificador real `flashlight:flashlight`. Un personaje nuevo recibe una linterna encendida con 12000 ticks de carga. No se recarga al iniciar sesión.

La batería se guarda en `CUSTOM_DATA` de cada ItemStack; el servidor consume un tick de carga por tick activo sostenido. La transformación entre ítem encendido/apagado copia componentes completos. Se corrige también el procedimiento nativo que podía modificar la otra mano al pulsar. Se conserva la colocación segura del haz original: no reemplaza bloques sólidos. La batería original se fabrica con cobre, redstone y lapislázuli y se consume manualmente desde inventario. No se recargan gratuitamente objetos fabricados.

Se registran seis nuevos ítems (tres variantes con sus estados), mediante el DeferredRegister nativo antes de su registro. Expedición: 20 min/25 bloques; económica: 30 min/12 bloques; táctica: 10 min/40 bloques; básica: 10 min/25 bloques. Se reutiliza el modelo original. Barra y tooltip de tiempo restante, avisos traducidos, recetas y desbloqueos del recetario. Variantes en la pestaña del mod y herramientas. El alcance nativo reconoce los nuevos ítems y elige el mayor alcance entre manos activas.

SurvivorCreator conserva byte a byte todas las clases y recursos anteriores salvo `spawn/Flashlight.class`; el puente nuevo llama al equipo inicial mediante reflexión opcional. No se alteran profesiones, pantalla de personaje, configuraciones ni curación por nivel. Se conservan texturas, sonidos, bloques, adjuntos y SafeLight del JAR base. Farol, casco de luz y gafas nativos quedan fuera del nuevo sistema de baterías.

Desde la raíz, con Python 3 y Java 21:

```
python flashlight-battery4/build.py
python flashlight-battery4/test.py
python flashlight-battery4/package.py
```

Pruebas sobre los helpers de producción y dobles de Minecraft: 78 comprobaciones de carga, duración exacta de las cuatro variantes, autoridad de servidor, controles, transferencia de objetos y equipo inicial; nueve comprobaciones de hooks binarios. ASM verifica todos los métodos de salida. No se ejecutó Minecraft; las pruebas no verifican el renderizado ni un inicio real de NeoForge.
