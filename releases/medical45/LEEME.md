# Heridas visibles y cadáver único — NeoForge 1.21.1

Cierra Minecraft y el servidor. Reemplaza los seis mods indicados abajo en clientes y servidor; retira sus versiones anteriores, sin dejar duplicados. Instala el conjunto completo: cambia el protocolo de Ragdollified. No cambies tus paquetes TZP/Poldos ni Splatter blood-budget.2 para esta actualización.

| Mod anterior | Incluido |
|---|---|
| First Aid 3.6.14 | 3.6.15, heridas visibles |
| TaCZ First Aid compat 1.0.2 | 1.0.3, coordenadas de impacto |
| Gore .29 | .30, heridas médicas y cuerpo enlazado |
| Ragdollified .23 | .24, representación del mismo Corpse |
| Corpse cargo .4 | .5, enlace con física y lesiones |
| TaCZ NPCs .30 | .31, transferencia al cuerpo enlazado |

## Cambios

- First Aid guarda marcas de disparo y mordedura dentro de cada región corporal. Se sincronizan con su modelo médico existente, se guardan con el jugador y se dibujan sobre su modelo normal o slim, sin reemplazarlo. Las marcas nuevas tienen ubicación propia; no se reconstruyen impactos de partidas antiguas que nunca guardaron coordenadas.
- El puente de TaCZ entrega a la marca el mismo punto de impacto que utilizó para aplicar el daño. La conversión usa la orientación corporal y una transformación de ida y vuelta para agacharse o arrastrarse. El gore deja de usar la altura reducida de colisión del zombi como escala del esqueleto completo.
- Una venda cubre la marca con tela; las vendas sucias se oscurecen. El tratamiento limpio reduce la marca conforme progresa la recuperación médica; al sanar vuelve la piel original. Suciedad, infección, inflamación o una bala alojada pendiente de resolución pausan su cierre. Los tiempos médicos existentes de First Aid siguen controlando salud, sangrado y extracción.
- Corpse conserva el inventario y el guardado. Ragdollified representa ese mismo cadáver usando el ID de Corpse; se suprime el ragdoll independiente del jugador/NPC al morir. Se transfieren las lesiones del NPC o la instantánea médica del jugador antes de vaciar el equipo del NPC. Al retirar el cadáver se retira su física. El cuerpo conserva las lesiones al guardar y recargar.
- Se conserva la textura propia del NPC y su modelo de brazos normal/slim. Se mantiene la reducción de empuje de Ragdollified .23.
- Los disparos al cadáver pasan por el volumen físico conservado, sin chocar antes con la caja genérica de Corpse. Las zonas totalmente destruidas dejan de interceptar balas; la envolvente de tejido restante se almacena en caché y se invalida cuando cambian sus lesiones.
- Se conservan el gore procedural, las quemaduras, la anatomía de NPCs y el centrado de piezas de las versiones entregadas. Este paquete no rehace las linternas, shaders, sonidos ni IA de combate.

## Alcance y comprobación

Se ejecutaron pruebas de código de persistencia, tratamiento, transformaciones de postura, enlace de jugadores/NPCs, retirada del cuerpo, colisión de partes destruidas y dibujo de marcas en brazos normales/slim. Las dependencias de Minecraft de esas pruebas son objetos simulados; no equivalen a ejecutar el juego. Se verificaron los métodos de los seis JAR con ASM y su integridad ZIP. El informe adjunto distingue esas pruebas.

**No se ha ejecutado Minecraft con el modpack completo.** Las marcas se colocan sobre superficies de los modelos y usan las regiones médicas existentes; no se ha añadido un trazado exacto contra cada hueso de todas las animaciones o armaduras de terceros. La colisión del tejido restante es conservadora. Los cadáveres anteriores sin instantánea de este enlace mantienen su representación anterior; usa entidades nuevas para comprobar la transferencia.

Para probar: dispara a un jugador de prueba y a un NPC nuevo; comprueba la región médica, véndala y observa la marca. Mata al NPC: debe quedar un solo cuerpo con su equipo y lesiones. Guarda/sal del mundo, vuelve a entrar y abre ese cuerpo. Retíralo y comprueba que no quede una copia física. Comprueba también heridas de zombi de pie y arrastrándose. Si falla, conserva el latest.log de esa prueba e indica qué versiones de estos seis JAR cargó.
