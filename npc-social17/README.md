# TacZ NPCs .17: conversaciones y compañeros persistentes

[Descargar ZIP directo](../distribuciones/Zombies-Poldo-NPCs-Sociales-17.zip) · [Guía de uso](GUIA.txt)

Tres reemplazos sobre los JAR adjuntos: TacZ NPCs .17, FactionFriction .16 y First Aid .6.6. Se conservan SmartBrainLib 1.16.11, Hordes .46 y Remains .15 sin modificarlos. `inputs.json` fija los seis archivos de entrada por SHA256. Los JAR originales van en `base/`, excluido de Git.

Se extiende el controlador Calm existente a NPC naturales sin interferir con los NPC de prueba `np15_test`. La adquisición de objetivos y los ataques contra jugadores respetan disposición, arma, afiliación, represalia, visibilidad e inspección continua de 100 ticks. El combate, munición, dispersiones, recargas y navegación siguen usando el mod nativo. Un objetivo que sale de vista pierde la inspección acumulada. Los gestos de paz usan la postura sincronizada real y se copian al jugador de renderizado.

Las conversaciones tienen pantalla no pausante y tres pestañas, 148 textos por idioma y ocho personalidades. El servidor envía únicamente una instantánea mediante un payload S2C acotado. Las acciones usan comandos de sesión, con distancia, dimensión, visibilidad, tiempo y afiliación comprobados en servidor; no se aceptan UUID de NPC arbitrarios del cliente. La confianza se guarda en los datos persistentes del NPC; reputación y registro de compañeros se serializan en FactionManager, mediante un campo público y hooks de carga/guardado. Se respeta SwitchyIdentity. Los compañeros no se insertan como falsos miembros humanos ni herederos de facciones.

El inventario es un ChestMenu nativo, con 47 casillas de bolsa y seis de equipo, bloqueo de segundo observador y pausa de mutaciones automáticas mientras está abierto. Los cambios se guardan de inmediato y validan armadura. Se preserva el contenido sobrante de inventarios antiguos. La muerte retira la afiliación sin inventar respawns; permanece la entrega nativa de pertenencias.

El puente médico nuevo llama al modelo real de First Aid para partes del jugador y recuperación crítica. Restaura la guarda `Assist.completing` incluso al fallar. El compañero usa RCP o su propio desfibrilador; se consumen suministros solo al completar tratamiento. Los NPC naturales utilizan vida de criatura y registro de partes compatible con `npcprobe.Parts`; no se simula que son ServerPlayer ni se cambia la medicina de jugadores existente. Se preserva también la ayuda entre NPC amistosos.

Trabajos locales: seguimiento, espera, patrulla, búsqueda, cosecha/replantado, limpieza que conserva pertenencias, fabricación validada por RecipeManager, cocción en hornos reales y entrega al propietario de la orden. Escaneos por lotes, sin cargar chunks y con comprobación de visión antes de actuar sobre bloques. Se respeta propiedad de facciones y `QuietlyPlayerPlaced`. El planificador de fabricación revisa hasta 24 recetas por intento, requiere ingredientes reales y mesa para recetas que exceden la cuadrícula personal. No implementa minería de túneles, construcción autónoma ni planificación ilimitada de todas las máquinas del pack.

Reproducir con Java 21 y Python 3 desde la raíz:

```
python npc-social17/translations.py
python npc-social17/build.py
python npc-social17/test.py
python npc-social17/package.py
```

358 comprobaciones. Las pruebas ejecutan helpers de producción con dobles del juego, incluidos persistencia de facciones, recetas y medicina, y verifican bytecode nativo modificado. No se inició un cliente o servidor Minecraft/NeoForge real; el pathfinding, los eventos del pack completo, la apariencia y el coste con muchos NPC requieren validación en una copia de mundo. No se publican archivos de diagnóstico ni datos reales de jugadores.
