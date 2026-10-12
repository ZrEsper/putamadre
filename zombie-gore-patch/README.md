# Zomboid Zombie Injuries 1.0.0

## Instalación

- Coloca `mods/zomboid-zombie-gore-1.0.0-neoforge-1.21.1.jar` en `mods` del cliente y servidor, con NeoForge 21.1 y Minecraft 1.21.1.
- Coloca `TZP-1.21.1-Zomboid-Gore.1.zip` en `resourcepacks` y actívalo por encima de otros packs que cambien los modelos de zombis. Sustituye el TZP anterior; conserva sus texturas originales.
- Conserva Entity Model Features 3.3.11 y Entity Texture Features 7.2.5. No se modifican sus JAR.
- Fresh Animations puede quedar debajo para los demás mobs. Las poses de estos zombis son una adaptación compacta inspirada en FreshLX: respiración, balanceo, apoyo de pies y reacción al daño. No incluyen todas las extensiones ni expresiones faciales de Fresh Animations.

## Comportamiento

Ocho zonas registran heridas y pérdida de partes: cráneo, mandíbula, ambos brazos, ambas piernas y dos mitades del torso. Se conserva el estado al guardar. Los disparos de TACZ usan su posición de impacto y tipo de arma: pistolas dejan marcas; escopetas pueden desprender partes; francotiradores dejan entrada/salida y tienen al menos dos penetraciones. La pérdida requiere daño suficiente: no todos los disparos desprenden algo.

Sin mandíbula, FirstAid no registra mordedura, pero sigue existiendo daño por arañazos. Sin brazos disminuye el daño; con daño de cráneo el andar se vuelve torpe; sin una pierna arrastra el cuerpo. El fuego oscurece progresivamente el cuerpo y activa movimiento agitado sin cambiar su objetivo de persecución. Las balas añaden un impulso horizontal pequeño, una vez por tick como máximo.

Los modelos cubren zombi normal, husk y drowned, con cinco variantes de proporciones y muchas texturas del TZP original. Los modelos propios de infectados especiales y zombis aldeanos no tienen anatomía visual adaptada en esta versión. Los fragmentos son efectos temporales del modelo; no generan entidades físicas ni objetos looteables.

## Optimización y comprobación

Un entero sincronizado por zombi almacena el estado visual. El estado se envía al cambiar; el fuego se revisa cada cinco ticks. No se buscan otros mobs ni se crean modelos nuevos por cada impacto. La matemática principal se reutiliza dentro del modelo; a más de 24 bloques se desactivan respiración, reacciones secundarias, heridas pequeñas, muñones y fragmentos. Los modelos completos todavía se dibujan: no sustituye la optimización general de hordas.

Validación: 23 pruebas de reglas, 18 de flujo con dobles de las APIs y 91 métodos verificados con ASM. Se cotejaron los descriptores reales de TACZ y FirstAid. Se comprobó la integridad de ambos ZIP/JAR y conservación de las texturas originales. **No se ejecutó Minecraft: aspecto, carga de EMF, comportamiento real y rendimiento con hordas quedan pendientes de prueba en el juego.**

Para probar: dispara con diferentes armas a un zombi de mucha vida, préndelo con un mechero y compara las animaciones de cerca y lejos. Los nombres `Gore1` a `Gore5` seleccionan las cinco variantes; el resto se elige aleatoriamente.

## Reconstrucción

Java 21, ECJ y ASM, Python 3 y Pillow. En la raíz del repositorio:

```sh
python zombie-gore-patch/build.py /ruta/a/herramientas /ruta/a/TZP_1.20.1_2.7.zip
```

`stubs` son dobles únicamente para compilar/probar; no se empaquetan. Texturas originales: Tissou. Referencia de animación: Fresh Animations y FA All Extensions de FreshLX, aportados por el usuario. Las texturas nuevas de heridas y quemaduras y los modelos de lesiones se generan en `models.py`.
