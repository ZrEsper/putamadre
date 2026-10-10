# Zombis reactivos — IA revisión 1

Base: `zomboid-hordes-1.8.3-neoforge-1.21.1-rare-mutants.1.jar` publicado en el repositorio. Conserva su población de mutantes raros, ausencia de sonidos distantes y todas sus demás clases/recursos, excepto ZombieIndividual y HordeEvents. Minecraft 1.21.1, NeoForge 21.1.255+, SoundAttract 6.3.8d.

## Instalar

Cliente y servidor: sustituir únicamente el JAR anterior de Zomboid Hordes por `zomboid-hordes-1.8.3-neoforge-1.21.1-rare-mutants-reactive-ai.1.jar`. No dejar dos Hordes instalados. Mantener MutantsZombies horde-loot.1, Remains mutant-loot.1 y los demás mods del paquete anterior. Compatible con perfiles de zombis ya guardados; no requiere mundo nuevo.

## Comportamiento

- Cualquier perfil, incluido el torpe, adquiere un jugador válido y visible a **dos bloques o menos**, sin depender del ángulo de mirada, estar agachado ni de su demora aleatoria. Revisión cada cuatro ticks (hasta 0,2 segundos a 20 TPS); dirige la mirada al jugador. Respeta objetivos más cercanos ya vivos.
- Con pasos normales, ruido de ropa al caminar, estornudos o tos, la audición cercana tiene un mínimo de **cinco bloques**. No puede fallar por una tirada aleatoria ni por la espera previa de oído. Caminar agachado conserva el radio silencioso del original; estar a dos bloques y a la vista sigue siendo detectable.
- El desplazamiento real del jugador a pie se comprueba también localmente, para que un zombi cercano pueda escuchar sus pasos aun cuando SoundTracker no registre el sonido. No produce pasos por cambiar la cámara, quedarse parado, hacer saltos grandes de posición, ir en un vehículo o caminar agachado. Detectar movimientos sin sonido no añade sonido ambiental al cliente.
- Con un obstáculo entre ambos, guarda el punto del ruido para investigarlo. No adquiere un objetivo visual a través de la pared. Oír al jugador guarda memoria durante cuatro segundos; al verlo puede orientarse sin el filtro frontal previo. Sigue vigente la memoria visual original una vez detectado.
- Fuera de la distancia cercana conserva las diferencias de oído y azar entre perfiles: torpe, normal y atento. Las esperas de audición después de aceptar un ruido son 12, 6 y 2 ticks respectivamente; los ruidos cercanos garantizados las pueden interrumpir. Un ruido que se rechaza no consume una espera nueva.
- Usa el punto exacto del ruido, sin dispersarlo aleatoriamente alrededor del jugador. Disparos, explosiones y eventos mantienen sus radios originales. Sonidos de tos/estornudo pueden atribuirse al jugador junto al punto del sonido aunque el registro no los marque como producidos por jugador; registros alejados no se atribuyen por esa ruta.
- Excluye muertos, jugadores creativos y espectadores. Se ejecuta en servidor sobre las entidades ya admitidas por el sistema de zombis; no añade lógica para bandidos.

La adquisición a dos bloques exige línea de visión física; el ángulo frontal se ignora. A cinco bloques la regla es de **audición del ruido**, no detección permanente de un humano inmóvil. Los tiempos en segundos suponen 20 TPS; un servidor lento responde más tarde.

## Construcción y validación

Java 21 y Python 3. Herramientas ECJ/ASM incluidas y verificadas por SHA-256; no necesita javac ni descargar dependencias. Las fuentes son del código añadido; los JAR originales conservan sus autores y licencias.

```sh
python3 build.py /ruta/zomboid-hordes-1.8.3-neoforge-1.21.1-rare-mutants.1.jar
```

La base se verifica contra `base.sha256.json`. Pruebas ejecutadas: 45 comprobaciones sobre los helpers de producción con doubles de API; límites de 2 y 5 bloques, todos los perfiles incluso con espera activa, paredes, memoria, pasos/crouching, estornudos, jugadores excluidos, ejecución solo en servidor, fases de ticks y adquisición/mirada. Análisis ASM de 59 métodos originales/modificados, integridad ZIP y comparación de todas las entradas no modificadas, reconstrucción desde fuentes descomprimidas en directorio limpio.

No se ejecutó Minecraft ni el modpack completo aquí. Recomendación de prueba: en supervivencia, un zombi de perfil torpe debe reaccionar al acercarte a dos bloques incluso agachado o detrás de él; a cinco debe escuchar pasos normales o estornudos; agachado e inmóvil a cinco no debe detectarte por esta regla. Repetir con una pared y verificar investigación del lugar del ruido, sin ataque a través de la pared. Confirmar también que los mutantes raros y el loot previo continúan funcionando.
