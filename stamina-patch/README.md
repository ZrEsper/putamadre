# Estamina v10, HUD v2 y Survivor Creator v18

Cambios construidos sobre los dos JAR aportados por el usuario y Survivor Creator v17 del repositorio. Minecraft / NeoForge 1.21.1.

## Instalación

Reemplazar las tres versiones anteriores en **cliente y servidor**, sin dejar duplicados:

- `eZWeight-neoforge-1.21.1-1.8.4-stamina-v10-fitness-limbs.jar`
- `zomboid-project-progression-1.21.1-1.0.2-stamina-hud-v2.jar`
- `survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v18-stamina.jar`

Conservar First Aid, OK Additions, GeckoLib y las otras dependencias de la instalación. **No hay que cambiar First Aid para este ajuste.** La API de piernas y pies se comprobó contra `firstaid-1.21.1-ZOMBOID-v3.4.6-wetness-no-panic.jar` del repositorio.

Los tres mods conservan sus identificadores y versiones declaradas anteriores; los nombres de los archivos distinguen este parche. La sincronización de estamina pasa a protocolo `10` y payload `ezweight:stamina_v10`: cliente y servidor necesitan eZWeight v10.

## Capacidad real de estamina

Fitness añade **5% por nivel**, Sprinting **3% por nivel**, y el rasgo **Corredor** añade **15% adicional**. Son porcentajes sumados sobre la reserva base de 3000; niveles limitados a 0–10. Se usan los niveles reales del servidor, incluidos los iniciales por profesión/rasgos y los adquiridos jugando.

| Selección | Capacidad |
|---|---:|
| Fitness 0, Sprinting 0, sin Corredor | 3000 |
| Fitness 5, Sprinting 5 | 4200 |
| Corredor sin otros bonus: Fitness +1 y Sprinting +2 | 3780 |
| Fitness 10, Sprinting 10 y Corredor | 5850 |

Corredor mantiene su coste de cuatro puntos y su reducción de consumo de energía **alimentaria** del 10%. Survivor Creator muestra los bonus de estamina tanto en selección como en las descripciones de niveles. Los perfiles guardados siguen usando el mismo ID `survivorcreator:runner`.

La barra representa la misma reserva que consumen carrera, saltos, golpes, golpes fuertes y empujones. eZWeight continúa como única autoridad; se mantiene su compatibilidad que cancela la reserva independiente antigua de Progression. Aumentar niveles o cambiar rasgos conserva la cantidad de energía restante, sin rellenar gratuitamente la reserva. Las partidas existentes se migran desde el factor anterior; al subir capacidad, el porcentaje puede bajar hasta recuperar energía descansando.

Se conservan costes absolutos de acciones y recuperación absoluta anteriores. Una capacidad mayor permite más acciones y también tarda más en llenarse por completo. Internamente se normaliza la reserva para conservar las interfaces públicas de combate; la sincronización incluye el factor de capacidad para que el cliente aplique las mismas comprobaciones de gasto.

## HUD

Nueva textura pixelada con marco oscuro, divisiones y colores apagados, en lugar de la línea vertical verde. Dos barras horizontales, **Energía** y **Carga**, junto a la derecha de la hotbar, cerca del borde inferior. Se adapta a anchos de GUI normales; en espacios estrechos abrevia a `E` y `P`.

- Porcentaje de energía y estado: En uso, Descansando, Recuperando o Listo.
- `Espera 20%` cuando está agotado. La marca en la barra señala el umbral del 20% que permite volver a correr.
- Tras gastar energía hay una pausa nativa de recuperación de 40 ticks; después se recupera más rápido quieto que caminando.
- Carga muestra también porcentajes superiores a 100%, con aviso en rojo al superar capacidad.
- Se retira el dibujo antiguo de Progression para evitar barras superpuestas. Sus funciones de progresión, atributos, peso y munición se mantienen.

`hud-preview.png` es una ilustración del diseño, **no una captura de Minecraft**.

## Sobrecarga y First Aid

El daño empieza solo cuando el peso es **mayor que la capacidad actual**, en lugar del umbral previo que podía comenzar al 80%. Ocurre cada **100 ticks consecutivos**, cinco segundos a 20 TPS. Al volver dentro de la capacidad, desactivar el daño, morir o estar en creativo/espectador, se reinicia el contador.

Se conserva la cantidad configurada por pulso (por defecto 0.5): el campo legado `damage_per_second` pasa a ser la cantidad por pulso de cinco segundos. **No se multiplica el daño por cinco.**

Con First Aid, se reparte entre ambas piernas y ambos pies que todavía tengan salud. Se aplica el daño directamente a esas partes con sus debuffs, se programa sincronización y se actualiza la salud vanilla desde el modelo. No se realiza además un `hurt` genérico: cabeza, torso y brazos no reciben este daño por sobrecarga. Las partes ya sin salud se excluyen del reparto. Si el modelo médico aún no existe se omite ese pulso; si First Aid no está instalado se conserva el daño vanilla, sin localización anatómica.

## Fuentes y comprobación

Java 21 / Python 3, usando los originales indicados y las herramientas incluidas, verificadas por SHA-256:

    python3 -B build.py /ruta/eZWeight-neoforge-1.21.1-1.8.4-stamina-v9-combat.jar /ruta/zomboid-project-progression-fitness-stamina-hud-1.0.2-stamina-bars-ammo.jar /ruta/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v17-profiles.jar

`src/` implementa capacidad, red, reglas, HUD y lesión; `survivor-src/` actualiza las descripciones y la selección. `Patch.java` modifica límites precisos del bytecode existente. Texturas y JSON se incluyen en `resources/` y `survivor-resources/`. Los stubs y dobles de API se usan para compilar/probar y **no se empaquetan en los mods**.

Pasaron **58 comprobaciones** de capacidad y Corredor, conservación de energía, gasto sin doble conversión, consumo y tick originales parcheados de eZWeight, recuperación, bloqueo al 20%, codec de red y paquetes fuera de orden, limpieza al desconectar, HUD, intervalo real de 100 ticks, umbral por encima de capacidad, inmunidad creativa, aislamiento de piernas/pies y vista previa de beneficios. Se ejecutan helpers y bytecode real con `-Xverify:all`; ASM analiza 23 métodos de las clases transformadas de eZWeight. También se comprueba la integridad del ZIP y la conservación de bytes originales fuera de los cambios. El conjunto se reconstruye desde el archivo de fuentes.

**No se ejecutó Minecraft ni el modpack completo.** Las pruebas usan dobles de Minecraft, Survivor Creator y First Aid; no equivalen a verificar la posición visual en todos los HUD del pack, debuffs médicos o sincronización real entre dos máquinas. Probar en juego: comparar capacidad según niveles; gastar y recuperar energía; llegar a cero y esperar al 20%; cambiar nivel sin rellenar reserva; cargar más del máximo durante cinco segundos y comprobar solo piernas/pies en First Aid; bajar de peso y verificar que cesa el daño.
