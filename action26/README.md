# Global Melee v26: bodycam y activación de parkour

[Instalación y controles](GUIA.txt). Un solo Global Melee en clientes y servidor; protocolo `boxing26`.

La v25 omitía la bodycam continua y la entrada C del donante. Además, la velocidad que el servidor examinaba al recibir el deslizamiento podía haber descendido ya al agacharse. V26 registra una tecla configurable C, conserva también agacharse y retiene seis ticks de dirección y velocidad realmente medidas durante la carrera. Esa historia caduca y no permite fabricar impulso estando quieto.

`Bodycam26` recupera la respiración y oscilación al caminar/correr de `Bodycam20` del JAR enviado, con reducción al apuntar y composición con la cámara de combate. Solo modifica los ángulos del evento de cámara en primera persona; deja intactas las rotaciones de puntería del jugador y la guardia de tercera persona.

Se puede mantener salto frente a una repisa, además del doble toque anterior. La búsqueda cliente evita peticiones repetidas a paredes inválidas. `Mantle26` transmite el destino validado por el servidor mediante un payload exclusivo servidor→cliente. La recepción se encola en el hilo principal y el cliente aplica ese destino sin depender de un segundo reconocimiento de bloques o del caché de suelo anterior al salto. Se conservan altura máxima de dos bloques, colisiones, cancelación por dimensión/estado y separación de los mapas cliente/servidor.

La base y el donante permanecen fijados por SHA-256 en `inputs.json`. No se sustituyen las clases de combate por las del donante v20.

```sh
python action26/build.py
python action26/test.py
```

Las pruebas nuevas invocan los suscriptores reales `BoxingClientEvents16` y `KeyEvents26` del JAR producido, comprueban activación, bodycam, destino con retraso y recorrido con colisiones simuladas. La prueba de desaceleración al agacharse reproduce el rechazo de v25 como control negativo. Continúan pasando combate, poses, sincronización de caídas y compatibilidad con el JAR EMF 3.3.11 suministrado. Los stubs de compilación no se incluyen en el mod.

No se ha ejecutado Minecraft completo ni comprobado el aspecto final dentro del pack.
