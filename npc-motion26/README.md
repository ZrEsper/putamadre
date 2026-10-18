# NPC .26: mirada, fogonazo y deslizamiento

Base .25 fijada por SHA. Compilar desde la raíz con `python npc-motion26/build.py`; validar con `python npc-motion26/test.py`. Reutiliza Java21, ECJ y ASM instalados. No modifica TaCZ, Global Melee .39 ni Flashlight .16.

## Mirada y arma

CalmBrain.face ahora entrega el punto de mira al LookControl además de fijar yaw/pitch. Un enganche al final de AutoGunOperator.tick vuelve a alinear cuerpo/cabeza con el enemigo visible después de MoveControl/LookControl y mantiene el arma levantada salvo durante recarga. Es importante hacerlo al final: el movimiento podía sobrescribir el yaw calculado durante el objetivo de combate.

El renderizador FakePlayer alinea cuerpo, cabeza y arma con el rumbo de la cabeza sincronizada. La postura baja y la interpolación tumbada no dependen del rol guardado en PersistentData del servidor, que no tiene por qué existir en el cliente. Se conservan el movimiento y las condiciones de tiro nativos.

## Fogonazo y animaciones

AnimationPacket del binario real tiene constructor `(byte, int)`, no `(int, int)`. Se corrigió la construcción de los eventos de recarga. Los disparos exitosos de cualquier NPC envían acción3; el cliente recuerda el evento durante90ms. El Mixin de MuzzleFlashRender dibuja la textura nativa en el nodo físico del cañón con luz completa, dentro del ámbito del NPC, y restaura las matrices compartidas de TaCZ. Conserva el modificador nativo de silenciador y evita el pase de sombras de Iris. No altera el fogonazo del jugador. Es un fogonazo visible, no una nueva luz dinámica de terreno.

Acciones4/5 sincronizan un deslizamiento de12ticks. Se activa para hostiles armados que recargan, reciben daño o escuchan disparos cercanos, con enemigo a3–22 bloques y un intervalo mínimo de8segundos. Conserva gravedad, frena progresivamente y comprueba suelo, chunks cargados y colisiones. Tiene una postura lateral de torso/piernas y la ropa la sigue. No reduce la caja de colisión ni permite atravesar huecos bajos. El disparo añade una breve reacción de brazos; la recarga usa el clip nativo que ahora recibe su paquete correctamente. Se conservan los pasos y voces anteriores.

## Equipo

La visión nocturna instalada permanece activa de día y de noche mientras tenga batería. Mantiene el visor bajado; cuando falta óptica, casco compatible o energía usa una linterna. Se conservan dos perfiles iniciales militares: óptica/láser y linterna. Las gafas retiradas se conservan en el inventario y se pueden volver a equipar. Las transiciones reutilizan la linterna guardada y no generan otra cada tick si el jugador la retira. Las ópticas/linterna también ayudan a la percepción de defensores neutrales sin dar visión a través de paredes.

## Verificación y límites

37 comprobaciones heredadas de táctica,41 de mirada/posturas/equipo/paquetes y9 de fogonazo, más la prueba de modelo de piernas/ropa/recoil. ASM BasicVerifier revisa once enganches nativos; se comprueban contra bytecode real el constructor byte de AnimationPacket, campos y métodos de MuzzleFlashRender y la firma del Mixin. La clase mínima MuzzleFlashRender procede del TaCZ1.1.8 aportado antes y queda fijada por SHA. Las fixtures no se empaquetan. Los helpers están fuera del paquete reservado de Mixin.

No se ejecutó Minecraft con el modpack completo ni se verificó visualmente con MakeUp. Las pruebas comprueban decisiones, sincronización y llamadas de renderizado mediante fixtures. El deslizamiento y el aspecto final del fogonazo deben revisarse dentro del juego.
