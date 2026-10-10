# Muertes coordinadas: death-compat.1

Parche de los tres JAR exactos entregados por el usuario. Minecraft 1.21.1 / NeoForge **21.1.255 o superior**, con Doomsday Decoration 1.1.4.2 o superior. Conserva los autores, licencias, recursos y dependencias originales.

## Instalación

Eliminar las versiones anteriores de estos tres mods; no instalar dos JAR con el mismo modId.

Cliente, incluido mundo de un jugador:

- `Saros-Mob-Ragdoll-NeoForge-1.21.1-1.0.1-death-compat.1.jar`
- `mobdismembermentneoforge-1.21.1-1.4.0-death-compat.1.jar`
- `zombie-remains-loot-1.6.2-neoforge-1.21.1-death-compat.1.jar`

Servidor dedicado: instalar las copias parcheadas de **Saros y Zombie Remains Loot**. Mob Dismemberment sigue siendo visual y solo de cliente; no hace falta instalarlo en el servidor. Todos los jugadores necesitan el cliente parcheado. Los requisitos exactos entre parches evitan mezclarlos con los originales.

Mantener Doomsday Decoration, TACZ, TacZ NPCs tactical.13, Self Expression y las otras dependencias que ya se usaban. Se conservan la identificación de bandidos y los métodos originales que generan su loot y copian su equipo.

## Secuencia

1. El servidor recoge el daño del golpe fatal después de la reducción de armadura y decide el efecto. Explosiones, o daño al menos `max(24, 1.25 × salud máxima)`, producen desmembramiento. Para un mob con 20 de salud, el umbral es 25 puntos (12.5 corazones). Las demás muertes usan ragdoll.
2. El servidor envía una orden identificada a los clientes cercanos. Se desactiva la selección automática independiente de los dos mods visuales. Así una muerte no genera los dos efectos.
3. El cliente comprueba reposo real: todos los cuerpos del ragdoll con velocidad pequeña y suelo debajo, o todos los fragmentos restantes apoyados/en agua y con movimiento horizontal pequeño. Requiere al menos un segundo de simulación y 12 ticks consecutivos en reposo.
4. Retira el efecto y comunica su posición. El servidor valida identidad de la muerte, observador, dimensión, tiempo, coordenadas finitas y distancia. Busca soporte sólido y un bloque reemplazable sin fluido en chunks ya cargados. Nunca reemplaza otro cadáver ni un bloque sólido.
5. Se crea el cadáver saqueable conservando su inventario y calidad, y se ordena retirar ese efecto a los demás observadores. Otros mobs que no estaban admitidos por Remains solo reciben el efecto visual; no se amplía su tabla de loot.

Los cadáveres pendientes se guardan en el SavedData existente, con los stacks serializados mediante ItemStack para conservar componentes. Tras reiniciar, se colocan cuando el terreno está cargado. Se mantiene el formato de los cadáveres ya existentes y su descomposición.

## Recuperación y límites

- Sin soporte para desmembrar un modelo, se intenta ragdoll. Si falla la biblioteca física, el loot sigue perteneciendo al servidor.
- Si no llega un aviso de finalización, el servidor recupera el cadáver tras 600 ticks (30 segundos a 20 TPS), buscando suelo debajo de la muerte. No exige que siga conectado un observador.
- Si no hay sitio válido, conserva el inventario pendiente y reintenta; no fuerza chunks ni lo borra. En agua profunda o vacío puede seguir pendiente hasta haber un lugar seco válido. La búsqueda admite hasta 128 bloques hacia abajo y tres bloques alrededor.
- La física de ambos mods es local a cada cliente; no es una entidad física compartida del servidor. La primera posición de reposo válida determina dónde aparece el cadáver para todos. Se admite hasta 16 bloques horizontales respecto de la muerte.
- Se respeta la resurrección del Mutant Zombie mientras tenga vidas restantes; los jugadores conservan el sistema de cadáveres que ya usaban.
- Se mantienen las reglas originales de inventario de Remains 1.6.1, incluida su capacidad y la lectura de los primeros nueve stacks del botín almacenado de TacZ NPCs. Este parche no modifica la IA ni amplía esa capacidad.

## Validación y reconstrucción

**47 aserciones** ejecutan los helpers publicados y el codec real del nuevo paquete con dobles de Minecraft/NeoForge/física: elección del efecto, exclusión de duplicados, reposo, pausa, posición de aterrizaje, inventario/ropa/munición, calidad legendaria, reinicio, chunk descargado, fallo de colocación, límite de tiempo, resurrección y fallo de física. Análisis ASM de **69 métodos** originales/modificados. Recursos y clases no seleccionados se conservan byte por byte; los JAR no incluyen las clases simuladas.

**No se ejecutó Minecraft ni el modpack completo en este entorno.** Falta validar carga real, renderizado, física nativa y orden de eventos con el resto del modpack.

Requiere Java 21 y Python 3. El ZIP de fuentes contiene herramientas con hashes fijados, fuentes, dobles, pruebas y transformación ASM. Los originales se suministran como argumentos y sus SHA-256 se verifican:

```bash
python3 -B build.py /ruta/Saros-Mob-Ragdoll-NeoForge-1.21.1-1.0.1.jar /ruta/mobdismembermentneoforge-1.21.1-1.4.0.jar /ruta/zombie-remains-loot-1.6.1-neoforge-1.21.1-npc-compat.jar
```

Los tres resultados se generan en `output/`. Se comprobó también la reconstrucción desde una extracción limpia del ZIP de fuentes.
