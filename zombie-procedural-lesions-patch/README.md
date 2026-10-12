# TZP Gore 4: piel original, lesiones y movimiento expresivo

## Instalación

Activa `TZP-1.21.1-Gore.4-original-skins-cartoon-lesions.zip` arriba de Fresh Animations y los demás packs de modelos de zombis. Desactiva los packs TZP/Gore 1, 2 y 3 anteriores. Conserva Gore 1.0.4, Remains 4, Hordes 20, ETF y EMF. Este cambio no requiere sustituir los JAR.

El pack parte de los modelos conectados originales del TZP enviado, incluyendo las animaciones de FreshLX. No reutiliza el esqueleto que generó los problemas de los packs anteriores. Las 3938 imágenes y sonidos originales se conservan byte a byte.

## Aspecto y movimiento

Un zombi intacto usa los mismos cubos, UV, posiciones, mirrors y jerarquía del modelo original. Los cubos de su piel permanecen completos hasta que una lesión exige quitar tejido. No existe material de carbón ni variable de quemadura que cambie la piel. El fuego se ve con las llamas de Minecraft; los movimientos agitados solo se activan mientras `is_burning` sea verdadero. No se añade una piel carbonizada persistente.

Se conservan respiración, expresiones faciales, pies y balanceos de Fresh Animations. Se añaden peso lateral al andar, oscilación de brazos, reacción elástica al daño, movimientos agitados por fuego y desequilibrio cuando falta un brazo. Con daño craneal la cabeza cae y oscila; sin piernas se arrastra. La pose de cabeza y su geometría en headwear reciben la misma reacción, evitando que se anime solo una pieza vacía.

Las heridas se activan por las flags de Gore, por zona dañada:

- Marcas pequeñas de bala con centro oscuro, borde rojizo irregular y transparencia real.
- Carne interior de tonos apagados, separada del borde de la piel.
- Pérdida parcial del cráneo: conserva media piel original y expone hueso con cavidad ocular.
- Mandíbula y extremos de extremidades con tejido y hueso visible al perderlos.
- Costillas y músculo cuando se pierde tejido de un lado del torso.
- Marcas de salida para los impactos que Gore clasifica como francotirador.

Los disparos se localizan por las ocho zonas sincronizadas del addon; no se crea un agujero en la coordenada exacta de cada proyectil ni se simulan tejidos físicos. La pérdida de tejido usa las reglas de daño existentes del JAR. Los modelos propios de infectados especiales no se reemplazan.

## Prevención del fallo anterior y coste

Todas las animaciones están en entradas superiores de models, donde EMF 3.3.11 las recoge. Las piezas de lesión tienen escala cero también en sus datos iniciales; solo reciben escala y visibilidad al existir la lesión. Si una animación no pudiera evaluarse, no deja una capa de herida o carbón visible por defecto.

Se usan cuatro modelos base, sin las cinco variantes de esqueleto del pack anterior. Las texturas procedurales nuevas son tres imágenes de 64×64. Los indicadores de daño se calculan una vez por modelo; las superficies pequeñas de carne/hueso y marcas se limitan a 16 bloques. No genera modelos, entidades ni texturas nuevas con cada impacto. Las poses a distancia conservan la base Fresh; el balanceo adicional y detalles caros se limitan a los zombis cercanos.

## Comprobación

- 368 comprobaciones estructurales: geometría original intacta, materiales ocultos inicialmente, ubicación de animaciones y ausencia de carbón.
- Comparación de geometría y UV en un rasterizador técnico offline: original e intacto nuevo producen la misma imagen píxel a píxel en la pose de diagnóstico.
- 999 líneas de animación analizadas y 2400 fotogramas simulados para zombi, husk y drowned, con casos intacto, fuego, lesión y distancia. Las poses son finitas, están acotadas y los brazos cambian de ángulo en el tiempo. Las superficies de lesión permanecen ocultas en los casos sin lesión, incluso mientras están ardiendo.
- ZIP íntegro, imágenes y sonidos originales conservados.

**Esto no es una prueba en Minecraft.** El rasterizador y el evaluador matemático son herramientas de diagnóstico propias; no ejecutan el renderizador, compilador de expresiones ni shaders de EMF. Falta verificar la apariencia y rendimiento reales con los mods del usuario. `geometry-preview.png` está rotulada expresamente como una previsualización offline.

## Reconstrucción

Python 3, Pillow y NumPy:

```sh
python zombie-procedural-lesions-patch/build.py /ruta/a/TZP_1.20.1_2.7.zip
python zombie-procedural-lesions-patch/check_animations.py
python zombie-procedural-lesions-patch/preview.py /ruta/a/TZP_1.20.1_2.7.zip
```

Texturas originales de Tissou; modelos/animaciones originales de FreshLX. Los atlas de tejido, hueso y balas se dibujan proceduralmente desde cero, sin alterar las imágenes originales del pack.
