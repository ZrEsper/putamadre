# First Aid Zomboid 3.4.6: humedad, gripe y toalla

Parche sobre el JAR suministrado `firstaid-1.21.1-ZOMBOID-v3.4.5-skills-menu.jar`.
Minecraft 1.21.1, NeoForge 21.1.219 o posterior, y las mismas dependencias que la base:
`lrtactical` [0.4.3,0.4.4), `hordes` [1.6.3f,1.6.3g).

[Descargar JAR actualizado](../mods/firstaid-1.21.1-ZOMBOID-v3.4.6-wetness-no-panic.jar) ·
[Descargar código y herramientas del parche](firstaid-v3.4.6-patch-source.zip) ·
[Resultados de validación](VALIDATION.txt).

## Instalar

Reemplaza el First Aid anterior por `firstaid-1.21.1-ZOMBOID-v3.4.6-wetness-no-panic.jar`.
Usa la misma versión en cliente y servidor. No cargues ambos First Aid a la vez.
Mantén las dependencias existentes. El menú de habilidades y los tratamientos se conservan.

## Comportamiento

- Se desactiva el latido y el pánico automático de encuentros/disparos cercanos,
  incluida su supresión, fatiga y contorno claro de pantalla. El inyector conserva
  sus efectos médicos propios y su función de rescate, sin activar ese pánico.
- Contacto con agua moja solo las regiones alcanzadas: cabeza, torso, brazos,
  piernas y pies. Se comprueba la altura real del fluido. Las regiones giran con
  el cuerpo y se orientan horizontalmente al nadar. Es una aproximación por
  volúmenes corporales, no una colisión con cada polígono del modelo.
- Cada región queda al 100 % al tocar agua y se seca gradualmente en 2400 ticks
  fuera del agua: dos minutos a 20 TPS. Volver al agua moja de nuevo esa región.
- Tras 15 segundos de humedad retenida puede aparecer gripe. La probabilidad
  se evalúa cada cinco segundos: 15 % multiplicado por la humedad media corporal.
  No todos los contactos causan gripe y un contacto breve no la causa inmediatamente.
- La gripe dura 2400 ticks desde que aparece; el agua no prolonga ese episodio.
  Hay un único episodio por exposición continua, hasta que el cuerpo se seca.
- Durante la gripe estornudas cada 10–20 segundos. El estornudo emite un sonido
  original y ocho partículas verdes de slime desde la cara. Atrae zombis vivos
  dentro de un radio esférico de 32 bloques, incluidos subtipos de Zombie y mobs
  con ID de zombi. La navegación depende de los caminos disponibles y de la IA
  de cada mod. No atrae todos los animales o monstruos.
- Una toalla se fabrica con **3 `firstaid:cloth`**, en cualquier distribución.
  Clic derecho seca todo el cuerpo. Es reutilizable; no cura instantáneamente una
  gripe que ya empezó. Un jugador inconsciente no puede usarla.
- El panel de temperatura muestra humedad por extremidad y tiempo de gripe,
  incluso sin Homeostatic. El estado de salud también muestra la gripe. La
  temperatura y la hidratación existentes de Homeostatic se conservan cuando
  esa dependencia está disponible.
- Humedad, exposición, gripe e intervalo de estornudo se guardan y sincronizan
  mediante el NBT y el paquete existentes de First Aid. Los guardados anteriores
  comienzan secos. Los temporizadores avanzan en el servidor; creativo y
  espectador quedan excluidos.

## Reproducir

Descarga y extrae [el paquete de código](firstaid-v3.4.6-patch-source.zip).
El directorio extraído `firstaid-patch/` contiene los scripts, fuentes, pruebas
y herramientas. La base está en [mods/](../mods/firstaid-1.21.1-ZOMBOID-v3.4.5-skills-menu.jar).

Necesitas Python 3, Java 21 y ffmpeg con libvorbis. Las herramientas Java
incluidas se verifican con SHA-256 antes de usarlas. No se desactiva TLS ni
verificación de artefactos. Desde el directorio extraído:

```sh
python3 build.py /ruta/firstaid-1.21.1-ZOMBOID-v3.4.5-skills-menu.jar
```

El resultado se escribe en `output/`. Se verifica el hash de la base y se
rechazan versiones diferentes. `src/` contiene toda la lógica añadida;
`tools/PatchFirstAid.java` define las transformaciones concretas de las clases
existentes y genera TowelItem. El sonido y la textura de toalla son originales
y se generan en build.py. Las API de prueba no se incluyen en el mod.

Las comprobaciones ejecutan las clases nuevas reales del JAR con dobles de las
API de Minecraft y NeoForge: temporizadores, agua por región, guardado,
sincronización solicitada, uso del objeto real de toalla, partículas, sonidos,
atracción de zombis y menú. También analizan las pilas y tipos del bytecode de
los métodos afectados, el registro y las anotaciones Mixin. La reconstrucción
verifica que solo cambien las entradas expresamente declaradas.

**No se ha arrancado Minecraft ni el modpack completo en este entorno.** La
prueba final de integración consiste en cargar cliente y servidor con las
dependencias anteriores; fabricar una toalla; entrar en agua a diferentes
alturas; observar los porcentajes en el menú; salir, secarse y comprobar un
episodio de gripe, sus partículas/sonido y los zombis cercanos. Verifica también
que el menú de habilidades y los tratamientos habituales sigan funcionando.

Los cambios sobre First Aid se distribuyen bajo la licencia GPLv3 del mod base.
ECJ y ASM conservan sus propias licencias incluidas en sus JARs.
