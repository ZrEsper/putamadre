# Immersive Vehicles + Official Pack: mecánica Zomboid

Minecraft 1.21.1, Immersive Vehicles 24.0.0, Official Pack V29. Reemplazar **ambos** JAR anteriores en cliente y servidor; no instalarlos junto a los originales. Copiar el mundo antes de cambiarlo. Las versiones internas originales se conservan para no romper restricciones de otros addons.

## Tablero

En los seis tipos de carretera del Official Pack (Scout, Mercedes 230CE, Mustang 1969, GMC Brigadier, camión de bomberos y quad), el conductor ve un tablero compacto centrado abajo, encima de la barra de objetos, con dos relojes grandes de RPM/velocidad, combustible, temperatura, marcha, modo de caja, batería y estado del coche/motor. Se fija el panel completo y la visibilidad tanto en primera persona como en tercera/isométrica, sin depender de la opción de medio HUD. Las cabinas e instrumentos originales en 3D siguen disponibles; los aviones conservan sus HUD y controles.

## Caja de cambios

Los cinco motores de carretera incluyen seis relaciones hacia adelante, neutral y reversa. Al ampliar las cajas originales de 2–5 marchas se conservan la primera y la relación final, con pasos intermedios: no se inventa una séptima ni se exagera la velocidad máxima cambiando la relación final. Cambios en 12 ticks y puntos automáticos separados para reducir la alternancia de marchas.

- **Automático:** cambia solo según RPM y acelerador. Los motores automáticos originales mantienen ese modo inicial.
- **Manual:** con la mano principal vacía, clic izquierdo sube una marcha y clic derecho baja. Seleccionar una casilla vacía de la barra para vaciar la mano; la linterna en la mano secundaria puede quedarse.
- El HUD indica los controles y avisa si se intenta cambiar con un objeto equipado.
- Sólo puede hacerlo el conductor. El acompañante no cambia marchas. Los clics de menús no cambian; no se cuenta dos veces el clic derecho por la mano secundaria.
- Para alternar, usar el **Chequeador de motores**, clic derecho en el vehículo y botón «Cambiar caja: manual / automática». Se guarda el modo usando las variables persistentes originales de MTS y sus paquetes de red.
- Una intervención de ratón con prioridad alta consume los clics de cambio antes de los controles de apuntado/interacción isométrica. Se limita a conductor, caja manual y mano vacía; conserva el resto de interacciones.

Los sonidos nativos de caja se reproducen al activar las fases de embrague después de un cambio válido, en ambas direcciones, también en automático. Los intentos rechazados no disparan ese sonido. Los sonidos originales de cambios incorrectos se conservan.

## Chequeo y reparación

El Chequeador se fabrica con hierro, comparador y pepita de oro. Abre una pantalla pequeña con silueta del coche y condición en colores, carrocería, combustible, batería, motor instalado, estado encendido/apagado, marcha/modo, RPM, temperatura, aceite y horas de desgaste. La llave original sigue abriendo su editor de instrumentos: no se elimina esa función.

El **Kit de mantenimiento de motor** se fabrica con hierro, redstone y pepita de oro. Clic derecho en el motor o en el coche dirige el kit al motor más próximo al punto elegido. Repara hasta 100 HP y resetea el desgaste usando el sistema nativo. Se consume, respeta el cooldown original de reparación y puede reparar un motor averiado. Se recomienda parar y apagar el coche para hacer mantenimiento.

El kit de reparación original ahora repara **80 HP de carrocería**. Se corrigió la comparación que usaba el daño del vehículo en vez del daño del motor/parte: se limita la cantidad al daño real de la parte y se evita el daño negativo y la referencia a un vehículo inexistente al reparar una pieza suelta.

## Maletera y resistencia

Cada vehículo de carretera incluye una caja de almacenamiento trasera permanente de **54 espacios**, integrada como parte interactuable nativa de MTS. Clic derecho desde atrás, con el vehículo desbloqueado. No se puede retirar con la llave. Usa el inventario, guardado, peso y sincronización originales de MTS.

También se añade al cargar coches existentes si falta el nuevo slot. Se añade al final de la lista y se mantienen todos los índices originales: motores, ruedas, piezas, asientos y cargas antiguas no se reubican ni se sustituyen. La comprobación es idempotente y sólo crea inventario en el servidor.

- Carrocería: vida ×3.
- Grupos de colisión con vida finita: ×2.
- Motores de carretera con vida finita: ×2.
- Desgaste de motores de carretera: ×0,5.

Los coches siguen pudiendo averiarse y destruirse. No se ajustan daños de jugadores ni los vehículos de otros packs.

## Verificación y límites

Pruebas simuladas: mano principal vacía, pasajero/conductor, izquierda/derecha, modo automático/manual, aviso al llevar objetos, mano secundaria sin duplicación, clic antes de isométrica, soltar/repetir, menú/ventana, selector de caja y estado del motor; migración de maleteras sin reemplazar cargas, sin duplicarse ni crearse en cliente. Análisis del bytecode modificado y original, integridad ZIP y comprobación de definiciones/recetas/recursos del pack. Hashes en `validation.json`.

**No se ha ejecutado Minecraft en este entorno.** Falta validar la interfaz y la integración del conjunto de mods dentro del juego. Probar primero en copia del mundo: coche antiguo/nuevo, maletera con objetos y recarga, chequeador, kit con carrocería sana y motor dañado, modo manual con mano ocupada/vacía y cámara isométrica, automático acelerando, sonido de cambio y acompañante.

## Compilación

Java 21, Python 3, dependencias Python en `requirements.txt`, ECJ/ASM verificados por `tools.sha256.json`. Unir las tres partes 7z y extraer el JAR del pack. Pasar ambos JAR originales y las herramientas:

```bash
python -m pip install -r mts-zomboid-patch/requirements.txt
python mts-zomboid-patch/build.py /ruta/Immersive-Vehicles-original.jar /ruta/MTS-Official-Pack-original.jar /ruta/herramientas
```

El script normaliza los `00` numéricos originales únicamente en las definiciones que modifica y usa JSON5 para leer comentarios/comas finales. No reescribe aviones ni las demás definiciones. Opcionalmente admite una carpeta de definiciones ya parseadas antes del argumento de herramientas para ahorrar tiempo. La salida va a `mods/`; no se empaquetan las clases de prueba ni instaladores.
