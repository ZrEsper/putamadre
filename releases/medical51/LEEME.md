# Hotfix 51 — crash de heridas, torso y cadáver duplicado

Continúa la corrección 50, commit `ab7aa8bb62107b75a191ab6e6e577715e04719e0`, basada en la rama medical49 de baconwithd/Steph.

## Instalar

Cierra Minecraft y el servidor. Reemplaza **solamente** Gore .34 y Ragdollified .27 por los dos JAR de `mods/`, en el cliente y el servidor. No dejes dos versiones del mismo mod.

Conserva FirstAid 3.6.17, TaCZ FirstAid Compat 1.0.5, TaCZ NPCs .33, Zombie Remains Loot .24, Petrichor depth-copy .1 y Corpse .5 de la combinación anterior. No cambian sus inventarios ni sus versiones en este hotfix.

## Qué se corrigió

- El crash adjunto es `IllegalStateException: addVertex`, causado por `Not building!` en `Bite48.emit`, durante el renderizado del jugador en el inventario. Las marcas médicas y la carne usaban el BufferSource recibido del renderizador: cambiar su textura cerraba el búfer de piel que las partes siguientes todavía necesitaban. Ahora utilizan dos búferes propios, que se vacían al terminar de renderizar la entidad. El búfer original permanece abierto.
- El torso mordido se dibujaba 16 veces más pequeño. La geometría producida por CSG24 y Mesh12 ya estaba en unidades de bloque; Bite48 volvía a dividir las coordenadas entre 16. Se elimina esa segunda división. Una mordida pequeña conserva el volumen completo de piel salvo la lesión localizada.
- Al morir un PlayerZombie, el sistema de Hordes/PlayerCorpse43 crea el Corpse con el inventario real. Ragdollified también generaba su cuerpo de zombi independiente. Se reserva esa muerte a Corpse y se bloquea el ragdoll adicional en la fábrica del servidor y en las dos fábricas locales del cliente. **El zombi del jugador conserva su cadáver Corpse con botín; no se añade un segundo cuerpo físico independiente.** Los derribos vivos y las muertes de zombis normales conservan sus rutas existentes.

Prueba la duplicación con una muerte nueva después de reiniciar. Los cuerpos duplicados que ya existan no se eliminan automáticamente, para evitar borrar inventarios guardados.

## Pruebas

La prueba con las funciones de producción reproduce tanto el crash del búfer como el torso reducido en Gore .34. Con Gore .35 se comprueba que la piel sigue aceptando vértices después de dibujar lesiones y después de vaciar las capas médicas; también se comprueba el tamaño completo del torso y la selección de un solo sistema de cadáver para PlayerZombie y sus subclases. Un PlayerZombie vivo no queda excluido de los derribos locales.

Se comprobaron los enganches de los archivos reales: vaciado al terminar Skin12, una fábrica física del servidor y dos fábricas locales. Pasan el análisis de bytecode de 998 clases, los 4030 controles anteriores de persistencia médica/cadáver único, la ejecución real de Bullet13.tick con contenedor y cuerpo posado, y 12 controles del derribo/revivida de supervivientes.

**No se ejecutó Minecraft ni una GPU.** El ciclo de BufferSource se simula con el comportamiento de cerrar el lote anterior al cambiar RenderType; las funciones de dibujo, reflexión y geometría sí son las del mod compilado. La apariencia y la interacción con el modpack completo requieren comprobarse dentro del juego.

Se adjuntan fuentes, pruebas, versiones y SHA-256. Los JAR no contienen las clases simuladas usadas en las pruebas.
