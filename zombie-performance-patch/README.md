# Hordes 19, Gore 1.0.3 y pack de lesiones

## Instalación

Reemplaza Hordes 18 por `zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.19-cached-path-retries.jar` y Gore 1.0.2 por `zomboid-zombie-gore-1.0.3-neoforge-1.21.1-cached-corpse-drops.jar`, tanto en cliente como servidor. No instales dos versiones del mismo mod.

Activa `pack/TZP-1.21.1-Gore.2-cached-injuries.zip` por encima de Fresh Animations y de otros modelos de zombis; sustituye el TZP conectado anterior. Conserva ETF y EMF. Las texturas y sonidos originales se preservan. Los modelos de lesión cubren zombis normales, husks y drowned; no reemplazan la anatomía de mutantes de otros mods. Las variables visuales coinciden con el Gore 1.0.2 recibido.

## Cambios

- Conserva los demás archivos de los JAR suministrados, incluyendo las reacciones de TACZ y melee de Gore 1.0.2.
- Las clases y los miembros usados por los helpers de reflexión se reutilizan. Evita generar claves con streams y nombres de tipos en cada llamada.
- Una ruta terminada no puede provocar otra petición inmediata: mínimo 20 ticks tras éxito y 40 tras fallo, incluso si cambia el destino. Conserva el máximo de cuatro solicitudes por mundo y tick, y distribuye las esperas de renovación con un desfase por navegación. La IA vanilla ajena a ese helper no cambia.
- Las flags de cada zona de lesión se calculan una vez por modelo. Las marcas son más visibles y los detalles secundarios se limitan a 16 bloques. No crea entidades físicas para los fragmentos.

## Objetos cercanos a cadáveres

Cada segundo en el servidor se revisan como máximo ocho entidades Corpse y ocho o más entradas de cuerpos de Zombie Remains (máximo total 16 consultas). El recorrido del mapa de cuerpos examina como máximo 64 entradas por pasada y se retoma donde quedó. Solo examina chunks ya cargados.

La recogida se pausa mientras algún jugador del mundo tenga abierto un menú de contenedor, para evitar modificar un inventario que una interfaz puede estar copiando.

Los objetos deben llevar al menos cinco segundos en el mundo, estar a tres bloques o menos, con diferencia vertical de hasta 1,5 bloques y línea de interacción sin pared intermedia. Cada consulta procesa como máximo 32 objetos. Los stacks se copian conservando componentes y se combinan solo cuando coinciden; el objeto del suelo se elimina únicamente cuando ya se transfirió entero. Si una transferencia es parcial, queda el resto en el suelo. No recoge orbes de experiencia.

**Corpse:** primero llena los slots principales, luego combina objetos en `Death.getAdditionalItems`; añade slots adicionales solo si falta espacio. Ese inventario adicional utiliza la persistencia nativa de Corpse. No sustituye su GUI.

**Zombie Remains (cuerpos que son bloques):** llena su contenedor actual y marca sus datos para guardar. **La ampliación de estos cuerpos queda pendiente del JAR exacto `zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.3.jar`.** Su sistema de carga y menú deben adaptarse juntos: agrandar únicamente el contenedor puede perder slots al recargar. Por ahora los sobrantes quedan fuera. La adaptación usa los nombres de la API de Zombie Remains conocida; ante incompatibilidad registra un aviso y deja los objetos sin transferir.

## Evidencia y límites

El perfil recibido registra solo `Server thread`, durante unos 60 segundos. Había 237 zombis normales, 974 objetos y 115 orbes; media aproximada de 40,3 ms por tick y percentil 95 de 66,2 ms. La creación de rutas ocupa 10,12 % sin sumar llamadas recursivas duplicadas; `NoisePolicy.investigateTick`, 6,61 %; eventos de Gore, 1,60 %. Son tiempos inclusivos por componente y no deben sumarse como categorías independientes. No mide FPS, EMF ni GPU.

El TZP recibido tenía modelos conectados de Fresh Animations y ninguna variable `zg_` en los JEM. El nuevo pack añade la anatomía que responde a las variables del addon.

Validación: 17 comprobaciones de limitador/reflexión y 27 de inventario/recogida con dobles de API: conservación, ampliación adicional, tags distintos, transferencias parciales, paredes, distancia, altura y espera de objetos nuevos. ASM verificó 727 métodos en Hordes y 157 en Gore. Se comprobó que los demás archivos originales permanecen iguales, ZIP íntegros y 3938 texturas/sonidos conservados. **No se ejecutó Minecraft ni se obtuvo un perfil posterior: no hay una mejora de FPS o MSPT medida todavía.**

Para comprobar en juego: tira stacks de tipos distintos junto a un cadáver, espera cinco segundos, abre el inventario, reinicia el mundo y verifica que siguen allí. Repite con inventario lleno. Prueba disparos y fuego con el pack nuevo activo. Para medir gráficos, toma un perfil del hilo Render thread; para comparar servidor, toma otro perfil en la misma zona y con una cantidad similar de entidades.

## Reconstrucción

Java 21, ECJ/ASM en `/workspace/zombies-only-patch/tools`, Python 3 y Pillow:

```sh
python zombie-performance-patch/build.py /ruta/a/Hordes18.jar /ruta/a/Gore1.0.2.jar
python zombie-performance-patch/pack/build.py /ruta/a/TZP_1.20.1_2.7.zip
```

Los dobles de API se compilan en un directorio separado y no se incluyen en los JAR. Las texturas originales son de Tissou; los movimientos compactos se inspiran en Fresh Animations de FreshLX. Los modelos y texturas procedurales de heridas pertenecen al addon.
