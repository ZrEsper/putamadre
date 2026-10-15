# FirstAid 3.6.2 · Tolerancia balística local

[Guía conjunta](../screamer-horror-patch/GUIA.md). Entrada exacta: FirstAid 3.6.1 head8-torso15 del usuario. Ocho puntos de armadura local equivalen nominalmente a 16 impactos de pistola en total. La resistencia no puede empeorar respecto a ir sin armadura. El impacto usa el daño efectivo que entrega TaCZ, su caída por distancia y sus dos fuentes normal/perforante, sin aplicar dos veces la penetración ni la reducción balística de armadura.

Desde la raíz: `python armor-bullet-tolerance-patch/build.py`. Java 21 y ECJ/ASM fijados por SHA-256. Solo cambia DamageablePart.damage, ArmorUtils.applyArmor y el contexto de fuente de ClinicalHooks.distribute; el resto del JAR y recursos se conservan. El contexto se apila por hilo y se limpia en salida normal y excepcional. El daño sobrante vuelve a las unidades originales del distribuidor para evitar transferir unidades de salud como si fueran daño bruto.

14.773 comprobaciones de lógica con dobles de API y verificación del bytecode. No se ha ejecutado el flujo completo de Minecraft/TaCZ. Quedan por verificar en juego el orden de eventos de todos los mods, durabilidad, daño efectivo con tus packs y sincronización.
